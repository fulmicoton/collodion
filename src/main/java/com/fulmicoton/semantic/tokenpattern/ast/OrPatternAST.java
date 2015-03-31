package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;

public class OrPatternAST extends TokenPatternAST {

    final TokenPatternAST left;
    final TokenPatternAST right;

    public OrPatternAST(TokenPatternAST left, TokenPatternAST right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toDebugString() {
        return "(" + this.left.toDebugString() + ")" + "|" + "(" + this.right.toDebugString() + ")";
    }

    @Override
    public SimpleState<SemToken> buildMachine(SimpleState<SemToken> fromState) {
        final SimpleState<SemToken> leftFinalState = this.left.buildMachine(fromState);
        final SimpleState<SemToken> rightFinalState = this.right.buildMachine(fromState);
        rightFinalState.addTransition(new EpsilonTransition<>(leftFinalState));
        return leftFinalState;
    }
}
