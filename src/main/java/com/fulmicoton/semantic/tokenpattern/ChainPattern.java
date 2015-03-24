package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.tokenpattern.TokenPattern;

public class ChainPattern extends TokenPattern {

    private final TokenPattern left;
    private final TokenPattern right;

    public ChainPattern(TokenPattern left, TokenPattern right) {
        this.left = left;
        this.right = right;
    }


}
