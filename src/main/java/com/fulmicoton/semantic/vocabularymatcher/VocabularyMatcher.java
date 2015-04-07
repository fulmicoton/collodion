package com.fulmicoton.semantic.vocabularymatcher;


import com.fulmicoton.semantic.Annotation;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public abstract class VocabularyMatcher {

    protected final CharSequence charSequence;

    VocabularyMatcher(final CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    public abstract Iterator<Annotation> match() throws IOException;

}
