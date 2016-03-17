package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.tokenpattern.GroupAllocator;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.State;

public class CapturingGroupAST extends UnaryPatternAST {

    private int groupId = -1;
    private final AnnotationKey name;

    public CapturingGroupAST(final AST pattern, final AnnotationKey name) {
        super(pattern);
        this.name = name;
    }

    @Override
    public String toDebugString() {
        return "(?<" + this.name + ":" + this.groupId + ">" + this.pattern.toDebugString() + ")";
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
            this.groupId = groupAllocator.allocateNamedGroup(this.name);
        }
        this.pattern.allocateGroups(groupAllocator);
    }
}
