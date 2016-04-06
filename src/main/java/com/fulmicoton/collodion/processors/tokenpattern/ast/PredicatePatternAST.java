package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.tokenpattern.GroupAllocator;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.Predicate;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.State;

import javax.validation.constraints.NotNull;

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
    public State buildMachine(final int patternId, final State fromState) {
        return fromState.transition(patternId, this.predicate);
    }

    public void allocateGroups(final GroupAllocator groupAllocator) {}
}
