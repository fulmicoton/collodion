package com.fulmicoton.semantic;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        final File jsonFile = new File("/home/paul/github/semantic-studio/test/pipeline.json");
        final SemanticAnalyzer analyzer = SemanticAnalyzer.fromFile(jsonFile);
        analyzer.init();
        final TokenStream tokenStream = analyzer.tokenStream("", "The baker loves bread and jambon.");
        tokenStream.reset();
        CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
        VocabularyAttribute vocabularyAnnotation = tokenStream.getAttribute(VocabularyAttribute.class);
        StemAttribute stem = tokenStream.getAttribute(StemAttribute.class);
        while (tokenStream.incrementToken()) {
            System.out.println(charTerm.toString() + "-" + stem.toString() + "-" + vocabularyAnnotation.toString()) ;
        }

        System.out.println(analyzer.toJSON());

    }
}
