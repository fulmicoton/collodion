package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

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
    public StateImpl<SemToken> buildMachine(final StateImpl<SemToken> fromState,
                                            final GroupAllocator groupAllocator) {
        final StateImpl<SemToken> afterLeft = left.buildMachine(fromState, groupAllocator);
        return right.buildMachine(afterLeft, groupAllocator);
    }
}
