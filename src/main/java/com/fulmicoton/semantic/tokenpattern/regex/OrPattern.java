package com.fulmicoton.semantic.tokenpattern.regex;

import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;

public class OrPattern extends TokenPattern {

    final TokenPattern left;
    final TokenPattern right;

    public OrPattern(TokenPattern left, TokenPattern right) {
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
