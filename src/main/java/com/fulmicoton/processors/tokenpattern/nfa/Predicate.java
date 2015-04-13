package com.fulmicoton.processors.tokenpattern.nfa;

import com.fulmicoton.processors.tokenpattern.SemToken;

public interface Predicate {
    public boolean apply(final SemToken token);
}
