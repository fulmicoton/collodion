package com.fulmicoton.semantic.tokenpattern.nfa;

import java.util.List;

public interface State<T> {
    public abstract Iterable<State<T>> transition(final T token);
    public abstract Iterable<State<T>> afterEpsilonTransitions();
}
