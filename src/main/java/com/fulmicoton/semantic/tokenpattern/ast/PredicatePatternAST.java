package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.ConditionalTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;
import com.fulmicoton.semantic.tokenpattern.nfa.Transition;
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
    public StateImpl<SemToken> buildMachine(final StateImpl<SemToken> fromState) {
        final StateImpl<SemToken> targetState = new StateImpl<>();
        final Transition<SemToken> transition = new ConditionalTransition<>(targetState, this.predicate);
        fromState.addTransition(transition);
        return targetState;
    }

    public void allocateGroups(final GroupAllocator groupAllocator) {}
}
