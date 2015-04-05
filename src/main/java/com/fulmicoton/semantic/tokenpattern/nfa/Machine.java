package com.fulmicoton.semantic.tokenpattern.nfa;

import com.fulmicoton.semantic.tokenpattern.MultiGroupAllocator;
import com.fulmicoton.semantic.tokenpattern.SemToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class Machine {

    final int[] statesResults;
    final int nbPatterns;
    final int[][] transitions;
    final Predicate[][] predicates;
    final int[][] openGroups;
    final int[][] closeGroups;
    final MultiGroupAllocator multiGroupAllocator;

    public Machine(final int[] statesResults,
                   final int nbPatterns,
                   final int[][] transitions,
                   final Predicate[][] predicates,
                   final int[][] openGroups,
                   final int[][] closeGroups,
                   final MultiGroupAllocator multiGroupAllocator) {
        this.statesResults = statesResults;
        this.nbPatterns = nbPatterns;
        this.transitions = transitions;
        this.predicates = predicates;
        this.openGroups = openGroups;
        this.closeGroups = closeGroups;
        this.multiGroupAllocator = multiGroupAllocator;
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

    public MultiMatcher match(final Iterator<SemToken> tokens) {
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
            final SemToken token = tokens.next();
            for (Thread thread: threads) {
                final int[] stateTransitions = this.transitions[thread.state];
                final Predicate[] statePredicates = this.predicates[thread.state];
                for (int i=0; i<stateTransitions.length; i++) {
                    final Predicate predicate = statePredicates[i];
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
        return this.makeMatchers(threads);
    }

    private MultiMatcher makeMatchers(final List<Thread> threads) {
        final Matcher[] matchers = new Matcher[this.nbPatterns];
        for (Thread thread: threads) {
            int matchingPattern = this.statesResults[thread.state];
            if (matchingPattern != -1) {
                final Matcher matcher = Matcher.doesMatch(thread.groups, multiGroupAllocator.get(matchingPattern));
                matchers[matchingPattern] = matcher;
            }
        }
        for (int i=0; i<this.nbPatterns; i++) {
            if (matchers[i] == null) {
                matchers[i] = Matcher.doesNotMatch(multiGroupAllocator.get(i));
            }
        }
        return new MultiMatcher(matchers);
    }

}
