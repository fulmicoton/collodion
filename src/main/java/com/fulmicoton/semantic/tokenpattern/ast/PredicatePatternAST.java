package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.Transition;
import com.fulmicoton.semantic.tokenpattern.nfa.State;
import com.fulmicoton.semantic.tokenpattern.nfa.Arrow;
import com.google.common.base.Predicate;
import com.sun.istack.internal.NotNull;

public class PredicatePatternAST extends AST {

    private final String str;
    private final Predicate<SemToken> predicate;

    protected PredicatePatternAST(@NotNull final String str,
                                  @NotNull final Predicate<SemToken> predicate) {
        this.str = str;
        this.predicate = predicate;
    }

    @Override
    public String toDebugString() {
        return this.str;
    }

    @Override
    public State<SemToken> buildMachine(final State<SemToken> fromState) {
        final State<SemToken> targetState = new State<>();
        fromState.addTransition(this.predicate, targetState);
        return targetState;
    }

    public void allocateGroups(final GroupAllocator groupAllocator) {}
}
