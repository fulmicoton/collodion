package com.fulmicoton.semantic.tokenpattern.nfa;


public class EpsilonTransition<T> implements Transition<T> {

    private final State<T> epsilonDestination;

    public EpsilonTransition(State<T> epsilonDestination) {
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


}
