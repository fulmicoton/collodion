package com.fulmicoton.collodion.common;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalysisExecutor {

    private final ExecutorService executorService;
    private final Analyzer analyzer;

    public AnalysisExecutor(final Analyzer analyzer, final int nbThreads) {
        this.executorService = Executors.newFixedThreadPool(nbThreads);
        this.analyzer = analyzer;
    }

    public static interface AnalysisTask<T> {
        public T process(final TokenStream tokenStream) throws Exception;
    }


    public static class AnalysisCallable<T> implements Callable<T> {

        private final Reader reader;
        private final String fieldName;
        private final Analyzer analyzer;
        private final AnalysisTask<T> analysisTask;

        public AnalysisCallable(Reader reader,
                                String fieldName,
                                Analyzer analyzer,
                                AnalysisTask<T> analysisTask) {
            this.reader = reader;
            this.fieldName = fieldName;
            this.analyzer = analyzer;
            this.analysisTask = analysisTask;
        }

        @Override
        public T call() throws Exception {
            try (TokenStream tokenStream = analyzer.tokenStream(this.fieldName, this.reader)) {
                tokenStream.reset();
                return analysisTask.process(tokenStream);
            }
        }
    }

    public <T> T call( final String fieldName, final String str, final AnalysisTask<T> task) throws Exception {
        return call(fieldName, new StringReader(str), task);
    }

    public <T> T call( final String fieldName, final Reader reader, final AnalysisTask<T> task) throws Exception {
        final AnalysisCallable<T> callable = new AnalysisCallable<>(reader, fieldName, this.analyzer, task);
        return this.executorService.submit(callable).get();
    }


}
