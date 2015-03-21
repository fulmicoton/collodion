package com.fulmicoton.semantic;


import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public abstract class VocabularyMatcher {

    private final List<Rule> rules;
    protected final CharSequence charSequence;

    VocabularyMatcher(List<Rule> rules,
                      CharSequence charSequence) {
        this.rules = ImmutableList.copyOf(rules);
        this.charSequence = charSequence;
    }

    public abstract Iterator<Annotation> match() throws IOException;

}
