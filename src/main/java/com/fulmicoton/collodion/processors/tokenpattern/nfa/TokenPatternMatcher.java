package com.fulmicoton.collodion.processors.tokenpattern.nfa;

import com.fulmicoton.collodion.processors.tokenpattern.SemToken;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenPatternMatcher {

    List<Thread> threads;
    int offset = 0;
    private final Machine machine;

    private Thread createThread(final int maxAccessiblePatternId,
                                final int start,
                                final int stateId,
                                final Groups groups,
                                final int offset,
                                final int matchLength) {
        if ((this.machine.openGroups.length + this.machine.closeGroups.length) > 0) {
            final Groups newGroups = new Groups(this.machine.openGroups[stateId], this.machine.closeGroups[stateId], offset, matchLength, groups);
            return new Thread(start, maxAccessiblePatternId, stateId, newGroups, matchLength - 1);
        }
        else {
            return new Thread(start, maxAccessiblePatternId, stateId, groups, matchLength - 1);
        }
    }

    TokenPatternMatcher(final Machine machine) {
        this.machine = machine;
        this.reset(0);
    }

    public void reset(final int offset) {
        this.offset = offset;
        final Thread initialThread = this.createThread(-1, -1, 0, null, offset, 1);
        threads = Lists.newArrayList(initialThread);
    }

    public MultiMatcher matchers() {
        final TokenPatternMatchResult[] matchResults = new TokenPatternMatchResult[machine.numPatterns];
        for (final Thread thread: threads) {
            final int matchingPattern = machine.statesResults[thread.state];
            if (matchingPattern != -1) {
                final TokenPatternMatchResult matchResult = TokenPatternMatchResult.doesMatch(matchingPattern, thread.groups, machine.multiGroupAllocator.get(matchingPattern));
                matchResults[matchingPattern] = matchResult;
            }
        }
        for (int i = 0; i<machine.numPatterns; i++) {
            if (matchResults[i] == null) {
                matchResults[i] = TokenPatternMatchResult.doesNotMatch(i, machine.multiGroupAllocator.get(i));
            }
        }
        return new MultiMatcher(matchResults);
    }

    public void killThreadsWithLowerPriority(final int matchStart, final int patternId) {
        final List<Thread> trimmedThreads = Lists.newArrayList();
        for (final Thread thread: this.threads) {
            if (thread.start < matchStart) {
                trimmedThreads.add(thread);
            }
            else if (thread.start == matchStart) {
                if (thread.minAccessiblePatternId < patternId) {
                    trimmedThreads.add(thread);
                }
            }
        }
        this.threads = trimmedThreads;
    }

    public boolean hasActiveThreads() {
        return !this.threads.isEmpty();
    }

    // TODO consider putting position as an attribute
    public void processToken(final SemToken token) {
        this.offset++;
        final Set<Integer> states = new HashSet<>();
        final List<Thread> newThreads = new ArrayList<>();
        for (final Thread thread: this.threads) {
            if (thread.sleep > 0) {
                newThreads.add(thread);
                thread.sleep -= 1;
            }
            else {
                final int[] stateTransitions = machine.transitions[thread.state];
                final Predicate[] statePredicates = machine.predicates[thread.state];
                for (int i = 0; i < stateTransitions.length; i++) {
                    final Predicate predicate = statePredicates[i];
                    for (final int transitionLength : predicate.apply(token)) {
                        final int dest = stateTransitions[i];
                        if (states.add(dest)) {
                            final int newThreadStart;
                            if (thread.state == 0) {
                                newThreadStart = this.offset - 1;
                            } else {
                                newThreadStart = thread.start;
                            }
                            final int maxAccessiblePatternId = this.machine.minAccessiblePatternIds[dest];
                            final Thread newThread = this.createThread(maxAccessiblePatternId, newThreadStart, dest, thread.groups, offset, transitionLength);
                            newThreads.add(newThread);
                        }
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
        for (final Thread thread: this.threads) {
            final int matchingPattern = machine.statesResults[thread.state];
            if ((matchingPattern >=0) && (matchingPattern < highestPriorityMatchingPattern)) {
                highestPriorityMatchingPattern = matchingPattern;
                matchResult = TokenPatternMatchResult.doesMatch(highestPriorityMatchingPattern, thread.groups, machine.multiGroupAllocator.get(matchingPattern));
            }

        }
        return matchResult;
    }
}
