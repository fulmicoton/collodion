package com.fulmicoton.collodion.server.tasks;


import com.fulmicoton.collodion.CollodionAnalyzer;
import org.apache.lucene.analysis.TokenStream;

public abstract class AnalysisTask<T> {

    private final CollodionAnalyzer analyzer;

    protected AnalysisTask(CollodionAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public final CollodionAnalyzer getAnalyzer() {
        return this.analyzer;
    }

    public abstract T process(TokenStream tokenStream) throws Exception;
}
