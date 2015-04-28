package com.fulmicoton.collodion.server;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.AnalysisExecutor;
import com.fulmicoton.collodion.corpus.Corpus;
import com.fulmicoton.collodion.corpus.FilteredCorpus;
import com.fulmicoton.collodion.corpus.SimpleCorpus;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.lucene.document.Document;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public enum Application {

    INSTANCE;

    public AnalysisExecutor executor;

    public static Application get() {
        return INSTANCE;
    }

    private final Corpus corpus;
    private final CollodionAnalyzer analyzer;
    private final LoadingCache<String, Corpus> queryCache;


    Application() {
        try {
            this.corpus = SimpleCorpus.fromPath("US.json");
            this.analyzer = CollodionAnalyzer.fromPath("pipeline-benchmark.json");
            this.executor = new AnalysisExecutor(this.analyzer, Runtime.getRuntime().availableProcessors());
            final Corpus corpus = this.corpus;
            this.queryCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(new QueryCorpus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AnalysisExecutor executor() {
        return this.executor;
    }

    private class QueryCorpus extends CacheLoader<String, Corpus> {

        @Override
        public Corpus load(String query) throws Exception {
            return FilteredCorpus.filter(corpus, this.getMatchQueryPredicate(query));
        }

        private Predicate<Document> getMatchQueryPredicate(final String query) {
            //CollodionAnalyzer extendedAnalyzer = analyzer.append();
            return null;
        }
    }



    public Corpus getCorpus(String query) throws ExecutionException {
        if (query == null) {
            return this.corpus;
        }
        else {
            return this.queryCache.get(query);
        }
    }




}
