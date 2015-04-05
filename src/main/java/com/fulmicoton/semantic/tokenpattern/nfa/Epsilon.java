package com.fulmicoton.semantic.tokenpattern.nfa;


public class Epsilon<T> implements Arrow<T> {

    private State<T> epsilonDestination;

    public Epsilon(State<T> epsilonDestination) {
        this.epsilonDestination = epsilonDestination;
    }

    @Override
    public State<T> getDestination() {
        return this.epsilonDestination;
    }

    @Override
    public Iterable<State<T>> transition(T token) {
        return this.epsilonDestination.transition(token);
    }

    @Override
    public Iterable<Arrow<T>> underlyingTransitions(T token) {
        return this.epsilonDestination.allTransitions();
    }

    @Override
    public void replace(State<T> from, State<T> to) {
        if (this.epsilonDestination == from) {
            this.epsilonDestination = to;
        }
    }


}
