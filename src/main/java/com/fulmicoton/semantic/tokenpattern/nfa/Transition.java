package com.fulmicoton.semantic.tokenpattern.nfa;

import com.google.common.collect.ImmutableList;
import com.sun.istack.internal.NotNull;

public class Transition extends Arrow {

    public final Predicate predicate;

    public Transition(
            @NotNull final State destination,
            @NotNull final Predicate predicate) {
        super(destination);
        this.predicate = predicate;
    }

    @Override
    public Iterable<Transition> allTransitions() {
        return ImmutableList.of(this);
    }
}
