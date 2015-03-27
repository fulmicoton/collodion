package com.fulmicoton.semantic.tokenpattern.nfa;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleState<T> implements State<T> {

    public List<Transition<T>> transitions = new ArrayList<>();
    public Set<State<T>> epsilons = new HashSet<>();

    public void addTransition(Transition<T> transition) {
        transitions.add(transition);
    }

    @Override
    public Iterable<State<T>> transition(T token) {
        final List<State<T>> destStates = new ArrayList<>();
        final Set<State<T>> destStatesSet = new HashSet<>();
        for (Transition<T> transition: this.transitions) {
            if (transition.predicate.apply(token)) {
                final State<T> destState = transition.destination;
                for (State<T> afterEps: destState.afterEpsilonTransitions()) {
                    if (!destStatesSet.contains(afterEps)) {
                        destStatesSet.add(destState);
                        destStates.add(destState);
                    }
                }
            }
        }
        return destStates;
    }

    @Override
    public Iterable<State<T>> afterEpsilonTransitions() {
        return Iterables.concat(this.epsilons, ImmutableList.of(this));
    }

    public void addEpsilon(SimpleState<T> fromState) {
        epsilons.add(fromState);
    }
}
