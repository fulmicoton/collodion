package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.Epsilon;
import com.fulmicoton.semantic.tokenpattern.nfa.State;

public class StarPatternAST extends UnaryPatternAST {

    public StarPatternAST(AST pattern) {
        super(pattern);
    }

    public String toDebugString() {
        return this.pattern.toDebugStringWrapped() + "*";
    }

    @Override
    public State<SemToken> buildMachine(State<SemToken> fromState) {
        final State<SemToken> dest = this.pattern.buildMachine(fromState);
        dest.addTransition(new Epsilon<>(fromState));
        return fromState;
    }
}
