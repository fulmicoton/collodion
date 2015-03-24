package com.fulmicoton.semantic.tokenpattern;

public class PlusPattern  extends TokenPattern {

    private final TokenPattern pattern;

    public PlusPattern(TokenPattern pattern) {
        this.pattern = pattern;
    }
}
