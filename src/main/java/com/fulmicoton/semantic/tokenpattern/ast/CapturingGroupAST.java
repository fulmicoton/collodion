package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

public class CapturingGroupAST extends UnaryPatternAST {

    private int groupId = -1;

    public CapturingGroupAST(TokenPatternAST pattern) {
        super(pattern);
    }

    @Override
    public String toDebugString() {
        return "(" + this.groupId + ":" + this.pattern.toDebugString() + ")";
    }

    @Override
    public StateImpl<SemToken> buildMachine(final StateImpl<SemToken> fromState) {
        final StateImpl<SemToken> virtualStateOpen = new StateImpl<>();
        virtualStateOpen.openGroup = this.groupId;
        fromState.addTransition(new EpsilonTransition<>(virtualStateOpen));
        final StateImpl<SemToken> patternStart = new StateImpl<>();
        virtualStateOpen.addTransition(new EpsilonTransition<>(patternStart));
        final StateImpl<SemToken> patternEnd = this.pattern.buildMachine(patternStart);
        patternEnd.closeGroup = this.groupId;
        final StateImpl<SemToken> groupEnd = new StateImpl<>();
        patternEnd.addTransition(new EpsilonTransition<>(groupEnd));
        return groupEnd;
    }

    @Override
    public void allocateGroups(final GroupAllocator groupAllocator) {
        if (this.groupId < 0) {
            this.groupId = groupAllocator.allocateGroup();
        }
        this.pattern.allocateGroups(groupAllocator);
    }
}
