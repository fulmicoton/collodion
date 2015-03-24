package com.fulmicoton.semantic.tokenpattern;

public class ChainPattern extends TokenPattern {

    private final TokenPattern left;
    private final TokenPattern right;

    public ChainPattern(TokenPattern left, TokenPattern right) {
        this.left = left;
        this.right = right;
    }


    @Override
    public String toDebugString() {
        return this.left.toDebugString() + this.right.toDebugString();
    }
}
