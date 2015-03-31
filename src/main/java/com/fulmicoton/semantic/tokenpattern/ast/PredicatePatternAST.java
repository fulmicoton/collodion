package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.nfa.ConditionalTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;
import com.fulmicoton.semantic.tokenpattern.nfa.Transition;
import com.google.common.base.Predicate;

public abstract class PredicatePatternAST extends TokenPatternAST {

    public abstract Predicate<SemToken> predicate();

    @Override
    public SimpleState<SemToken> buildMachine(SimpleState<SemToken> fromState) {
        final SimpleState<SemToken> targetState = new SimpleState<>();
        final Transition<SemToken> transition = new ConditionalTransition<>(targetState, this.predicate());
        fromState.addTransition(transition);
        return targetState;
    }
}
