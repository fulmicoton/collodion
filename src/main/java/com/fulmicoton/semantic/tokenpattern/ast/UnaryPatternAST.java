package com.fulmicoton.semantic.tokenpattern.ast;

public abstract class UnaryPatternAST extends TokenPatternAST{

    final TokenPatternAST pattern;

    public UnaryPatternAST(TokenPatternAST pattern) {
        this.pattern = pattern;
    }

    public void allocateGroups(GroupAllocator groupAllocator) {
        this.pattern.allocateGroups(groupAllocator);
    }

}
