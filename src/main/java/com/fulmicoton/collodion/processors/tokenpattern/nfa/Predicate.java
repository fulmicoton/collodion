package com.fulmicoton.collodion.processors.tokenpattern.nfa;

import com.fulmicoton.collodion.processors.tokenpattern.SemToken;

import java.util.List;

public interface Predicate {
    List<Integer> apply(final SemToken token);
}
