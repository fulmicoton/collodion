package com.fulmicoton.semantic.tokenpattern.nfa;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MachineBuilder<T> {

    State<T> initialState;
    final GroupAllocator groupAllocator;
    IndexBuilder<State<T>> stateIndex;
    State<T>[] states;
    State<T> endState;

    public MachineBuilder(final State<T> initialState,
                          final State<T> endState,
                          final GroupAllocator groupAllocator) {
        this.initialState = initialState;
        this.groupAllocator = groupAllocator;
        this.endState = endState;
    }

    private IndexBuilder<State<T>> makeStateIndex() {
        final IndexBuilder<State<T>> stateIdMap = new IndexBuilder<>();
        stateIdMap.get(this.initialState);
        for (State<T> state: this.getStates()) {
            stateIdMap.get(state);
        }
        stateIdMap.setImmutable();
        return stateIdMap;
    }

    public Machine build() {
        this.stateIndex = makeStateIndex();
        this.states = (State<T>[])this.stateIndex.buildIndex(new State[0]);
        final int nbStates = this.stateIndex.size();
        final int[][] transitions = new int[nbStates][];
        final Predicate<T>[][] predicates = (Predicate<T>[][]) new Predicate[nbStates][];
        final int[][] openGroups = new int[nbStates][];
        final int[][] closeGroups = new int[nbStates][];
        for (int stateId=0; stateId < nbStates; stateId++) {
            final State<T> state = this.states[stateId];
            openGroups[stateId] = Ints.toArray(state.allOpenGroups());
            closeGroups[stateId] = Ints.toArray(state.allCloseGroups());
            final List<Transition<T>> transitionList = state.allTransitions();
            final int stateNbTransitions = transitionList.size();
            final int[] stateTransitions = new int[stateNbTransitions];
            transitions[stateId] = stateTransitions;
            final Predicate<T>[] statePredicates = (Predicate<T>[])new Predicate[stateNbTransitions];
            predicates[stateId] = statePredicates;
            for (int transitionId=0; transitionId<stateNbTransitions; transitionId++) {
                final Transition<T> transition = transitionList.get(transitionId);
                stateTransitions[transitionId] = this.stateIndex.get(transition.getDestination());
                statePredicates[transitionId] = transition.predicate;
            }
        }
        final boolean[] acceptStates = this.computeAcceptStates(this.endState);
        return new Machine<>(acceptStates,
                            transitions,
                            predicates,
                            openGroups,
                            closeGroups,
                            this.groupAllocator);
    }

    private boolean[] computeAcceptStates(final State<T> endState) {
        final Set<State<T>> acceptStates = Sets.newHashSet(endState);
        Set<State<T>> frontier = Sets.newHashSet(endState);
        while (!frontier.isEmpty()) {
            Set<State<T>> newFrontier = new HashSet<>();
            for (State<T> state: frontier) {
                for (State<T> impliedState: state.epsilonOrigins()) {
                    if (acceptStates.add(impliedState)) {
                        newFrontier.add(impliedState);
                    }
                }
            }
            frontier = newFrontier;
        }
        boolean[] acceptStatesArr = new boolean[this.stateIndex.size()];
        for (State<T> state: acceptStates) {
            if (this.stateIndex.contains(state)) {
                acceptStatesArr[this.stateIndex.get(state)] = true;
            }
        }
        return acceptStatesArr;
    }

    public Iterable<State<T>> getStates() {
        final Set<State<T>> states = new HashSet<>();
        List<State<T>> frontier = ImmutableList.of(this.initialState);
        states.add(this.initialState);
        while (!frontier.isEmpty()) {
            final List<State<T>> newFrontier = new ArrayList<>(30);
            for (State<T> state: frontier) {
                for (Transition<T> transition: state.allTransitions()) {
                    if (states.add(transition.getDestination())) {
                        newFrontier.add(transition.getDestination());
                    }
                }
            }
            frontier = newFrontier;
        }
        return states;
    }
}
