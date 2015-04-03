package com.fulmicoton.semantic.tokenpattern.nfa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Thread<T> {

    private final State<T> state;
    private final Groups groups;

    State<T> getState() {
        return this.state;
    }

    Groups groups() {
        return this.groups;
    }

    Thread(final State<T> state) {
        this(state, null, 0);
    }

    Thread(final State<T> state,
           final Groups groups,
           final int tokenId) {
        this.state = state;
        this.groups = state.updateGroups(groups, tokenId);
    }

    public List<Thread<T>> transition(final T token,
                                      final int tokenId,
                                      final Set<State<T>> states) {
        List<Thread<T>> newThreads = new ArrayList<>();
        for (State<T> nextState: this.state.transition(token)) {
            if (!states.contains(nextState)) {
                states.add(nextState);
                final Thread newThread = new Thread(nextState, this.groups, tokenId);
                newThreads.add(newThread);
            }
        }
        return newThreads;
    }
}
