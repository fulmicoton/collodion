package com.fulmicoton.semantic.vocabularymatcher;

import com.fulmicoton.multiregexp.MultiPattern;
import com.fulmicoton.multiregexp.MultiPatternMatcher;
import com.fulmicoton.semantic.Annotation;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class RegexVocabularyMatcher extends VocabularyMatcher {

    private final MultiPatternMatcher matcher;
    private final List<Annotation> annotations = Lists.newArrayList();

    public RegexVocabularyMatcher(final List<Rule> rules,
                                  final CharSequence charSequence) {
        super(charSequence);
        List<String> patterns = Lists.newArrayList();
        for (final Rule rule: rules) {
            annotations.add(rule.annotation);
            patterns.add(rule.value);
        }
        this.matcher = MultiPattern.of(patterns).matcher();
    }

    @Override
    public Iterator<Annotation> match() throws IOException {
        final int[] matches = this.matcher.match(this.charSequence);
        final List<Annotation> annotations = this.annotations;
        return new Iterator<Annotation>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < matches.length;
            }

            @Override
            public Annotation next() {
                final int ruleId = matches[i];
                i++;
                return annotations.get(ruleId);

            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
