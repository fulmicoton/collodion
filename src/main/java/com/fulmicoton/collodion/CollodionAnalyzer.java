package com.fulmicoton.collodion;


import com.fulmicoton.collodion.common.JSON;
import com.fulmicoton.collodion.common.loader.ChainLoader;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.google.common.collect.ImmutableList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

public class CollodionAnalyzer extends Analyzer {

    private static final Charset UTF8 = Charset.forName("utf-8");

    private transient Loader loader;

    public final List<ProcessorBuilder> processorBuilders;

    public ImmutableList<ProcessorBuilder> processorBuilders() {
        return ImmutableList.copyOf(this.processorBuilders);
    }

    public CollodionAnalyzer(final List<ProcessorBuilder> processorBuilders) {
        this(processorBuilders, Loader.DEFAULT_LOADER);
    }

    public CollodionAnalyzer(final List<ProcessorBuilder> processorBuilders,
                             final Loader loader) {
        this.processorBuilders = processorBuilders;
        this.loader = loader;
    }

    public void prependLoader(Loader loader) {
        this.loader = ChainLoader.of(loader, this.loader);
    }

    public void init() throws Exception {
        for (ProcessorBuilder processorBuilder: this.processorBuilders) {
            processorBuilder.init(this.loader);
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

    public static CollodionAnalyzer fromFile(final File inputFile) throws IOException {
        return fromStream(new FileInputStream(inputFile));
    }

    public static CollodionAnalyzer fromPath(final Loader loader, final String path) throws IOException {
        final CollodionAnalyzer collodionAnalyzer = loader.readObject(path, CollodionAnalyzer.class);
        collodionAnalyzer.prependLoader(loader);
        return collodionAnalyzer;
    }

    public static CollodionAnalyzer fromPath(final String path) throws IOException {
        return fromPath(Loader.DEFAULT_LOADER, path);
    }

    public static CollodionAnalyzer fromStream(final InputStream inputStream) {
        return JSON.fromJson(new InputStreamReader(inputStream, UTF8), CollodionAnalyzer.class);
    }

    public String toJSON() {
        return JSON.toJson(this);
    }

}