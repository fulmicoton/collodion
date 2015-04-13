package com.fulmicoton.processors.tokenpattern.nfa;

public abstract class Arrow {

    private State destination;

    public State getDestination() {
        return this.destination;
    }

    protected Arrow(final State destination) {
        this.destination = destination;
    }

    public abstract Iterable<Transition> allTransitions();
}
