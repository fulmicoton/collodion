package com.fulmicoton.semantic.tokenpattern.regex;

import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;
import com.google.common.collect.Sets;

import java.util.Set;

public class RepeatPattern extends TokenPattern {

    private final TokenPattern pattern;
    private final int min;
    private final int max;

    public RepeatPattern(TokenPattern pattern, int min, int max) {
        this.pattern = pattern;
        this.min = min;
        this.max = max;
    }

    @Override
    public String toDebugString() {
        return "(" + this.pattern.toDebugString() + "){" + this.min + "," + this.max + "}";
    }

    @Override
    public SimpleState<SemToken> buildMachine(final SimpleState<SemToken> fromState) {
        SimpleState<SemToken> finalState = fromState;
        final Set<SimpleState<SemToken>> okStates = Sets.newHashSet();
        for (int i = 0; i < max; i++) {
            if (i >= min) {
                okStates.add(finalState);
            }
            finalState = pattern.buildMachine(finalState);
        }
        for (SimpleState<SemToken> state: okStates) {
            state.addTransition(new EpsilonTransition<SemToken>(finalState));
        }
        return finalState;
    }
}