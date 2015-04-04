package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.EpsilonTransition;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

public class CapturingGroupAST extends UnaryPatternAST {

    private int groupId = -1;
    private final String name;

    public CapturingGroupAST(AST pattern) {
        this(pattern, null);
    }

    public CapturingGroupAST(AST pattern, final String name) {
        super(pattern);
        this.name = name;
    }

    @Override
    public String toDebugString() {
        if (this.name == null) {
            return "(" + this.groupId + ":" + this.pattern.toDebugString() + ")";
        }
        else {
            return "(?<" + this.name + ":" + this.groupId + ">" + this.pattern.toDebugString() + ")";
        }
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
            if (this.name == null) {
                this.groupId = groupAllocator.allocateUnnamedGroup();
            }
            else {
                this.groupId = groupAllocator.allocateNamedGroup(this.name);
            }
        }
        this.pattern.allocateGroups(groupAllocator);
    }
}
