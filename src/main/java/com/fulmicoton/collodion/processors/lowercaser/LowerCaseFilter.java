package com.fulmicoton.collodion.processors.lowercaser;

import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class LowerCaseFilter extends TokenFilter {

    public static class Builder implements ProcessorBuilder<LowerCaseFilter> {

        @Override
        public void init(final Loader loader) throws IOException {
        }

        @Override
        public LowerCaseFilter createFilter(final TokenStream prev) throws IOException {
            return new LowerCaseFilter(prev);
        }
    }

    private final CharTermAttribute charTermAttribute;
    private final LowerCaseAttribute lowerCaseAttribute;

    protected LowerCaseFilter(final TokenStream input) {
        super(input);
        this.charTermAttribute = input.getAttribute(CharTermAttribute.class);
        this.lowerCaseAttribute = addAttribute(LowerCaseAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final int charTermLength = this.charTermAttribute.length();
            lowerCaseAttribute.setLength(charTermLength);
            final char[] buffer = this.lowerCaseAttribute.buffer();
            for (int i=0; i<charTermLength; i++) {
                final char c = this.charTermAttribute.charAt(i);
                buffer[i] = Character.toLowerCase(c);
            }
            return true;
        }
        else {
            return false;
        }
    }
}
