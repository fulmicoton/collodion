package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.tokenpattern.GroupAllocator;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.State;

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
    public State buildMachine(final State fromState) {
        final State virtualStateOpen = new State();
        virtualStateOpen.addOpenGroup(this.groupId);
        fromState.addEpsilon(virtualStateOpen);
        final State patternStart = new State();
        virtualStateOpen.addEpsilon(patternStart);
        final State patternEnd = this.pattern.buildMachine(patternStart);
        patternEnd.addCloseGroup(this.groupId);
        final State groupEnd = new State();
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
