package com.fulmicoton.collodion.server;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.corpus.Corpus;
import com.fulmicoton.collodion.corpus.SimpleCorpus;


public enum Application {

    INSTANCE;

    public static Application get() {
        return INSTANCE;
    }

    private Corpus corpus;
    private CollodionAnalyzer analyzer;

    Application() {
        try {
            this.corpus = SimpleCorpus.fromPath("US.json");
            this.analyzer = CollodionAnalyzer.fromPath("pipeline-benchmark.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CollodionAnalyzer getAnalyzer() {
        return this.analyzer;
    }
    public Corpus getCorpus() {
        return this.corpus;
    }

    public int getNbDocuments() {
        return this.corpus.size();
    }


}
