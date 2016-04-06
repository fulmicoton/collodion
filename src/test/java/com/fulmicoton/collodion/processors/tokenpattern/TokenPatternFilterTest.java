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
    public void testTokenPatternEmpty() throws Exception {
        final CollodionAnalyzer collodionAnalyzer = CollodionAnalyzerTest.loadPipeline("tokenpatterntest-country-pipeline.json");
        final TokenStream tokenStream = collodionAnalyzer.tokenStream("", "");
        tokenStream.reset();
        Assert.assertFalse(tokenStream.incrementToken());
    }

    @Test
    public void testTokenPattern() throws Exception {
        final CollodionAnalyzer collodionAnalyzer = CollodionAnalyzerTest.loadPipeline("tokenpatterntest-country-pipeline.json");
        final TokenStream tokenStream = collodionAnalyzer.tokenStream("", "Robert lives in France. Lily lives in the UK");
        tokenStream.reset();
        final CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
        final AnnotationAttribute annotationAttribute = tokenStream.getAttribute(AnnotationAttribute.class);
        final StemAttribute stem = tokenStream.getAttribute(StemAttribute.class);
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals("Robert", charTerm.toString());
            Assert.assertEquals("robert", stem.toString());
            Assert.assertEquals("LIVEPTN(2); LIVEPTN.NAME(1)", annotationAttribute.toString());
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals("lives", charTerm.toString());
            Assert.assertEquals("live", stem.toString());
            Assert.assertEquals("LIVE(1); LIVEVB(1)", annotationAttribute.toString());
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals("in", charTerm.toString());
            Assert.assertEquals("in", stem.toString());
            Assert.assertEquals("", annotationAttribute.toString());
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals("France", charTerm.toString());
            Assert.assertEquals("franc", stem.toString());
            Assert.assertEquals("COUNTRY(1)", annotationAttribute.toString());
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals("Lily", charTerm.toString());
            Assert.assertEquals("lili", stem.toString());
            Assert.assertEquals("LIVEPTN(2); LIVEPTN.NAME(1)", annotationAttribute.toString());
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "lives");
            Assert.assertEquals(stem.toString(), "live");
            Assert.assertEquals(annotationAttribute.toString(), "LIVE(1); LIVEVB(1)");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "in");
            Assert.assertEquals(stem.toString(), "in");
            Assert.assertEquals(annotationAttribute.toString(), "");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "the");
            Assert.assertEquals(stem.toString(), "the");
            Assert.assertEquals(annotationAttribute.toString(), "");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "UK");
            Assert.assertEquals(stem.toString(), "uk");
            Assert.assertEquals(annotationAttribute.toString(), "COUNTRY(1)");
        }
    }
}
