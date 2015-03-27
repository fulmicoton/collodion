package com.fulmicoton.semantic.tokenpattern.nfa;

import com.google.common.base.Predicate;

public class Transition<T> {

    final State destination;
    final Predicate<T> predicate;

    public Transition(final State destination,
                      final Predicate<T> predicate) {
        this.destination = destination;
        this.predicate = predicate;
    }

}
