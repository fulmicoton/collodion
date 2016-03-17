package com.fulmicoton.collodion.processors.tokenpattern.ast;


import com.fulmicoton.collodion.processors.tokenpattern.GroupAllocator;

public abstract class BinaryPatternAST extends AST {

    final AST left;
    final AST right;

    public BinaryPatternAST(final AST left, final AST right) {
        this.left = left;
        this.right = right;
    }

    public final void allocateGroups(GroupAllocator groupAllocator) {
        this.localAllocateGroups(groupAllocator);
        this.left.allocateGroups(groupAllocator);
        this.right.allocateGroups(groupAllocator);
    }

    protected void localAllocateGroups(GroupAllocator groupAllocator) {}
}
