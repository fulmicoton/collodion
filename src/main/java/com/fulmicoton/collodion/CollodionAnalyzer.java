package com.fulmicoton.collodion;


import com.fulmicoton.collodion.common.JSON;
import com.fulmicoton.collodion.common.loader.ChainLoader;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.fulmicoton.collodion.processors.resetannotation.ResetAnnotationFilter;
import com.fulmicoton.collodion.tokenizer.SolilessTokenizer;
import com.google.common.collect.ImmutableList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

public class CollodionAnalyzer extends Analyzer {

    private static final Charset UTF8 = Charset.forName("utf-8");

    private transient Loader loader;

    public final ImmutableList<ProcessorBuilder> processorBuilders;

    public CollodionAnalyzer(final List<ProcessorBuilder> processorBuilders) {
        this(processorBuilders, Loader.DEFAULT_LOADER);
    }

    public CollodionAnalyzer(final List<ProcessorBuilder> processorBuilders,
                             final Loader loader) {
        this.processorBuilders = ImmutableList.copyOf(processorBuilders);
        this.loader = loader;
    }

    public CollodionAnalyzer append(final ProcessorBuilder newProcessorBuilder) {
        final ImmutableList<ProcessorBuilder> newProcessorBuilders = ImmutableList
                .<ProcessorBuilder>builder()
                .addAll(this.processorBuilders)
                .add(newProcessorBuilder)
                .build();
        return new CollodionAnalyzer(newProcessorBuilders, this.loader);
    }

    public void prependLoader(Loader loader) {
        this.loader = ChainLoader.of(loader, this.loader);
    }

    private void init() throws Exception {
        for (ProcessorBuilder processorBuilder: this.processorBuilders) {
            processorBuilder.init(this.loader);
        }
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final Tokenizer source = new SolilessTokenizer(new StandardTokenizer(reader), reader);
        TokenStream lastFilter = new ResetAnnotationFilter(source);
        for (ProcessorBuilder processorBuilder: this.processorBuilders) {
            try {
                lastFilter = processorBuilder.createFilter(lastFilter);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new TokenStreamComponents(source, lastFilter);
    }

    public static CollodionAnalyzer fromPath(final String path, final Loader loader) throws Exception {
        final CollodionAnalyzer collodionAnalyzer = loader.readObject(path, CollodionAnalyzer.class);
        collodionAnalyzer.prependLoader(loader);
        collodionAnalyzer.init();
        return collodionAnalyzer;
    }

    public static CollodionAnalyzer fromPath(final String path) throws Exception {
        return fromPath(path, Loader.DEFAULT_LOADER);
    }

    public static CollodionAnalyzer fromStream(final InputStream inputStream) throws Exception {
        final Reader reader = new InputStreamReader(inputStream, UTF8);
        final CollodionAnalyzer collodionAnalyzer = JSON.fromJson(reader, CollodionAnalyzer.class);
        collodionAnalyzer.init();
        return collodionAnalyzer;
    }

    public String toJSON() {
        return JSON.toJson(this);
    }

}
