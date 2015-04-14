package com.fulmicoton.collodion.processors;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.JSON;
import com.fulmicoton.collodion.common.Utils;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.common.loader.ResourceLoader;
import com.fulmicoton.collodion.processors.stemmer.StemAttribute;
import com.fulmicoton.collodion.processors.vocabularymatcher.VocabularyAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Test;

public class CollodionAnalyzerTest {

    private static final Loader RESOURCE_LOADER = ResourceLoader.fromClass(CollodionAnalyzerTest.class);

    public static CollodionAnalyzer loadPipeline(final String pipelineName) throws Exception {
        CollodionAnalyzer analyzer = CollodionAnalyzer.fromPath(RESOURCE_LOADER, pipelineName);
        analyzer.init();
        return analyzer;
    }

    @Test
    public void testSemanticAnalyzerFromYaml() throws Exception {
        final CollodionAnalyzer collodionAnalyzer = loadPipeline("pipeline-yaml.yaml");

    }

    @Test
    public void testSemanticAnalyzer() throws Exception {
        final CollodionAnalyzer collodionAnalyzer = loadPipeline("pipeline.json");
        final TokenStream tokenStream = collodionAnalyzer.tokenStream("", "The baker loves bread and jambon.");
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
        final CollodionAnalyzer collodionAnalyzer = loadPipeline("tokenpatterntest-country-pipeline.json");
        final TokenStream tokenStream = collodionAnalyzer.tokenStream("", "aaaa I live in UK for work.");
        tokenStream.reset();
        CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
        VocabularyAttribute vocabularyAnnotation = tokenStream.getAttribute(VocabularyAttribute.class);
        StemAttribute stem = tokenStream.getAttribute(StemAttribute.class);

        System.out.println(JSON.toJson(Utils.toJson(tokenStream)));
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
