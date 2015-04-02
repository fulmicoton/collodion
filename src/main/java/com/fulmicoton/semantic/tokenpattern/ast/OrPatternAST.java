package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

public class OrPatternAST extends TokenPatternAST {

    final TokenPatternAST left;
    final TokenPatternAST right;

    public OrPatternAST(TokenPatternAST left, TokenPatternAST right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toDebugString() {
        return "((" + this.left.toDebugString() + ")|(" + this.right.toDebugString() + "))";
    }

    @Override
    public StateImpl<SemToken> buildMachine(StateImpl<SemToken> fromState, final GroupAllocator groupAllocator) {
        final StateImpl<SemToken> leftFinalState = this.left.buildMachine(fromState, groupAllocator);
        final StateImpl<SemToken> rightFinalState = this.right.buildMachine(fromState, groupAllocator);
        rightFinalState.addTransition(new EpsilonTransition<>(leftFinalState));
        return leftFinalState;
    }
}
