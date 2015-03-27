package com.fulmicoton.semantic.tokenpattern.nfa;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Machine<T> {

    final State<T> initialState;
    final Set<State<T>> acceptStates = new HashSet<>();

    public Machine(final State<T> initialState) {
        this.initialState = initialState;
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
        List<State<T>> threads = Lists.newArrayList(this.initialState);
        while (tokens.hasNext()) {
            if (threads.isEmpty()) {
                return new Matcher<T>(false);
            }
            List<State<T>> newThreads = Lists.newArrayList();
            Set<State<T>> newStates = Sets.newHashSet();
            final T token = tokens.next();
            for (State<T> state: threads) {
                for (State<T> nextState: state.transition(token)) {
                    if (!newStates.contains(nextState)) {
                        newThreads.add(nextState);
                    }
                }
            }
            threads = newThreads;
        }
        return this.makeMatcher(threads);
    }

}
