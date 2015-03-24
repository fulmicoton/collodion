package com.fulmicoton.semantic.tokenpattern;

public class StarPattern extends TokenPattern {

    private final TokenPattern pattern;

    public StarPattern(TokenPattern pattern) {
        this.pattern = pattern;
    }
}
