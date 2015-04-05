package com.fulmicoton.semantic.tokenpattern.nfa;

import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class Machine<T> {

    final boolean[] acceptStates;
    final int[][] transitions;
    final Predicate<T>[][] predicates;
    final int[][] openGroups;
    final int[][] closeGroups;
    final GroupAllocator groupAllocator;

    public Machine(final boolean[] acceptStates,
                   final int[][] transitions,
                   final Predicate<T>[][] predicates,
                   final int[][] openGroups,
                   final int[][] closeGroups,
                   final GroupAllocator groupAllocator) {
        this.acceptStates = acceptStates;
        this.transitions = transitions;
        this.predicates = predicates;
        this.openGroups = openGroups;
        this.closeGroups = closeGroups;
        this.groupAllocator = groupAllocator;
    }
    
    private Thread createThread(int stateId, Groups groups, int offset) {
        for (int openGroup: this.openGroups[stateId]) {
            groups = Groups.openGroup(groups, openGroup, offset);
        }
        for (int closeGroup: this.closeGroups[stateId]) {
            groups = Groups.closeGroup(groups, closeGroup, offset);
        }
        return new Thread(stateId, groups);
    }

    public Matcher<T> match(final Iterator<T> tokens) {
        List<Thread> threads = new ArrayList<>();
        int offset = 0;
        threads.add(this.createThread(0, null, offset));
        while (tokens.hasNext()) {
            offset++;
            if (threads.isEmpty()) {
                break;
            }
            final Set<Integer> states = new HashSet<>();
            final List<Thread> newThreads = new ArrayList<>();
            final T token = tokens.next();
            for (Thread thread: threads) {
                final int[] stateTransitions = this.transitions[thread.state];
                final Predicate<T>[] statePredicates = this.predicates[thread.state];
                for (int i=0; i<stateTransitions.length; i++) {
                    final Predicate<T> predicate = statePredicates[i];
                    if (predicate.apply(token)) {
                        final int dest = stateTransitions[i];
                        if (states.add(dest)) {
                            final Thread newThread = this.createThread(dest, thread.groups, offset);
                            newThreads.add(newThread);
                        }
                    }
                }
            }
            threads = newThreads;
        }
        return this.makeMatcher(threads);
    }

    private Matcher<T> makeMatcher(final List<Thread> threads) {
        for (Thread thread: threads) {
            boolean accept =  this.acceptStates[thread.state];
            if (accept) {
                return Matcher.doesMatch(thread.groups, groupAllocator);
            }
        }
        return Matcher.doesNotMatch(groupAllocator);
    }

}
