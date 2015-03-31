package com.fulmicoton.semantic.tokenpattern.regex;

import com.fulmicoton.semantic.tokenpattern.nfa.ConditionalTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;
import com.fulmicoton.semantic.tokenpattern.nfa.Transition;
import com.google.common.base.Predicate;

public abstract class PredicatePattern extends TokenPattern implements Predicate<SemToken> {

    @Override
    public SimpleState<SemToken> buildMachine(SimpleState<SemToken> fromState) {
        final SimpleState<SemToken> targetState = new SimpleState<>();
        final Transition<SemToken> transition = new ConditionalTransition<>(targetState, this);
        fromState.addTransition(transition);
        return targetState;
    }
}
