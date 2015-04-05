package com.fulmicoton.semantic.tokenpattern.nfa;

public abstract class Arrow<T> {

    private State<T> destination;

    public State<T> getDestination() {
        return this.destination;
    }

    protected Arrow(final State<T> destination) {
        this.destination = destination;
    }

    public abstract Iterable<Transition<T>> allTransitions();
}
