package com.fulmicoton.processors;

import com.fulmicoton.SemanticAnalyzer;
import com.fulmicoton.common.Utils;
import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;

import java.io.IOException;

public class NumberParserTest {


    final SemanticAnalyzer semanticAnalyzer;

    public NumberParserTest() throws Exception {
        this.semanticAnalyzer = SemanticAnalyzerTest.loadPipeline("pipeline-numberparser.yaml");
    }

    public TokenStream testProcess(final String text) throws IOException {
        final TokenStream tokenStream = this.semanticAnalyzer.tokenStream("", text);
        tokenStream.reset();
        return tokenStream;
    }


    @Test
    public void testNumberParser() throws IOException {
        final TokenStream tokenStream = testProcess("10 000 francs la R5");
        System.out.println(Utils.toJson(tokenStream));
    }


}
