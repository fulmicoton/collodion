package com.fulmicoton.semantic.tokenpattern.nfa;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Machine<T> {

    final State<T> initialState;
    final Set<State<T>> acceptStates;

    public Machine(final State<T> initialState, final State<T> endState) {
        this.initialState = initialState;
        this.acceptStates = this.computeAcceptStates(endState);
    }

    private Set<State<T>> computeAcceptStates(final State<T> endState) {
        final Map<State<T>, List<State<T>>> impliesMap = new HashMap<>();
        for (State<T> state: this.getStates()) {
            for (State<T> impliedBy: state.epsilonSuccessors()) {
                if (!impliesMap.containsKey(impliedBy)) {
                    impliesMap.put(impliedBy, new ArrayList<State<T>>());
                }
                impliesMap.get(impliedBy).add(state);
            }
        }
        final Set<State<T>> acceptStates = new HashSet<>();
        acceptStates.add(endState);
        Set<State<T>> frontier = new HashSet<>();
        frontier.add(endState);
        while (!frontier.isEmpty()) {
            Set<State<T>> newFrontier = new HashSet<>();
            for (State<T> state: frontier) {
                final List<State<T>> impliedStates = impliesMap.get(state);
                if (impliedStates != null) {
                    for (State<T> impliedState: impliedStates) {
                        if (acceptStates.add(impliedState)) {
                            newFrontier.add(impliedState);
                        }
                    }
                }
            }
            frontier = newFrontier;
        }
        return acceptStates;
    }

    private boolean accept(final State<T> state) {
        return this.acceptStates.contains(state);
    }

    private State<T> findAcceptedState(final Iterable<State<T>> finalStates) {
        for (final State<T> finalState: finalStates) {
            if (this.accept(finalState)) {
                return finalState;
            }
        }
        return null;
    }

    private Matcher<T> makeMatcher(final Iterable<State<T>> finalStates) {
        final State<T> acceptedState = this.findAcceptedState(finalStates);
        if (acceptedState != null) {
            return new Matcher<T>(true);
        }
        else {
            return new Matcher<T>(false);
        }
    }

    public Matcher match(Iterator<T> tokens) {
        OrderedSet<State<T>> threads = new OrderedSet<>();
        threads.add(this.initialState);
        while (tokens.hasNext()) {
            if (threads.isEmpty()) {
                return new Matcher<T>(false);
            }
            OrderedSet<State<T>> newThreads = new OrderedSet<>();
            final T token = tokens.next();
            for (State<T> state: threads) {
                newThreads.addAll(state.transition(token));
            }
            threads = newThreads;
        }
        return this.makeMatcher(threads);
    }

    public Iterable<State<T>> getStates() {
        Set<State<T>> states = new HashSet<>();
        states.add(this.initialState);
        List<State<T>> frontier = ImmutableList.of(this.initialState);
        while (!frontier.isEmpty()) {
            List<State<T>> newFrontier = new ArrayList<>();
            for (State<T> state: frontier) {
                for (State<T> successor: state.successors()) {
                    if (states.add(successor)) {
                        newFrontier.add(successor);
                    }
                }
            }
            frontier = newFrontier;
        }
        return states;
    }
}
