package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.tokenpattern.nfa.State;

public class OrPatternAST extends BinaryPatternAST {

    public OrPatternAST(final AST left, final AST right) {
        super(left, right);
    }

    @Override
    public String toDebugString() {
        return this.left.toDebugStringWrapped() + "|" + this.right.toDebugStringWrapped();
    }

    @Override
    public String toDebugStringWrapped() {
        return "(" + this.toDebugString() + ")";
    }

    @Override
    public State buildMachine(final int patternId, final State fromState) {
        final State leftFinalState = this.left.buildMachine(patternId, fromState);
        final State rightFinalState = this.right.buildMachine(patternId, fromState);
        rightFinalState.addEpsilon(leftFinalState);
        return leftFinalState;
    }
}
