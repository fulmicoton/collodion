package com.fulmicoton.semantic.tokenpattern.nfa;

public interface Transition<T> {
    public State<T> getDestination();
    public Iterable<State<T>> transition(final T token);
}
