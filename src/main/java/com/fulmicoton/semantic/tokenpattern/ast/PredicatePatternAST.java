package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.Transition;
import com.fulmicoton.semantic.tokenpattern.nfa.State;
import com.fulmicoton.semantic.tokenpattern.nfa.Arrow;
import com.google.common.base.Predicate;

public class PredicatePatternAST extends AST {

    private final String str;
    private final Predicate<SemToken> predicate;

    protected PredicatePatternAST(final String str,
                                  final Predicate<SemToken> predicate) {
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
        final Arrow<SemToken> transition = new Transition<>(targetState, this.predicate);
        fromState.addTransition(transition);
        return targetState;
    }

    public void allocateGroups(final GroupAllocator groupAllocator) {}
}
