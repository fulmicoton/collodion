package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

public class NonCapturingGroupAST extends UnaryPatternAST {

    public NonCapturingGroupAST(TokenPatternAST pattern) {
        super(pattern);
    }

    @Override
    public String toDebugString() {
        return "(?:" + this.pattern.toDebugString() + ")";
    }

    @Override
    public StateImpl<SemToken> buildMachine(final StateImpl<SemToken> fromState) {
        return this.pattern.buildMachine(fromState);
    }


}
