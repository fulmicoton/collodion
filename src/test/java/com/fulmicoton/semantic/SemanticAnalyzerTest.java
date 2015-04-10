package com.fulmicoton.semantic;

import com.fulmicoton.common.loader.Loader;
import com.fulmicoton.common.loader.ResourceLoader;
import com.fulmicoton.semantic.stemmer.StemAttribute;
import com.fulmicoton.semantic.vocabularymatcher.VocabularyAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
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
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "The");
            Assert.assertEquals(stem.toString(), "The");
            Assert.assertEquals(vocabularyAnnotation.toString(), "");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "baker");
            Assert.assertEquals(stem.toString(), "baker");
            Assert.assertEquals(vocabularyAnnotation.toString(), "containsa");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "loves");
            Assert.assertEquals(stem.toString(), "love");
            Assert.assertEquals(vocabularyAnnotation.toString(), "");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "bread");
            Assert.assertEquals(stem.toString(), "bread");
            Assert.assertEquals(vocabularyAnnotation.toString(), "containsa");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "and");
            Assert.assertEquals(stem.toString(), "and");
            Assert.assertEquals(vocabularyAnnotation.toString(), "containsa");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "jambon");
            Assert.assertEquals(stem.toString(), "jambon");
            Assert.assertEquals(vocabularyAnnotation.toString(), "jambon; containsa");
        }
        Assert.assertFalse(tokenStream.incrementToken());
    }



    @Test
    public void testTokenPattern() throws Exception {
        final SemanticAnalyzer semanticAnalyzer = loadPipeline("tokenpatterntest-country-pipeline.json");
        final TokenStream tokenStream = semanticAnalyzer.tokenStream("", "aaaa I live in UK for work.");
        tokenStream.reset();
        CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
        VocabularyAttribute vocabularyAnnotation = tokenStream.getAttribute(VocabularyAttribute.class);
        StemAttribute stem = tokenStream.getAttribute(StemAttribute.class);
        while (tokenStream.incrementToken()) {

        }
        /*
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "The");
            Assert.assertEquals(stem.toString(), "The");
            Assert.assertEquals(vocabularyAnnotation.toString(), "");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "baker");
            Assert.assertEquals(stem.toString(), "baker");
            Assert.assertEquals(vocabularyAnnotation.toString(), "containsa");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "loves");
            Assert.assertEquals(stem.toString(), "love");
            Assert.assertEquals(vocabularyAnnotation.toString(), "");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "bread");
            Assert.assertEquals(stem.toString(), "bread");
            Assert.assertEquals(vocabularyAnnotation.toString(), "containsa");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "and");
            Assert.assertEquals(stem.toString(), "and");
            Assert.assertEquals(vocabularyAnnotation.toString(), "containsa");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "jambon");
            Assert.assertEquals(stem.toString(), "jambon");
            Assert.assertEquals(vocabularyAnnotation.toString(), "jambon; containsa");
        }
        Assert.assertFalse(tokenStream.incrementToken());
        */
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
