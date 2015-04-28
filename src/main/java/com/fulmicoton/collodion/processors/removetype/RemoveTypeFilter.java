package com.fulmicoton.collodion.processors.removetype;

import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

public class RemoveTypeFilter extends TokenFilter {

    public static class Builder implements ProcessorBuilder<RemoveTypeFilter> {

        public String typeToRemove = "<JUNK>";

        @Override
        public void init(final Loader loader) throws IOException {
        }

        @Override
        public RemoveTypeFilter createFilter(TokenStream prev) throws IOException {
            return new RemoveTypeFilter(prev, this.typeToRemove);
        }
    }

    // --------

    private final TypeAttribute typeAttribute;
    private final String typeToRemove;

    protected RemoveTypeFilter(final TokenStream input, final String typeToRemove) {
        super(input);
        this.typeAttribute = input.getAttribute(TypeAttribute.class);
        this.typeToRemove = typeToRemove;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        while (this.input.incrementToken()) {
            if (!this.typeAttribute.type().equals(this.typeToRemove)) {
                return true;
            }
        }
        return false;
    }
}
