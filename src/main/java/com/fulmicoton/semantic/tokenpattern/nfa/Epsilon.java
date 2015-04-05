package com.fulmicoton.semantic.tokenpattern.nfa;



public class Epsilon extends Arrow {

    Epsilon(final State destination) {
        super(destination);
    }

    @Override
    public Iterable<Transition> allTransitions() {
        return this.getDestination().allTransitions();
    }

}
