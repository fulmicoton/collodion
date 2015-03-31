package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;

public class StarPatternAST extends TokenPatternAST {

    private final TokenPatternAST pattern;

    public StarPatternAST(TokenPatternAST pattern) {
        this.pattern = pattern;
    }

    public String toDebugString() {
        return "(" + this.pattern.toDebugString() +")*";
    }

    @Override
    public SimpleState<SemToken> buildMachine(SimpleState<SemToken> fromState) {
        final SimpleState<SemToken> dest = this.pattern.buildMachine(fromState);
        dest.addTransition(new EpsilonTransition<>(fromState));
        return fromState;
    }
}
