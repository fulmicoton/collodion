package com.fulmicoton.semantic.tokenpattern.nfa;


import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.sun.istack.internal.NotNull;

public class Transition<T> extends Arrow<T> {

    public final Predicate<T> predicate;

    public Transition(
            @NotNull final State<T> destination,
            @NotNull final Predicate<T> predicate) {
        super(destination);
        this.predicate = predicate;
    }

    @Override
    public Iterable<Transition<T>> allTransitions() {
        return ImmutableList.of(this);
    }
}
