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
        final CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
        final AnnotationAttribute annotationAttribute = tokenStream.getAttribute(AnnotationAttribute.class);
        final StemAttribute stem = tokenStream.getAttribute(StemAttribute.class);
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals("Robert", charTerm.toString());
            Assert.assertEquals("Robert", stem.toString());
            Assert.assertEquals("LIVEPTN; LIVEPTN.NAME", annotationAttribute.toString());
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals("lives", charTerm.toString());
            Assert.assertEquals("live", stem.toString());
            Assert.assertEquals("LIVE", annotationAttribute.toString());
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
            Assert.assertEquals("Franc", stem.toString());
            Assert.assertEquals("COUNTRY", annotationAttribute.toString());
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "Lily");
            Assert.assertEquals(stem.toString(), "Lili");
            Assert.assertEquals(annotationAttribute.toString(), "LIVEPTN; LIVEPTN.NAME");
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(charTerm.toString(), "lives");
            Assert.assertEquals(stem.toString(), "live");
            Assert.assertEquals(annotationAttribute.toString(), "LIVE");
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
            Assert.assertEquals(stem.toString(), "UK");
            Assert.assertEquals(annotationAttribute.toString(), "COUNTRY");
        }
    }
}
