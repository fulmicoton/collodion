package com.fulmicoton.semantic.tokenpattern.nfa;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class State<T> {

    public int openGroup = -1;
    public int closeGroup = -1;

    public List<Transition<T>> transitions = new ArrayList<>();

    public void addTransition(Transition<T> transition) {
        transitions.add(transition);
    }

    @Override
    public Iterable<State<T>> transition(T token) {
        final Set<State<T>> destStateSet = new HashSet<>();
        final List<State<T>> destStateList = new ArrayList<>();
        for (final Transition<T> transition: this.transitions) {
            for (final State<T> dest: transition.transition(token)) {
                if (destStateSet.add(dest)) {
                    destStateList.add(dest);
                }
            }
        }
        return destStateList;
    }

    @Override
    public Iterable<State<T>> successors() {
        List<State<T>> successors = Lists.newArrayList();
        for (Transition<T> transition: transitions) {
            successors.add(transition.getDestination());
        }
        return successors;
    }

    @Override
    public Iterable<State<T>> epsilonSuccessors() {
        List<State<T>> epsilonSuccessors = Lists.newArrayList();
        for (Transition<T> transition: this.transitions) {
            if (transition instanceof EpsilonTransition) {
                epsilonSuccessors.add(transition.getDestination());
            }
        }
        return epsilonSuccessors;
    }


    @Override
    public Groups updateGroups(final Groups groups, final int offset) {
        Groups result = groups;
        if (this.openGroup >= 0) {
            result = Groups.openGroup(result, this.openGroup, offset);
        }
        if (this.closeGroup >= 0) {
            result = Groups.closeGroup(result, this.closeGroup, offset);
        }
        for (final State<T> state: epsilonSuccessors()) {
            result = state.updateGroups(result, offset);
        }
        return result;
    }

    @Override
    public void replace(final State<T> from, final State<T> to) {
        for (Transition<T> transition: this.transitions) {
            transition.replace(from, to);
        }
    }

}
