package org.apache.lucene.analysis.en;


import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.fulmicoton.collodion.processors.stemmer.StemAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class StemFilter extends TokenFilter {

    private final PorterStemmer stemmer = new PorterStemmer();
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final StemAttribute stemAtt = addAttribute(StemAttribute.class);

    protected StemFilter(TokenStream input) {
        super(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken())
            return false;
        if (stemmer.stem(termAtt.buffer(), 0, termAtt.length())) {
            stemAtt.copyBuffer(stemmer.getResultBuffer(), 0, stemmer.getResultLength());
        }
        else {
            stemAtt.copyBuffer(termAtt.buffer(), 0, termAtt.length());
        }
        return true;
    }

    public static class Builder implements ProcessorBuilder<StemFilter> {

        @Override
        public void init(final Loader loader) throws IOException {}

        @Override
        public StemFilter createFilter(TokenStream prev) throws IOException {
            return new StemFilter(prev);
        }
    }

}
