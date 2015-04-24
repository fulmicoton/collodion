package com.fulmicoton.collodion.tokenizer;


import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.loader.ResourceLoader;
import junit.framework.Assert;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Test;

public class SolilessTokenizerTest {

    @Test
    public void testWithPunctuation() throws Exception {
        CollodionAnalyzer collodionAnalyzer = CollodionAnalyzer.fromPath(ResourceLoader.fromClass(SolilessTokenizerTest.class), "pipeline-empty.json");
        final TokenStream tokenStream = collodionAnalyzer.tokenStream("", "RESPONSABILITIES: Our client");
        tokenStream.reset();
        final CharTermAttribute term = tokenStream.getAttribute(CharTermAttribute.class);
        final TypeAttribute type = tokenStream.getAttribute(TypeAttribute.class);
        Assert.assertTrue(tokenStream.incrementToken());
        Assert.assertEquals(type.type(), "<ALPHANUM>");
        Assert.assertEquals(term.toString(), "RESPONSABILITIES");
        Assert.assertTrue(tokenStream.incrementToken());
        Assert.assertEquals(type.type(), "<JUNK>");
        Assert.assertEquals(term.toString(), ": ");
        Assert.assertTrue(tokenStream.incrementToken());
        Assert.assertEquals(type.type(), "<ALPHANUM>");
        Assert.assertEquals(term.toString(), "Our");
        Assert.assertTrue(tokenStream.incrementToken());
        Assert.assertEquals(type.type(), "<JUNK>");
        Assert.assertEquals(term.toString(), " ");
        Assert.assertTrue(tokenStream.incrementToken());
        Assert.assertEquals(type.type(), "<ALPHANUM>");
        Assert.assertEquals(term.toString(), "client");
        Assert.assertFalse(tokenStream.incrementToken());
    }

}
