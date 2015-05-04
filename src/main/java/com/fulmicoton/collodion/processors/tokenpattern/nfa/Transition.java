package com.fulmicoton.collodion.processors.tokenpattern.nfa;

import com.google.common.collect.ImmutableList;

public class Transition extends Arrow {

    public final Predicate predicate;

    public Transition(final State destination, final Predicate predicate) {
        super(destination);
        this.predicate = predicate;
    }

    @Override
    public Iterable<Transition> allTransitions() {
        return ImmutableList.of(this);
    }
}
