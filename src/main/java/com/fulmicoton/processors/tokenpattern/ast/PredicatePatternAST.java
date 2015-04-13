package com.fulmicoton.processors.tokenpattern.ast;

import com.fulmicoton.processors.tokenpattern.GroupAllocator;
import com.fulmicoton.processors.tokenpattern.nfa.Predicate;
import com.fulmicoton.processors.tokenpattern.nfa.State;
import com.sun.istack.internal.NotNull;

public class PredicatePatternAST extends AST {

    private final String str;
    private final Predicate predicate;

    protected PredicatePatternAST(@NotNull final String str,
                                  @NotNull final Predicate predicate) {
        this.str = str;
        this.predicate = predicate;
    }

    @Override
    public String toDebugString() {
        return this.str;
    }

    @Override
    public State buildMachine(final State fromState) {
        return fromState.transition(this.predicate);
    }

    public void allocateGroups(final GroupAllocator groupAllocator) {}
}
