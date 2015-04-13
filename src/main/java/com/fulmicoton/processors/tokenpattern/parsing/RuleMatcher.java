package com.fulmicoton.processors.tokenpattern.parsing;

import com.fulmicoton.multiregexp.Token;

import java.util.List;

public interface RuleMatcher<T> {
    boolean evaluate(boolean[][][] table, int start, int l, final List<Token<T>> tokens);
    List<Match<T>> getMatches(boolean[][][] table, int start, int l);
}
