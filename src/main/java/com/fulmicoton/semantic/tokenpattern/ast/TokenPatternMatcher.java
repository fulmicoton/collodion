package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.common.loader.Loader;
import com.fulmicoton.semantic.ProcessorBuilder;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TokenPatternMatcher extends TokenFilter {


    public static class Builder implements ProcessorBuilder<TokenPatternMatcher> {

        public String path;
        private transient List<TokenPatternAST> patterns;

        @Override
        public void init(final Loader loader) throws IOException {
            final InputStream inputStream = loader.open(path);
            // this.vocabulary = Vocabulary.fromStream(inputStream);
        }

        @Override
        public TokenPatternMatcher createFilter(TokenStream prev) throws IOException {
            //return new VocabularyFilter(prev, vocabulary);
            return null;
        }
    }

    protected TokenPatternMatcher(TokenStream input) {
        super(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        return false;
    }
}
