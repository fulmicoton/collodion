package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.State;

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
    public State<SemToken> buildMachine(final State<SemToken> fromState) {
        final State<SemToken> virtualStateOpen = new State<>();
        virtualStateOpen.addOpenGroup(this.groupId);
        fromState.addEpsilon(virtualStateOpen);
        final State<SemToken> patternStart = new State<>();
        virtualStateOpen.addEpsilon(patternStart);
        final State<SemToken> patternEnd = this.pattern.buildMachine(patternStart);
        patternEnd.addCloseGroup(this.groupId);
        final State<SemToken> groupEnd = new State<>();
        patternEnd.addEpsilon(groupEnd);
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
