package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.multiregexp.Token;

import java.util.List;

public interface RuleMatcher<T> {
    boolean evaluate(boolean[][][] table, int start, int l, final List<Token<T>> tokens);
    // ParsedTree emit(final Token<T> tokens, boolean[][][] table, int start, int length);

}
