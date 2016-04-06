package com.fulmicoton.collodion;


import com.fulmicoton.collodion.common.JSON;
import com.fulmicoton.collodion.common.loader.ChainLoader;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.fulmicoton.collodion.processors.resetannotation.ResetAnnotationFilter;
import com.fulmicoton.collodion.tokenizer.RegexpTokenizer;
import com.fulmicoton.collodion.tokenizer.SolilessTokenizer;
import com.fulmicoton.multiregexp.Lexer;
import com.google.common.collect.ImmutableList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
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

    public enum TokenizerType {
        STANDARD {
            @Override
            public Tokenizer getTokenizer(final Reader reader) {
                return new SolilessTokenizer(new StandardTokenizer(reader), reader);
            }
        },
        WHITESPACE {
            @Override
            public Tokenizer getTokenizer(final Reader reader) {
                return new SolilessTokenizer(new WhitespaceTokenizer(reader), reader);
            }
        },
        CUSTOM {
            @Override
            public Tokenizer getTokenizer(final Reader reader) {
                final Lexer<RegexpTokenizer.TokenType> lexer = new Lexer<>();
                lexer.addRule(RegexpTokenizer.TokenType.WHITESPACE, "[ \t\n]+");
                lexer.addRule(RegexpTokenizer.TokenType.NUMBER, "[0-9][0-9,\\.]+");
                lexer.addRule(RegexpTokenizer.TokenType.WORD, "[a-zA-Z0-9]+");
                lexer.addRule(RegexpTokenizer.TokenType.SYMBOL, ".");
                final RegexpTokenizer.Configuration configuration = new RegexpTokenizer.Configuration(lexer);
                return new RegexpTokenizer(reader, configuration);
            }
        };

        public abstract Tokenizer getTokenizer(final Reader reader);
    }
    public final ImmutableList<ProcessorBuilder> processorBuilders;
    public TokenizerType tokenizer;

    public CollodionAnalyzer(
            final TokenizerType tokenizer,
            final List<ProcessorBuilder> processorBuilders) {
        this(tokenizer, processorBuilders, Loader.DEFAULT_LOADER);
    }

    public CollodionAnalyzer(final TokenizerType tokenizer,
                             final List<ProcessorBuilder> processorBuilders,
                             final Loader loader) {
        this.tokenizer = tokenizer;
        this.processorBuilders = ImmutableList.copyOf(processorBuilders);
        this.loader = loader;
    }

    public CollodionAnalyzer append(final ProcessorBuilder newProcessorBuilder) {
        final ImmutableList<ProcessorBuilder> newProcessorBuilders = ImmutableList
                .<ProcessorBuilder>builder()
                .addAll(this.processorBuilders)
                .add(newProcessorBuilder)
                .build();
        return new CollodionAnalyzer(this.tokenizer, newProcessorBuilders, this.loader);
    }

    public void prependLoader(final Loader loader) {
        this.loader = ChainLoader.of(loader, this.loader);
    }

    private void init() throws Exception {
        for (final ProcessorBuilder processorBuilder: this.processorBuilders) {
            processorBuilder.init(this.loader);
        }
    }

    @Override
    protected TokenStreamComponents createComponents(
            final String fieldName,
            final Reader reader) {
        final Tokenizer source = this.tokenizer.getTokenizer(reader);
        TokenStream lastFilter = new ResetAnnotationFilter(source);
        for (final ProcessorBuilder processorBuilder: this.processorBuilders) {
            try {
                lastFilter = processorBuilder.createFilter(lastFilter);
            } catch (final IOException e) {
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
