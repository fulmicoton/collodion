package com.fulmicoton.semantic.tokenpattern.nfa;

public interface Arrow<T> {
    public State<T> getDestination();
    public void replace(State<T> from, State<T> to);
    public Iterable<Transition<T>> allTransitions();
}
