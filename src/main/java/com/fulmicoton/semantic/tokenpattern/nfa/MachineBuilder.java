package com.fulmicoton.semantic.tokenpattern.nfa;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.fulmicoton.semantic.tokenpattern.MultiGroupAllocator;
import com.fulmicoton.semantic.tokenpattern.ast.AST;
import com.fulmicoton.semantic.tokenpattern.ast.CapturingGroupAST;
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

    final State initialState;
    final MultiGroupAllocator multiGroupAllocator;
    final Map<State, Integer> stateResults;
    int nbPatterns = 0;

    public MachineBuilder() {
        this.initialState = new State();
        this.multiGroupAllocator = new MultiGroupAllocator();
        this.stateResults = new HashMap<>();
        this.nbPatterns = 0;
    }

    public int add(final String tokenPattern) {
        final GroupAllocator groupAllocator = this.multiGroupAllocator.newAllocator();
        final AST ast = new CapturingGroupAST(AST.compile(tokenPattern));
        ast.allocateGroups(groupAllocator);
        final State endState = ast.buildMachine(this.initialState);
        this.stateResults.put(endState, nbPatterns);
        return nbPatterns++;
    }


    private IndexBuilder<State> makeStateIndex() {
        final IndexBuilder<State> stateIdMap = new IndexBuilder<>();
        stateIdMap.get(this.initialState);
        for (final State state: this.getStates()) {
            stateIdMap.get(state);
        }
        stateIdMap.setImmutable();
        return stateIdMap;
    }

    public Machine build() {
        final IndexBuilder<State> stateIndex = makeStateIndex();
        final State[] states = stateIndex.buildIndex(new State[0]);
        final int nbStates = stateIndex.size();
        final int[][] transitions = new int[nbStates][];
        final Predicate[][] predicates = new Predicate[nbStates][];
        final int[][] openGroups = new int[nbStates][];
        final int[][] closeGroups = new int[nbStates][];
        for (int stateId=0; stateId < nbStates; stateId++) {
            final State state = states[stateId];
            openGroups[stateId] = Ints.toArray(state.allOpenGroups());
            closeGroups[stateId] = Ints.toArray(state.allCloseGroups());
            final List<Transition> transitionList = state.allTransitions();
            final int stateNbTransitions = transitionList.size();
            final int[] stateTransitions = new int[stateNbTransitions];
            transitions[stateId] = stateTransitions;
            final Predicate[] statePredicates = new Predicate[stateNbTransitions];
            predicates[stateId] = statePredicates;
            for (int transitionId=0; transitionId<stateNbTransitions; transitionId++) {
                final Transition transition = transitionList.get(transitionId);
                stateTransitions[transitionId] = stateIndex.get(transition.getDestination());
                statePredicates[transitionId] = transition.predicate;
            }
        }
        final int[] stateResultsArr = this.computeStateResults(stateIndex);
        return new Machine(stateResultsArr,
                           this.nbPatterns,
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
            Set<State> newFrontier = new HashSet<>();
            for (State state: frontier) {
                for (State impliedState: state.epsilonOrigins()) {
                    if (implyingStates.add(impliedState)) {
                        newFrontier.add(impliedState);
                    }
                }
            }
            frontier = newFrontier;
        }
        return implyingStates;
    }

    private int[] computeStateResults(final IndexBuilder<State> stateIndex) {
        final int[] stateResultsArr = new int[stateIndex.size()];
        Arrays.fill(stateResultsArr, -1);
        for (Map.Entry<State, Integer> e: this.stateResults.entrySet()) {
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

    public Iterable<State> getStates() {
        final Set<State> states = new HashSet<>();
        List<State> frontier = ImmutableList.of(this.initialState);
        states.add(this.initialState);
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
