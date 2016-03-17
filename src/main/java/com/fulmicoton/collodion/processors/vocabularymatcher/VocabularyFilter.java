package com.fulmicoton.collodion.processors.vocabularymatcher;

import com.fulmicoton.collodion.common.AnnotationAttribute;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.google.common.collect.Lists;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class VocabularyFilter extends TokenFilter {

    public static class Builder implements ProcessorBuilder<VocabularyFilter> {

        public String path;
        private transient Vocabulary vocabulary;

        @Override
        public void init(final Loader loader) throws IOException {
            final InputStream inputStream = loader.open(path);
            if (inputStream == null) {
                throw new FileNotFoundException("Could not find vocabulary file at path : " + this.path);
            }
            this.vocabulary = Vocabulary.fromStream(inputStream);

        }

        @Override
        public VocabularyFilter createFilter(final TokenStream prev) throws IOException {
            return new VocabularyFilter(prev, vocabulary);
        }
    }

    private final List<VocabularyMatcher> vocabularyMatchers;
    private final AnnotationAttribute vocabularyAttr;

    protected VocabularyFilter(final TokenStream input, final Vocabulary vocabulary) {
        super(input);
        final EnumMap<MatchingMethod, List<Rule>> groupedRules = vocabulary.grouped();
        this.vocabularyMatchers = Lists.newArrayList();
        this.vocabularyAttr = input.addAttribute(AnnotationAttribute.class);
        for (final Map.Entry<MatchingMethod, List<Rule>> e: groupedRules.entrySet()) {
            final MatchingMethod matchingMethod = e.getKey();
            final List<Rule> rules = e.getValue();
            final VocabularyMatcher vocabularyMatcher = matchingMethod.createMatcher(input, rules);
            this.vocabularyMatchers.add(vocabularyMatcher);
        }
    }

    @Override
    public final boolean incrementToken() throws IOException {
        final boolean res = input.incrementToken();
        this.vocabularyAttr.reset();
        if (!res) {
            return false;
        }
        for (final VocabularyMatcher vocabularyMatcher: this.vocabularyMatchers) {
            final Iterator<AnnotationKey> annotationIterator = vocabularyMatcher.match();
            while (annotationIterator.hasNext()) {
                this.vocabularyAttr.add(annotationIterator.next());
            }
        }
        return true;
    }
}
