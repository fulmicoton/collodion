package com.fulmicoton.semantic.tokenpattern.regex;

import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;

public class StarPattern extends TokenPattern {

    private final TokenPattern pattern;

    public StarPattern(TokenPattern pattern) {
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
