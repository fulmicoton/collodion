package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.tokenpattern.nfa.State;

public class StarPatternAST extends UnaryPatternAST {

    public StarPatternAST(final AST pattern) {
        super(pattern);
    }

    public String toDebugString() {
        return this.pattern.toDebugStringWrapped() + "*";
    }

    @Override
    public State buildMachine(final int patternId, final State fromState) {
        final State dest = this.pattern.buildMachine(patternId, fromState);
        dest.addEpsilon(fromState);
        return fromState;
    }
}
