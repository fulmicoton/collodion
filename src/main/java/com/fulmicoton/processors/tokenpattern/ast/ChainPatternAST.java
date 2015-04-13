package com.fulmicoton.processors.tokenpattern.ast;

import com.fulmicoton.processors.tokenpattern.nfa.State;

public class ChainPatternAST extends BinaryPatternAST {

    public ChainPatternAST(AST left, AST right) {
        super(left, right);
    }

    @Override
    public String toDebugString() {
        return this.left.toDebugString() + this.right.toDebugString();
    }

    public String toDebugStringWrapped() {
        return "(?:" + this.toDebugString() + ")";
    }

    @Override
    public State buildMachine(final State fromState) {
        final State afterLeft = left.buildMachine(fromState);
        return right.buildMachine(afterLeft);
    }
}
