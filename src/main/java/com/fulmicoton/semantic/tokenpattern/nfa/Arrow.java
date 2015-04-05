package com.fulmicoton.semantic.tokenpattern.nfa;

public interface Arrow<T> {
    public State<T> getDestination();
    public Iterable<State<T>> transition(final T token);
    public void replace(State<T> from, State<T> to);
}
