package com.fulmicoton.collodion.server;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.AnalysisExecutor;
import com.fulmicoton.collodion.common.loader.ChainLoader;
import com.fulmicoton.collodion.common.loader.DirectoryLoader;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.corpus.Corpus;
import com.fulmicoton.collodion.corpus.CorpusAndAnalyzer;
import com.fulmicoton.collodion.corpus.SimpleCorpus;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class CollodionApplication {

    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    private static CollodionApplication APPLICATION;

    private final Loader loader;
    private final CorpusAndAnalyzer corpusAndAnalyzer;
    private final AnalysisExecutor executor;

    public static synchronized CollodionApplication get() {
        return APPLICATION;
    }

    private final LoadingCache<QueryCorpus.Key, Corpus> queryCache;

    public static synchronized void setProjectPath(final File projectDirectory) {
        final Loader loader = ChainLoader.of(
            DirectoryLoader.forRoot(projectDirectory),
            Loader.DEFAULT_LOADER
        );
        load(loader);
    }

    private static void load(final Loader loader) {
        CollodionApplication.APPLICATION = new CollodionApplication(loader);
    }

    public static void reload() {
        load(get().loader);
    }

    CollodionApplication(final Loader loader) {
        this.loader = loader;
        try {
            final Corpus corpus = SimpleCorpus.fromPath("corpus.json", loader);
            final CollodionAnalyzer analyzer = CollodionAnalyzer.fromPath("pipeline.json", loader);
            this.corpusAndAnalyzer = new CorpusAndAnalyzer(corpus, analyzer);
            this.executor = new AnalysisExecutor(
                    this.corpusAndAnalyzer.analyzer,
                    NUM_THREADS
            );
            final CacheLoader<QueryCorpus.Key, Corpus> cacheLoader = new QueryCorpus(this.executor);
            this.queryCache = CacheBuilder
                    .newBuilder()
                    .maximumSize(20)
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .build(cacheLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AnalysisExecutor executor() {
        return this.executor;
    }

    public CorpusAndAnalyzer getCorpusAndAnalyzer(final String query) {
        if (query == null) {
            return this.corpusAndAnalyzer;
        }
        else {
            try {
                final QueryCorpus.Key key = new QueryCorpus.Key(this.corpusAndAnalyzer.corpus,
                                                                this.corpusAndAnalyzer.analyzer,
                                                                query);
                final Corpus corpus = this.queryCache.get(key);
                return new CorpusAndAnalyzer(corpus, this.corpusAndAnalyzer.analyzer);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
