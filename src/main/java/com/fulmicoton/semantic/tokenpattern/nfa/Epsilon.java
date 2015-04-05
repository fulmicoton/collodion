package com.fulmicoton.semantic.tokenpattern.nfa;



public class Epsilon<T> extends Arrow<T> {

    Epsilon(State<T> destination) {
        super(destination);
    }

    @Override
    public Iterable<Transition<T>> allTransitions() {
        return this.getDestination().allTransitions();
    }

}
