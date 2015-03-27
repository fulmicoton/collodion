package com.fulmicoton.semantic.tokenpattern.regex;

import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;

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

    @Override
    public SimpleState<SemToken> buildMachine(SimpleState<SemToken> fromState) {
        final SimpleState<SemToken> afterLeft = left.buildMachine(fromState);
        return right.buildMachine(afterLeft);
    }
}
