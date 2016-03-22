package com.fulmicoton.collodion.processors.sequencematcher;

import java.io.IOException;

public abstract class VocabularyMatcher {

    protected final CharSequence charSequence;

    VocabularyMatcher(final CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    public abstract void match(final MatchList matchList) throws IOException;

}
