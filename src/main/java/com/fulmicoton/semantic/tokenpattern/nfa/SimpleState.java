package com.fulmicoton.semantic.tokenpattern.nfa;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class SimpleState<T> implements State<T> {

    public List<Transition<T>> transitions = new ArrayList<>();

    public void addTransition(Transition<T> transition) {
        transitions.add(transition);
    }

    @Override
    public Iterable<State<T>> transition(T token) {
        final OrderedSet<State<T>> destStates = new OrderedSet<>();
        for (Transition<T> transition: this.transitions) {
            destStates.addAll(transition.transition(token));
        }
        return destStates;
    }

    @Override
    public Iterable<State<T>> successors() {
        List<State<T>> successors = Lists.newArrayList();
        for (Transition transition: transitions) {
            successors.add(transition.getDestination());
        }
        return successors;
    }

    @Override
    public Iterable<State<T>> epsilonSuccessors() {
        List<State<T>> epsilonSuccessors = Lists.newArrayList();
        for (Transition<T> transition: this.transitions) {
            if (transition instanceof EpsilonTransition) {
                epsilonSuccessors.add(transition.getDestination());
            }
        }
        return epsilonSuccessors;
    }

}
