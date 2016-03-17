package com.fulmicoton.collodion.processors.resetannotation;

import com.fulmicoton.collodion.common.AnnotationAttribute;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;

public class ResetAnnotationFilter extends TokenFilter {

    public static class Builder implements ProcessorBuilder<ResetAnnotationFilter> {

        @Override
        public void init(final Loader loader) throws IOException {}

        @Override
        public ResetAnnotationFilter createFilter(final TokenStream prev) throws IOException {
            return new ResetAnnotationFilter(prev);
        }
    }

    private final AnnotationAttribute annotationAttribute;

    public ResetAnnotationFilter(final TokenStream tokenStream) {
        super(tokenStream);
        this.annotationAttribute = tokenStream.addAttribute(AnnotationAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        this.annotationAttribute.reset();
        return this.input.incrementToken();
    }
}
