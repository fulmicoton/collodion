package com.fulmicoton.processors;

import com.fulmicoton.SemanticAnalyzer;
import com.fulmicoton.common.Utils;
import com.fulmicoton.processors.numberparser.NumberAttribute;
import junit.framework.Assert;
import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;

import java.io.IOException;

public class NumberParserTest {


    final SemanticAnalyzer semanticAnalyzer;

    public NumberParserTest() throws Exception {
        this.semanticAnalyzer = SemanticAnalyzerTest.loadPipeline("pipeline-numberparser.yaml");
    }


    public void numberParserTestHelper(final String text, double val) throws IOException {
        final TokenStream tokenStream = this.semanticAnalyzer.tokenStream("", text);
        tokenStream.reset();
        final NumberAttribute numberAttribute = tokenStream.getAttribute(NumberAttribute.class);
        {
            Assert.assertTrue(tokenStream.incrementToken());
            Assert.assertEquals(numberAttribute.val(), val);
        }
        {
            Assert.assertTrue(tokenStream.incrementToken());
        }
        {
            Assert.assertFalse(tokenStream.incrementToken());
        }
        tokenStream.close();
    }

    @Test
    public void testNumberParser() throws IOException {
        this.numberParserTestHelper("10000 francs", 10000);
        this.numberParserTestHelper("10,000 francs", 10000);
        this.numberParserTestHelper("10.000,00 francs", 10000);
        this.numberParserTestHelper("10,000.00 francs", 10000);
    }


}
