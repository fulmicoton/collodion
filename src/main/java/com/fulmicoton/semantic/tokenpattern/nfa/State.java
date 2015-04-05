package com.fulmicoton.semantic.tokenpattern.nfa;

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

    public State<T> isProxy() {
        if (!openGroups.isEmpty())
            return null;
        if (!closeGroups.isEmpty())
            return null;
        if (arrows.size() != 1)
            return null;
        final Arrow<T> arrow = arrows.get(0);
        if (!(arrow instanceof Epsilon))
            return null;
        return arrow.getDestination();
    }

    public void addTransition(Arrow<T> transition) {
        arrows.add(transition);
    }

    public Iterable<Transition<T>> allTransitions() {
        final List<Transition<T>> allTransitions = new ArrayList<>();
        for (final Arrow<T> arrow: this.arrows) {
            Iterables.addAll(allTransitions, arrow.allTransitions());
        }
        return allTransitions;
    }

    public Iterable<State<T>> transition(T token) {
        final Set<State<T>> destStateSet = new HashSet<>();
        final List<State<T>> destStateList = new ArrayList<>();
        for (final Transition<T> transition: this.allTransitions()) {
            for (final State<T> dest: transition.transition(token)) {
                if (destStateSet.add(dest)) {
                    destStateList.add(dest);
                }
            }
        }

        return destStateList;
    }

    public Iterable<State<T>> successors() {
        List<State<T>> successors = Lists.newArrayList();
        for (Arrow<T> transition: arrows) {
            successors.add(transition.getDestination());
        }
        return successors;
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
    /*
    public Groups updateGroups(final Groups groups, final int offset) {
        Groups result = groups;
        for (int openGroup: this.openGroups) {
            result = Groups.openGroup(result, openGroup, offset);
        }
        for (int closeGroup: this.closeGroups) {
            result = Groups.closeGroup(result, closeGroup, offset);
        }
        for (final State<T> state: epsilonSuccessors()) {
            result = state.updateGroups(result, offset);
        }
        return result;
    }
    */

    public void replace(final State<T> from, final State<T> to) {
        for (Arrow<T> transition: this.arrows) {
            transition.replace(from, to);
        }
    }

    public void addOpenGroup(int groupId) {
        this.openGroups.add(groupId);
    }


    public void addCloseGroup(int groupId) {
        this.closeGroups.add(groupId);
    }

    /*
    public void removeEpsilon() {
        final List<Arrow<T>> newTransitions = new ArrayList<>();
        for (Arrow<T> transition: this.arrows) {
            if (transition.allTransitions())
        }
        this.arrows = newTransitions;
    }
    */
}
