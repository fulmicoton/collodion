package com.fulmicoton.semantic;


import com.google.common.io.Files;
import com.fulmicoton.JSON;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

public class SemanticAnalyzer extends Analyzer {

    public final List<ProcessorBuilder> processorBuilders;

    public SemanticAnalyzer(List<ProcessorBuilder> processorBuilders) {
        this.processorBuilders = processorBuilders;
    }

    private static SemanticAnalyzer loadFromFile(File configuration) throws FileNotFoundException {
        return JSON.GSON.fromJson(new FileReader(configuration), SemanticAnalyzer.class);
    }

    public void init() throws Exception {
        for (ProcessorBuilder processorBuilder: this.processorBuilders) {
            processorBuilder.init();
        }
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final Tokenizer source = new StandardTokenizer(reader);
        TokenStream lastFilter = source;
        for (ProcessorBuilder processorBuilder: this.processorBuilders) {
            try {
                lastFilter = processorBuilder.createFilter(lastFilter);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new TokenStreamComponents(source, lastFilter);
    }

    public static SemanticAnalyzer fromFile(final File inputFile) throws IOException {
        final String json = Files.toString(inputFile, Charset.forName("utf-8"));
        return fromJSON(json);
    }


    public static SemanticAnalyzer fromJSON(String json) {
        return JSON.GSON.fromJson(json, SemanticAnalyzer.class);
    }

    public String toJSON() {
        return JSON.GSON.toJson(this, SemanticAnalyzer.class);
    }

}
