package com.fulmicoton.semantic.tokenpattern.nfa;


import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public class Transition<T> implements Arrow<T> {

    private State<T> destination;
    private final Predicate<T> predicate;

    public Transition(
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

    @Override
    public void replace(State<T> from, State<T> to) {
        if (this.destination == from) {
            this.destination = to;
        }
    }
}
