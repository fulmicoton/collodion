package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.ConditionalTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;
import com.fulmicoton.semantic.tokenpattern.nfa.Transition;
import com.google.common.base.Predicate;

public abstract class PredicatePatternAST extends TokenPatternAST {

    public abstract Predicate<SemToken> predicate();

    @Override
    public StateImpl<SemToken> buildMachine(final StateImpl<SemToken> fromState, final GroupAllocator groupAllocator) {
        final StateImpl<SemToken> targetState = new StateImpl<>();
        final Transition<SemToken> transition = new ConditionalTransition<>(targetState, this.predicate());
        fromState.addTransition(transition);
        return targetState;
    }
}
