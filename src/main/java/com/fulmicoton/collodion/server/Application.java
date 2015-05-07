package com.fulmicoton.collodion.server;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.AnalysisExecutor;
import com.fulmicoton.collodion.corpus.Corpus;
import com.fulmicoton.collodion.corpus.CorpusAndAnalyzer;
import com.fulmicoton.collodion.corpus.SimpleCorpus;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public enum Application {

    INSTANCE;

    public AnalysisExecutor executor;

    public static Application get() {
        return INSTANCE;
    }

    private final CorpusAndAnalyzer corpusAndAnalyzer;

    private final LoadingCache<QueryCorpus.Key, Corpus> queryCache;

    Application() {
        try {
            final Corpus corpus = SimpleCorpus.fromPath("US.json");
            final CollodionAnalyzer analyzer = CollodionAnalyzer.fromPath("pipeline-benchmark.json");
            this.corpusAndAnalyzer = new CorpusAndAnalyzer(corpus, analyzer);
            this.executor = new AnalysisExecutor(this.corpusAndAnalyzer.analyzer, Runtime.getRuntime().availableProcessors());
            final CacheLoader<QueryCorpus.Key, Corpus> cacheLoader = new QueryCorpus(this.executor);
            this.queryCache = CacheBuilder.newBuilder()
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
