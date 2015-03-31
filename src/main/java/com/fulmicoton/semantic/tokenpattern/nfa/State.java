package com.fulmicoton.semantic.tokenpattern.nfa;


public interface State<T> {
    public Iterable<State<T>> transition(final T token);
    public Iterable<State<T>> successors();
    public Iterable<State<T>> epsilonSuccessors();
}
