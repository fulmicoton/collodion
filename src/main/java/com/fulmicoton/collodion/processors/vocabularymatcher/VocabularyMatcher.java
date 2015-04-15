package com.fulmicoton.collodion.processors.vocabularymatcher;


import com.fulmicoton.collodion.processors.Annotation;

import java.io.IOException;
import java.util.Iterator;

public abstract class VocabularyMatcher {

    protected final CharSequence charSequence;

    VocabularyMatcher(final CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    public abstract Iterator<Annotation> match() throws IOException;

}