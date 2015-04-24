package com.fulmicoton.collodion.tokenizer;


import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.loader.ResourceLoader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Test;

public class WithPunctuationTokenizerTest {

    @Test
    public void testWithPunctuation() throws Exception {
        CollodionAnalyzer collodionAnalyzer = CollodionAnalyzer.fromPath(ResourceLoader.fromClass(WithPunctuationTokenizerTest.class), "pipeline-empty.json");
        final TokenStream tokenStream = collodionAnalyzer.tokenStream("", "RESPONSABILITIES: Our client");
        tokenStream.reset();
        final CharTermAttribute term = tokenStream.getAttribute(CharTermAttribute.class);
        final TypeAttribute type = tokenStream.getAttribute(TypeAttribute.class);
        while (tokenStream.incrementToken()) {
            System.out.println(type);
        }

    }

}
