package com.fulmicoton.processors.tokenpattern.ast;

import com.fulmicoton.processors.tokenpattern.nfa.State;

public class OrPatternAST extends BinaryPatternAST {

    public OrPatternAST(AST left, AST right) {
        super(left, right);
    }

    @Override
    public String toDebugString() {
        return this.left.toDebugStringWrapped() + "|" + this.right.toDebugStringWrapped();
    }

    @Override
    public String toDebugStringWrapped() {
        return "(?:" + this.toDebugString() + ")";
    }

    @Override
    public State buildMachine(State fromState) {
        final State leftFinalState = this.left.buildMachine(fromState);
        final State rightFinalState = this.right.buildMachine(fromState);
        rightFinalState.addEpsilon(leftFinalState);
        return leftFinalState;
    }
}
