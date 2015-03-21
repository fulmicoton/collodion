package com.fulmicoton.semantic;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;

public interface ProcessorBuilder<T extends TokenFilter> {
    public void init() throws IOException;
    public T createFilter(TokenStream prev) throws IOException;
}
