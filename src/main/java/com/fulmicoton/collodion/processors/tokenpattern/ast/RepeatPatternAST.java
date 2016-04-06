package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.tokenpattern.nfa.State;
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
    public State buildMachine(final int patternId, final State fromState) {
        State finalState = fromState;
        final Set<State> okStates = Sets.newHashSet();
        for (int i=0; i<max; i++) {
            if (i >= min) {
                okStates.add(finalState);
            }
            finalState = pattern.buildMachine(patternId, finalState);
        }
        for (final State state: okStates) {
            state.addEpsilon(finalState);
        }
        return finalState;
    }
}