package com.fulmicoton.collodion.processors.tokenpattern.nfa;

import com.fulmicoton.collodion.common.Index;
import com.fulmicoton.collodion.processors.tokenpattern.GroupAllocator;
import com.fulmicoton.collodion.processors.tokenpattern.MultiGroupAllocator;
import com.fulmicoton.collodion.processors.tokenpattern.ast.AST;
import com.fulmicoton.collodion.processors.tokenpattern.ast.CapturingGroupAST;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MachineBuilder {

    final State startState;
    final MultiGroupAllocator multiGroupAllocator;

    final Map<State, Integer> stateResults;

    int numPatterns = 0;

    public MachineBuilder() {
        this.startState = new State(Integer.MAX_VALUE);
        this.multiGroupAllocator = new MultiGroupAllocator();
        this.stateResults = new HashMap<>();
        this.numPatterns = 0;
    }

    public int addPattern(final AST ast) {
        final GroupAllocator groupAllocator = this.multiGroupAllocator.newAllocator();
        ast.allocateGroups(groupAllocator);
        final State endState = ast.buildMachine(this.numPatterns, this.startState);
        this.stateResults.put(endState, numPatterns);
        return numPatterns++;
    }

    private static Index<State> makeStateIndex(final State initialState) {
        final Index.Builder<State> stateIndexBuilder = Index.builder();
        stateIndexBuilder.get(initialState);
        for (final State state: getStates(initialState)) {
            stateIndexBuilder.get(state);
        }
        return stateIndexBuilder.build(new State[0]);
    }

    public Machine buildForSearch() {
        final State initialState = new State(Integer.MAX_VALUE);
        initialState.addEpsilon(this.startState);
        initialState.transition(AST.ALWAYS_TRUE, initialState);
        return this.build(initialState);
    }

    public Machine buildForMatch() {
        return this.build(this.startState);
    }

    private Machine build(final State initialState) {
        final Index<State> stateIndex = makeStateIndex(initialState);
        final int numStates = stateIndex.size();
        final int[] maxAccessiblePatternIds = new int[numStates];
        for (int stateId=0; stateId<numStates; stateId++) {
            maxAccessiblePatternIds[stateId] = stateIndex.elFromId(stateId).minAccessiblePatternId();
        }
        final int[][] transitions = new int[numStates][];
        final Predicate[][] predicates = new Predicate[numStates][];
        final int[][] openGroups = new int[numStates][];
        final int[][] closeGroups = new int[numStates][];
        for (int stateId=0; stateId < numStates; stateId++) {
            final State state = stateIndex.elFromId(stateId);
            openGroups[stateId] = Ints.toArray(state.allOpenGroups());
            closeGroups[stateId] = Ints.toArray(state.allCloseGroups());
            final List<Transition> transitionList = state.allTransitions();
            final int stateNumTransitions = transitionList.size();
            final int[] stateTransitions = new int[stateNumTransitions];
            transitions[stateId] = stateTransitions;
            final Predicate[] statePredicates = new Predicate[stateNumTransitions];
            predicates[stateId] = statePredicates;
            for (int transitionId=0; transitionId<stateNumTransitions; transitionId++) {
                final Transition transition = transitionList.get(transitionId);
                stateTransitions[transitionId] = stateIndex.get(transition.getDestination());
                statePredicates[transitionId] = transition.predicate;
            }
        }
        final int[] stateResultsArr = computeStateResults(stateIndex);
        return new Machine(stateResultsArr,
                           this.numPatterns,
                           maxAccessiblePatternIds,
                           transitions,
                           predicates,
                           openGroups,
                           closeGroups,
                           this.multiGroupAllocator);
    }

    private static Set<State> getImplyingStates(final State endState) {
        final Set<State> implyingStates = Sets.newHashSet(endState);
        Set<State> frontier = Sets.newHashSet(endState);
        while (!frontier.isEmpty()) {
            final Set<State> newFrontier = new HashSet<>();
            for (final State state: frontier) {
                for (final State impliedState: state.epsilonOrigins()) {
                    if (implyingStates.add(impliedState)) {
                        newFrontier.add(impliedState);
                    }
                }
            }
            frontier = newFrontier;
        }
        return implyingStates;
    }

    private int[] computeStateResults(final Index<State> stateIndex) {
        final int[] stateResultsArr = new int[stateIndex.size()];
        Arrays.fill(stateResultsArr, -1);
        for (final Map.Entry<State, Integer> e: stateResults.entrySet()) {
            for (final State state: getImplyingStates(e.getKey())) {
                if (stateIndex.contains(state)) {
                    final int stateId = stateIndex.get(state);
                    assert stateResultsArr[stateId] == -1;
                    stateResultsArr[stateId] = e.getValue();
                }
            }
        }
        return stateResultsArr;
    }

    public static Iterable<State> getStates(final State initialState) {
        final Set<State> states = new HashSet<>();
        List<State> frontier = ImmutableList.of(initialState);
        states.add(initialState);
        while (!frontier.isEmpty()) {
            final List<State> newFrontier = new ArrayList<>();
            for (final State state: frontier) {
                for (final Transition transition: state.allTransitions()) {
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
