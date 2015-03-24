package com.fulmicoton.semantic.tokenpattern;

public class RepeatPattern extends TokenPattern {

    private final TokenPattern pattern;
    private final int min;
    private final int max;

    public RepeatPattern(TokenPattern pattern, int min, int max) {
        this.pattern = pattern;
        this.min = min;
        this.max = max;
    }

    @Override
    public String toDebugString() {
        return "(" + this.pattern.toDebugString() + "){" + this.min + "," + this.max + "}";
    }
}