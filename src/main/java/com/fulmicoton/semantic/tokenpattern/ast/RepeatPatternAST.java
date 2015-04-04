package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;
import com.google.common.collect.Sets;

import java.util.Set;

public class RepeatPatternAST extends UnaryPatternAST {

    private final int min;
    private final int max;

    public RepeatPatternAST(final AST pattern, final int min, final int max) {
        super(pattern);
        this.min = min;
        this.max = max;
    }

    @Override
    public String toDebugString() {
        return this.pattern.toDebugStringWrapped() + "{" + this.min + "," + this.max + "}";
    }

    @Override
    public StateImpl<SemToken> buildMachine(final StateImpl<SemToken> fromState) {
        StateImpl<SemToken> finalState = fromState;
        final Set<StateImpl<SemToken>> okStates = Sets.newHashSet();
        for (int i = 0; i < max; i++) {
            if (i >= min) {
                okStates.add(finalState);
            }
            finalState = pattern.buildMachine(finalState);
        }
        for (StateImpl<SemToken> state: okStates) {
            state.addTransition(new EpsilonTransition<>(finalState));
        }
        return finalState;
    }
}