package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.Epsilon;
import com.fulmicoton.semantic.tokenpattern.nfa.State;

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
    public State<SemToken> buildMachine(State<SemToken> fromState) {
        final State<SemToken> leftFinalState = this.left.buildMachine(fromState);
        final State<SemToken> rightFinalState = this.right.buildMachine(fromState);
        rightFinalState.addTransition(new Epsilon<>(leftFinalState));
        return leftFinalState;
    }
}
