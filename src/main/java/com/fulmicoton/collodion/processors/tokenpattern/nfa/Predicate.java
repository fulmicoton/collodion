package com.fulmicoton.collodion.processors.tokenpattern.nfa;

import com.fulmicoton.collodion.processors.tokenpattern.SemToken;

public interface Predicate {
    public boolean apply(final SemToken token);
}
