package com.fulmicoton.semantic;

import com.fulmicoton.utils.loader.Loader;
import com.fulmicoton.utils.loader.ResourceLoader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

public class SemanticAnalyzerTest {

    private static final Loader RESOURCE_LOADER = ResourceLoader.fromClass(SemanticAnalyzerTest.class);

    private SemanticAnalyzer loadPipeline(final String pipelineName) throws Exception {
        SemanticAnalyzer analyzer = SemanticAnalyzer.fromPath(RESOURCE_LOADER, pipelineName);
        analyzer.init();
        return analyzer;
    }

    @Test
    public void testSemanticAnalyzer() throws Exception {
        final SemanticAnalyzer semanticAnalyzer = loadPipeline("pipeline.json");
        final TokenStream tokenStream = semanticAnalyzer.tokenStream("", "The baker loves bread and jambon.");
        tokenStream.reset();
        CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
        VocabularyAttribute vocabularyAnnotation = tokenStream.getAttribute(VocabularyAttribute.class);
        StemAttribute stem = tokenStream.getAttribute(StemAttribute.class);
        while (tokenStream.incrementToken()) {
            System.out.println(charTerm.toString() + "-" + stem.toString() + "-" + vocabularyAnnotation.toString()) ;
        }
    }


    /*
    final TokenStream tokenStream = analyzer.tokenStream("", "The baker loves bread and jambon.");
    tokenStream.reset();
    CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
    VocabularyAttribute vocabularyAnnotation = tokenStream.getAttribute(VocabularyAttribute.class);
    StemAttribute stem = tokenStream.getAttribute(StemAttribute.class);
    while (tokenStream.incrementToken()) {
        System.out.println(charTerm.toString() + "-" + stem.toString() + "-" + vocabularyAnnotation.toString()) ;
    }
    System.out.println(analyzer.toJSON());
    */
}
