package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

public class StarPatternAST extends UnaryPatternAST {

    public StarPatternAST(AST pattern) {
        super(pattern);
    }

    public String toDebugString() {
        return this.pattern.toDebugStringWrapped() + "*";
    }

    @Override
    public StateImpl<SemToken> buildMachine(StateImpl<SemToken> fromState) {
        final StateImpl<SemToken> dest = this.pattern.buildMachine(fromState);
        dest.addTransition(new EpsilonTransition<>(fromState));
        return fromState;
    }
}
