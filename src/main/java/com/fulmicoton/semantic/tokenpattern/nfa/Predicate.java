package com.fulmicoton.semantic.tokenpattern.nfa;

import com.fulmicoton.semantic.tokenpattern.SemToken;

public interface Predicate {
    public boolean apply(final SemToken token);
}
