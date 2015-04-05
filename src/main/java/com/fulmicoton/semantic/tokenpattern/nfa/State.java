package com.fulmicoton.semantic.tokenpattern.nfa;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class State<T> {

    private TreeSet<Integer> openGroups = new TreeSet<>();
    private TreeSet<Integer> closeGroups = new TreeSet<>();
    private List<Arrow<T>> arrows = new ArrayList<>();
    private Set<State<T>> epsilonOrigins = new HashSet<>();

    public Iterable<State<T>> epsilonOrigins() {
        return this.epsilonOrigins;
    }

    public void addTransition(final Predicate<T> predicate, final State<T> state) {
        this.addArrow(new Transition<>(state, predicate));
    }

    private void addArrow(Arrow<T> transition) {
        arrows.add(transition);
    }

    public List<Transition<T>> allTransitions() {
        final List<Transition<T>> allTransitions = new ArrayList<>();
        for (final Arrow<T> arrow: this.arrows) {
            Iterables.addAll(allTransitions, arrow.allTransitions());
        }
        return allTransitions;
    }

    public Iterable<State<T>> epsilonSuccessors() {
        List<State<T>> epsilonSuccessors = Lists.newArrayList();
        for (Arrow<T> transition: this.arrows) {
            if (transition instanceof Epsilon) {
                epsilonSuccessors.add(transition.getDestination());
            }
        }
        return epsilonSuccessors;
    }


    public TreeSet<Integer> allOpenGroups() {
        final TreeSet<Integer> openGroups = new TreeSet<>(this.openGroups);
        for (final State<T> state: epsilonSuccessors()) {
            openGroups.addAll(state.allOpenGroups());
        }
        return openGroups;
    }

    public TreeSet<Integer> allCloseGroups() {
        final TreeSet<Integer> closeGroups = new TreeSet<>(this.closeGroups);
        for (final State<T> state: this.epsilonSuccessors()) {
            closeGroups.addAll(state.allCloseGroups());
        }
        return closeGroups;
    }

    public void addOpenGroup(int groupId) {
        this.openGroups.add(groupId);
    }

    public void addCloseGroup(int groupId) {
        this.closeGroups.add(groupId);
    }

    private void addEpsilonOrigin(State<T> origin) {
        this.epsilonOrigins.add(origin);
    }

    public void addEpsilon(final State<T> dest) {
        dest.addEpsilonOrigin(this);
        this.addArrow(new Epsilon<T>(dest));
    }
}
