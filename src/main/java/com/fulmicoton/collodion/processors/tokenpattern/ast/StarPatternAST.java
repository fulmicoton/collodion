package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.tokenpattern.nfa.State;

public class StarPatternAST extends UnaryPatternAST {

    public StarPatternAST(AST pattern) {
        super(pattern);
    }

    public String toDebugString() {
        return this.pattern.toDebugStringWrapped() + "*";
    }

    @Override
    public State buildMachine(final State fromState) {
        final State dest = this.pattern.buildMachine(fromState);
        dest.addEpsilon(fromState);
        return fromState;
    }
}
