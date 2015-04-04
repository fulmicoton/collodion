package com.fulmicoton.semantic.tokenpattern.nfa;

import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.google.common.collect.ImmutableList;

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
    final GroupAllocator groupAllocator;

    public Machine(final State<T> initialState,
                   final State<T> endState,
                   final GroupAllocator groupAllocator) {
        this.initialState = initialState;
        this.acceptStates = this.computeAcceptStates(endState);
        this.groupAllocator = groupAllocator;
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

    private Thread<T> findAcceptedThread(final Iterable<Thread<T>> threads) {
        for (final Thread<T> thread: threads) {
            if (this.accept(thread.getState())) {
                return thread;
            }
        }
        return null;
    }

    private Matcher<T> makeMatcher(final Iterable<Thread<T>> finalThreads) {
        final Thread<T> acceptedThread = this.findAcceptedThread(finalThreads);
        if (acceptedThread != null) {
            return Matcher.doesMatch(acceptedThread.groups(), this.groupAllocator);
        }
        else {
            return Matcher.doesNotMatch(this.groupAllocator);
        }
    }

    public Matcher<T> match(final Iterator<T> tokens) {
        List<Thread<T>> threads = new ArrayList<>();
        threads.add(new Thread<>(this.initialState));
        int tokenId = 0;
        while (tokens.hasNext()) {
            tokenId++;
            if (threads.isEmpty()) {
                break;
            }
            final Set<State<T>> states = new HashSet<>();
            final List<Thread<T>> newThreads = new ArrayList<>();
            final T token = tokens.next();
            for (Thread<T> thread: threads) {
                newThreads.addAll(thread.transition(token, tokenId, states));
            }
            threads = newThreads;
        }
        return this.makeMatcher(threads);
    }

    public Iterable<State<T>> getStates() {
        final Set<State<T>> states = new HashSet<>();
        List<State<T>> frontier = ImmutableList.of(this.initialState);
        states.add(this.initialState);
        while (!frontier.isEmpty()) {
            final List<State<T>> newFrontier = new ArrayList<>(30);
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
