package com.fulmicoton.semantic.tokenpattern.regex;

public class PlusPattern  extends TokenPattern {

    private final TokenPattern pattern;

    public PlusPattern(TokenPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toDebugString() {
        return this.pattern.toDebugString() + "+";
    }
}
