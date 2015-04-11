package com.fulmicoton.semantic.tokenpattern.nfa;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenPatternMatcher {

    List<Thread> threads;
    int offset = 0;
    final private Machine machine;

    private Thread createThread(int stateId, Groups groups, int offset) {
        if (this.machine.openGroups.length + this.machine.closeGroups.length > 0) {
            groups = new Groups(this.machine.openGroups[stateId], this.machine.closeGroups[stateId], offset, groups);
        }
        return new Thread(stateId, groups);
    }

    TokenPatternMatcher(final Machine machine) {
        this.machine = machine;
        this.reset();
    }

    public void reset() {
        final Thread initialThread = this.createThread(0, null, offset);
        threads = Lists.newArrayList(initialThread);
    }

    public MultiMatcher matchers() {
        final TokenPatternMatchResult[] matchResults = new TokenPatternMatchResult[machine.nbPatterns];
        for (Thread thread: threads) {
            int matchingPattern = machine.statesResults[thread.state];
            if (matchingPattern != -1) {
                final TokenPatternMatchResult matchResult = TokenPatternMatchResult.doesMatch(matchingPattern, thread.groups, machine.multiGroupAllocator.get(matchingPattern));
                matchResults[matchingPattern] = matchResult;
            }
        }
        for (int i=0; i<machine.nbPatterns; i++) {
            if (matchResults[i] == null) {
                matchResults[i] = TokenPatternMatchResult.doesNotMatch(i, machine.multiGroupAllocator.get(i));
            }
        }
        return new MultiMatcher(matchResults);
    }

    public void processToken(final SemToken token) {
        offset++;
        final Set<Integer> states = new HashSet<>();
        final List<Thread> newThreads = new ArrayList<>();
        for (Thread thread: threads) {
            final int[] stateTransitions = machine.transitions[thread.state];
            final Predicate[] statePredicates = machine.predicates[thread.state];
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

    public TokenPatternMatchResult search(final SemToken newToken) {
        this.processToken(newToken);
        int highestPriorityMatchingPattern = Integer.MAX_VALUE;
        TokenPatternMatchResult matchResult = null;
        for (Thread thread: threads) {
            int matchingPattern = machine.statesResults[thread.state];
            if ((matchingPattern >=0) && (matchingPattern < highestPriorityMatchingPattern)) {
                highestPriorityMatchingPattern = matchingPattern;
                matchResult = TokenPatternMatchResult.doesMatch(highestPriorityMatchingPattern, thread.groups, machine.multiGroupAllocator.get(matchingPattern));
            }
        }
        this.reset();
        return matchResult;
    }
}
