package com.fulmicoton.processors.tokenpattern.ast;


import com.fulmicoton.processors.tokenpattern.GroupAllocator;

public abstract class BinaryPatternAST extends AST {

    protected final AST left;
    protected final AST right;

    public BinaryPatternAST(AST left, AST right) {
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
