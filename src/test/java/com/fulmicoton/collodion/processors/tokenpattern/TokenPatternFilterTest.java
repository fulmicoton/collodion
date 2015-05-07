package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.processors.CollodionAnalyzerTest;
import com.fulmicoton.collodion.processors.stemmer.StemAttribute;
import com.fulmicoton.collodion.common.AnnotationAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Test;

public class TokenPatternFilterTest {


    @Test
    public void testTokenPattern() throws Exception {
        final CollodionAnalyzer collodionAnalyzer = CollodionAnalyzerTest.loadPipeline("tokenpatterntest-country-pipeline.json");
        final TokenStream tokenStream = collodionAnalyzer.tokenStream("", "Robert lives in France. Lily lives in the UK");
        tokenStream.reset();
        CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
        AnnotationAttribute vocabularyAnnotation = tokenStream.getAttribute(AnnotationAttribute.class);
        StemAttribute stem = tokenStream.getAttribute(StemAttribute.class);
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "Robert");
            Assert.assertEquals(stem.toString(), "Robert");
            Assert.assertEquals(vocabularyAnnotation.toString(), "pattern0.0; pattern0.1");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "lives");
            Assert.assertEquals(stem.toString(), "live");
            Assert.assertEquals(vocabularyAnnotation.toString(), "LIVE; pattern0.2");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "in");
            Assert.assertEquals(stem.toString(), "in");
            Assert.assertEquals(vocabularyAnnotation.toString(), "");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "France");
            Assert.assertEquals(stem.toString(), "Franc");
            Assert.assertEquals(vocabularyAnnotation.toString(), "COUNTRY");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "Lily");
            Assert.assertEquals(stem.toString(), "Lili");
            Assert.assertEquals(vocabularyAnnotation.toString(), "pattern0.0; pattern0.1");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "lives");
            Assert.assertEquals(stem.toString(), "live");
            Assert.assertEquals(vocabularyAnnotation.toString(), "LIVE; pattern0.2");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "in");
            Assert.assertEquals(stem.toString(), "in");
            Assert.assertEquals(vocabularyAnnotation.toString(), "");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "the");
            Assert.assertEquals(stem.toString(), "the");
            Assert.assertEquals(vocabularyAnnotation.toString(), "");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "UK");
            Assert.assertEquals(stem.toString(), "UK");
            Assert.assertEquals(vocabularyAnnotation.toString(), "COUNTRY");
        }
    }
}
