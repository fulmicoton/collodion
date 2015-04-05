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
    public Iterable<Transition<T>> allTransitions() {
        return this.epsilonDestination.allTransitions();
    }

    @Override
    public void replace(State<T> from, State<T> to) {
        if (this.epsilonDestination == from) {
            this.epsilonDestination = to;
        }
    }


}
