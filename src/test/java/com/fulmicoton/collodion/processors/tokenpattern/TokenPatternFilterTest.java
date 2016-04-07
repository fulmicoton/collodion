package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.AnnotationAttribute;
import com.fulmicoton.collodion.processors.CollodionAnalyzerTest;
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

    public void testHelper(final String pipelineConf,
                           final String text,
                           final String... tokenStrs) throws Exception {
        final CollodionAnalyzer collodionAnalyzer = CollodionAnalyzerTest.loadPipeline(pipelineConf);
        final TokenStream tokenStream = collodionAnalyzer.tokenStream("", text);
        tokenStream.reset();
        int i = 0;
        final CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
        final AnnotationAttribute annotationAttribute = tokenStream.getAttribute(AnnotationAttribute.class);
        while (tokenStream.incrementToken()) {
            if (i >= tokenStrs.length) {
                throw new AssertionError("Token stream is longer than expected.");
            }
            final String tokenStr = charTerm.toString() + ":" + annotationAttribute.toString();
            Assert.assertEquals(tokenStrs[i], tokenStr);
            i += 1;
        }
        if (i < tokenStrs.length) {
            throw new AssertionError("Token stream is shorted than expected.");
        }
    }

    @Test
    public void testTokenPattern() throws Exception {
        // no trick
        testHelper(
                "tokenpatterntest-country-pipeline.json",
                "Robert lives in UK. Something",
                "Robert:LIVEPTN(4); LIVEPTN.NAME(1)",
                "lives:LIVE(1); LIVEVB(1)",
                "in:IN(1)",
                "UK:COUNTRY(1); LIVECOUNTRY(1)",
                "Something:"
        );
        // search
        testHelper(
                "tokenpatterntest-country-pipeline.json",
                "Because Because Robert lives in UK.",
                "Because:",
                "Because:",
                "Robert:LIVEPTN(4); LIVEPTN.NAME(1)",
                "lives:LIVE(1); LIVEVB(1)",
                "in:IN(1)",
                "UK:COUNTRY(1); LIVECOUNTRY(1)"
        );
        // chaining
        testHelper(
                "tokenpatterntest-country-pipeline.json",
                "Because Robert lives in France. Lily lives in a house.",
                "Because:",
                "Robert:LIVEPTN(4); LIVEPTN.NAME(1)",
                "lives:LIVE(1); LIVEVB(1)",
                "in:IN(1)",
                "France:COUNTRY(1); LIVECOUNTRY(1)",
                "Lily:LIVEPTN(2); LIVEPTN.NAME(1)",
                "lives:LIVE(1); LIVEVB(1)",
                "in:IN(1)",
                "a:",
                "house:"
        );
        testHelper(
                "tokenpatterntest-country-pipeline.json",
                "United Kingdom is in Europe.",
                "United:COUNTRY(2); A(5); COUNTRY.NAME(2)",
                "Kingdom:",
                "is:IS(1)",
                "in:IN(1)",
                "Europe:CONTINENT(1); COUNTRY.CONTINENT(1)"
        );
        // annotation with more than one token
        testHelper(
                "tokenpatterntest-country-pipeline.json",
                "Robert lives in United Kingdom.",
                "Robert:LIVEPTN(5); LIVEPTN.NAME(1)",
                "lives:LIVE(1); LIVEVB(1)",
                "in:IN(1)",
                "United:COUNTRY(2); LIVECOUNTRY(2)",
                "Kingdom:"
        );
    }


}
