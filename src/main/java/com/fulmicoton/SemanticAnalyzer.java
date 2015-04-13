package com.fulmicoton;


import com.fulmicoton.common.JSON;
import com.fulmicoton.common.loader.ChainLoader;
import com.fulmicoton.common.loader.DirectoryLoader;
import com.fulmicoton.common.loader.Loader;
import com.fulmicoton.common.loader.ResourceLoader;
import com.fulmicoton.processors.ProcessorBuilder;
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

public class SemanticAnalyzer extends Analyzer {

    private static final Charset UTF8 = Charset.forName("utf-8");

    public static final Loader DEFAULT_LOADER = ChainLoader.of(
        DirectoryLoader.forRoot(new File("/")),
        DirectoryLoader.forRoot(new File(".")),
        ResourceLoader.fromClass(SemanticAnalyzer.class)
    );

    private transient Loader loader = DEFAULT_LOADER;

    public final List<ProcessorBuilder> processorBuilders;


    public SemanticAnalyzer(final List<ProcessorBuilder> processorBuilders) {
        this(processorBuilders, DEFAULT_LOADER);
    }

    public void prependLoader(Loader loader) {
        this.loader = ChainLoader.of(loader, this.loader);
    }

    public SemanticAnalyzer(final List<ProcessorBuilder> processorBuilders,
                            final Loader loader) {
        this.processorBuilders = processorBuilders;
        this.loader = loader;
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

    public static SemanticAnalyzer fromFile(final File inputFile) throws IOException {
        return fromStream(new FileInputStream(inputFile));
    }

    public static SemanticAnalyzer fromPath(final Loader loader, final String path) {
        final SemanticAnalyzer semanticAnalyzer = loader.readObject(path, SemanticAnalyzer.class);
        semanticAnalyzer.prependLoader(loader);
        return semanticAnalyzer;
    }

    public static SemanticAnalyzer fromPath(final String path) {
        return fromPath(DEFAULT_LOADER, path);
    }

    public static SemanticAnalyzer fromStream(final InputStream inputStream) {
        return JSON.GSON.fromJson(new InputStreamReader(inputStream, UTF8), SemanticAnalyzer.class);
    }

    public String toJSON() {
        return JSON.GSON.toJson(this, SemanticAnalyzer.class);
    }

}
