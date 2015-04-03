package com.fulmicoton.semantic.tokenpattern.ast;


public abstract class BinaryPatternAST extends TokenPatternAST {

    protected final TokenPatternAST left;
    protected final TokenPatternAST right;

    public BinaryPatternAST(TokenPatternAST left, TokenPatternAST right) {
        this.left = left;
        this.right = right;
    }

    public final void allocateGroups(GroupAllocator groupAllocator) {
        this.localAllocateGroups(groupAllocator);
        this.left.allocateGroups(groupAllocator);
        this.right.allocateGroups(groupAllocator);
    }

    protected void localAllocateGroups(GroupAllocator groupAllocator) {
    }
}
