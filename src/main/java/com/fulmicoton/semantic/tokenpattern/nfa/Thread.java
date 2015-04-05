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
           Groups groups,
           final int tokenId) {
        this.state = state;
        for (int openGroup: state.allOpenGroups()) {
            groups = Groups.openGroup(groups, openGroup, tokenId);
        }
        for (int closeGroup: state.allCloseGroups()) {
            groups = Groups.closeGroup(groups, closeGroup, tokenId);
        }
        this.groups = groups;
    }

    public List<Thread<T>> transition(final T token,
                                      final int tokenId,
                                      final Set<State<T>> states) {
        List<Thread<T>> newThreads = new ArrayList<>();
        for (State<T> nextState: this.state.transition(token)) {
            if (!states.contains(nextState)) {
                states.add(nextState);
                final Thread<T> newThread = new Thread<>(nextState, this.groups, tokenId);
                newThreads.add(newThread);
            }
        }
        return newThreads;
    }
}
