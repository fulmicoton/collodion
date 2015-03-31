package com.fulmicoton.semantic.tokenpattern.nfa;


import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public class ConditionalTransition<T> implements Transition<T> {

    private final State<T> destination;
    private final Predicate<T> predicate;

    public ConditionalTransition(
            final State destination,
            final Predicate<T> predicate) {
        this.destination = destination;
        this.predicate = predicate;
    }

    @Override
    public State<T> getDestination() {
        return this.destination;
    }

    @Override
    public Iterable<State<T>> transition(T token) {
        if (this.predicate.apply(token)) {
            return ImmutableList.of(destination);
        }
        else {
            return ImmutableList.of();
        }
    }
}
