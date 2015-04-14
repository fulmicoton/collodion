package com.fulmicoton.collodion.processors;

import com.fulmicoton.collodion.common.loader.Loader;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;

public interface ProcessorBuilder<T extends TokenFilter> {
    public void init(final Loader loader) throws IOException;
    public T createFilter(TokenStream prev) throws IOException;
}
