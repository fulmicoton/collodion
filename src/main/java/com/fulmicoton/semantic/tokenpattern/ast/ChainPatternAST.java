package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

public class ChainPatternAST extends BinaryPatternAST {

    public ChainPatternAST(TokenPatternAST left, TokenPatternAST right) {
        super(left, right);
    }

    @Override
    public String toDebugString() {
        return this.left.toDebugString() + this.right.toDebugString();
    }

    @Override
    public StateImpl<SemToken> buildMachine(final StateImpl<SemToken> fromState) {
        final StateImpl<SemToken> afterLeft = left.buildMachine(fromState);
        return right.buildMachine(afterLeft);
    }
}
