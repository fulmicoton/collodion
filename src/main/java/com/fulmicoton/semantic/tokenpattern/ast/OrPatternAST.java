package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

public class OrPatternAST extends BinaryPatternAST {

    public OrPatternAST(TokenPatternAST left, TokenPatternAST right) {
        super(left, right);
    }

    @Override
    public String toDebugString() {
        return "((" + this.left.toDebugString() + ")|(" + this.right.toDebugString() + "))";
    }

    @Override
    public StateImpl<SemToken> buildMachine(StateImpl<SemToken> fromState) {
        final StateImpl<SemToken> leftFinalState = this.left.buildMachine(fromState);
        final StateImpl<SemToken> rightFinalState = this.right.buildMachine(fromState);
        rightFinalState.addTransition(new EpsilonTransition<>(leftFinalState));
        return leftFinalState;
    }
}
