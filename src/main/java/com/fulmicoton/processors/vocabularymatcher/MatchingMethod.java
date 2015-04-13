package com.fulmicoton.processors.vocabularymatcher;


import com.fulmicoton.processors.stemmer.StemAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Attribute;

import java.util.List;

public enum MatchingMethod {

    EXACT(CharTermAttribute.class) {
        @Override
        public VocabularyMatcher createMatcher(final TokenStream tokenStream,
                                               final List<Rule> rules) {
            return new ExactVocabularyMatcher(rules, this.extractForm(tokenStream));
        }
    },
    STEM(StemAttribute.class) {
        @Override
        public VocabularyMatcher createMatcher(final TokenStream tokenStream,
                                               final List<Rule> rules) {
            return new ExactVocabularyMatcher(rules, this.extractForm(tokenStream));
        }
    },
    REGEX(CharTermAttribute.class) {
        @Override
        public VocabularyMatcher createMatcher(final TokenStream tokenStream,
                                               final List<Rule> rules) {
            return new RegexVocabularyMatcher(rules, this.extractForm(tokenStream));
        }
    };

    private final Class<? extends Attribute> attributeClass;

    private MatchingMethod(final Class<? extends Attribute> attributeClass) {
        this.attributeClass = attributeClass;
    }

    public CharSequence extractForm(final TokenStream tokenStream) {
        return (CharSequence)tokenStream.getAttribute(this.attributeClass);
    }

    public abstract VocabularyMatcher createMatcher(final TokenStream tokenStream,
                                                    final List<Rule> rules);
}
