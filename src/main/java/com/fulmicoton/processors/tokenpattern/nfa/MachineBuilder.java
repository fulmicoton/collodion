package com.fulmicoton.processors.tokenpattern.nfa;

import com.fulmicoton.common.Index;
import com.fulmicoton.processors.tokenpattern.GroupAllocator;
import com.fulmicoton.processors.tokenpattern.MultiGroupAllocator;
import com.fulmicoton.processors.tokenpattern.ast.AST;
import com.fulmicoton.processors.tokenpattern.ast.CapturingGroupAST;
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
    int nbPatterns = 0;

    public MachineBuilder() {
        this.startState = new State();
        this.multiGroupAllocator = new MultiGroupAllocator();
        this.stateResults = new HashMap<>();
        this.nbPatterns = 0;
    }

    public int add(final String tokenPattern) {
        final GroupAllocator groupAllocator = this.multiGroupAllocator.newAllocator();
        final AST ast = new CapturingGroupAST(AST.compile(tokenPattern));
        ast.allocateGroups(groupAllocator);
        final State endState = ast.buildMachine(this.startState);
        this.stateResults.put(endState, nbPatterns);
        return nbPatterns++;
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
        final State initialState = new State();
        initialState.addEpsilon(this.startState);
        initialState.transition(AST.ALWAYS_TRUE).addEpsilon(initialState);
        return this.build(initialState);
    }

    public Machine buildForMatch() {
        return this.build(this.startState);
    }

    private Machine build(final State initialState) {
        final Index<State> stateIndex = makeStateIndex(initialState);
        final int nbStates = stateIndex.size();
        final int[][] transitions = new int[nbStates][];
        final Predicate[][] predicates = new Predicate[nbStates][];
        final int[][] openGroups = new int[nbStates][];
        final int[][] closeGroups = new int[nbStates][];
        for (int stateId=0; stateId < nbStates; stateId++) {
            final State state = stateIndex.elFromId(stateId);
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
        final int[] stateResultsArr = computeStateResults(stateIndex);
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

    private int[] computeStateResults(final Index<State> stateIndex) {
        final int[] stateResultsArr = new int[stateIndex.size()];
        Arrays.fill(stateResultsArr, -1);
        for (Map.Entry<State, Integer> e: stateResults.entrySet()) {
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
