package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.tokenpattern.GroupAllocator;

public abstract class UnaryPatternAST extends AST {

    final AST pattern;

    public UnaryPatternAST(AST pattern) {
        this.pattern = pattern;
    }

    public void allocateGroups(GroupAllocator groupAllocator) {
        this.pattern.allocateGroups(groupAllocator);
    }

}
