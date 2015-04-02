package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

public class StarPatternAST extends TokenPatternAST {

    private final TokenPatternAST pattern;

    public StarPatternAST(TokenPatternAST pattern) {
        this.pattern = pattern;
    }

    public String toDebugString() {
        return "(" + this.pattern.toDebugString() +")*";
    }

    @Override
    public StateImpl<SemToken> buildMachine(StateImpl<SemToken> fromState, final GroupAllocator groupAllocator) {
        final StateImpl<SemToken> dest = this.pattern.buildMachine(fromState, groupAllocator);
        dest.addTransition(new EpsilonTransition<>(fromState));
        return fromState;
    }
}
