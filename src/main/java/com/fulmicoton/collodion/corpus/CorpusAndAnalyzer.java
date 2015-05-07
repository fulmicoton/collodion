package com.fulmicoton.collodion.corpus;

import com.fulmicoton.collodion.CollodionAnalyzer;

public class CorpusAndAnalyzer {

    public final Corpus corpus;
    public final CollodionAnalyzer analyzer;

    public CorpusAndAnalyzer(final Corpus corpus,
                             final CollodionAnalyzer analyzer) {
        this.corpus = corpus;
        this.analyzer = analyzer;
    }
}
