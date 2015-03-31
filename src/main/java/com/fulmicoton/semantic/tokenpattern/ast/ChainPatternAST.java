package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;

public class ChainPatternAST extends TokenPatternAST {

    private final TokenPatternAST left;
    private final TokenPatternAST right;

    public ChainPatternAST(TokenPatternAST left, TokenPatternAST right) {
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
