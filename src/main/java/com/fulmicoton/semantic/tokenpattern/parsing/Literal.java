package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Token;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Literal<T> implements Rule<T>,  RuleMatcher<T> {


    private final T patternTokenType;

    private Literal(T patternTokenType) {
        this.patternTokenType = patternTokenType;
    }

    public static <T> Literal<T> of(T tokenType) {
        return new Literal<T>(tokenType);
    }

    @Override
    public boolean evaluate(boolean[][][] table, int start, int l, final List<Token<T>> tokens) {
        if (l != 1) {
            return false;
        }
        return tokens.get(start).type == this.patternTokenType;
    }

    @Override
    public RuleMatcher<T> matcher(IndexBuilder<Rule<T>> indexBuilder) {
        return this;

    }

    @Override
    public List<Rule<T>> dependencies() {
        return ImmutableList.of();
    }
}
