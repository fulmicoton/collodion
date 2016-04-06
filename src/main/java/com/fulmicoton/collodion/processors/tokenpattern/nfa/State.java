package com.fulmicoton.collodion.processors.tokenpattern.nfa;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class State {

    private int minPatternId;
    private TreeSet<Integer> openGroups = new TreeSet<>();
    private TreeSet<Integer> closeGroups = new TreeSet<>();
    private List<Arrow> arrows = new ArrayList<>();
    private Map<Predicate, State> transitionCache = new HashMap<>();
    private Set<State> epsilonOrigins = new HashSet<>();

    public Iterable<State> epsilonOrigins() {
        return this.epsilonOrigins;
    }

    public State(final int patternId) {
        this.minPatternId = patternId;
    }

    // TODO check how to make priority still work
    public State transition(final int patternId, final Predicate predicate) {
        State state = transitionCache.get(predicate);
        if (state == null) {
            state = new State(patternId);
            this.addArrow(new Transition(state, predicate));
            transitionCache.put(predicate, state);
        }
        else {
            state.updateMinPatternId(patternId);
        }
        return state;
    }

    public int minAccessiblePatternId() {
        return this.minPatternId;
    }

    public void updateMinPatternId(final int patternId) {
        if (patternId < this.minPatternId) {
            this.minPatternId = patternId;
        }
    }

    private void addArrow(final Arrow transition) {
        arrows.add(transition);
    }

    public List<Transition> allTransitions() {
        final List<Transition> allTransitions = new ArrayList<>();
        for (final Arrow arrow: this.arrows) {
            Iterables.addAll(allTransitions, arrow.allTransitions());
        }
        return allTransitions;
    }

    public Iterable<State> epsilonSuccessors() {
        final List<State> epsilonSuccessors = Lists.newArrayList();
        for (final Arrow transition: this.arrows) {
            if (transition instanceof Epsilon) {
                epsilonSuccessors.add(transition.getDestination());
            }
        }
        return epsilonSuccessors;
    }


    public TreeSet<Integer> allOpenGroups() {
        final TreeSet<Integer> openGroups = new TreeSet<>(this.openGroups);
        for (final State state: epsilonSuccessors()) {
            openGroups.addAll(state.allOpenGroups());
        }
        return openGroups;
    }

    public TreeSet<Integer> allCloseGroups() {
        final TreeSet<Integer> closeGroups = new TreeSet<>(this.closeGroups);
        for (final State state: this.epsilonSuccessors()) {
            closeGroups.addAll(state.allCloseGroups());
        }
        return closeGroups;
    }

    public void addOpenGroup(final int groupId) {
        this.openGroups.add(groupId);
    }

    public void addCloseGroup(final int groupId) {
        this.closeGroups.add(groupId);
    }

    private void addEpsilonOrigin(final State origin) {
        this.epsilonOrigins.add(origin);
    }

    public void addEpsilon(final State dest) {
        this.updateMinPatternId(dest.minAccessiblePatternId());
        dest.addEpsilonOrigin(this);
        this.addArrow(new Epsilon(dest));
    }
}
