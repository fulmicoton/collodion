package com.fulmicoton.semantic.tokenpattern.ast;


public class DotPatternAST extends PredicatePatternAST {

    @Override
    public String toDebugString() {
        return ".";
    }

    @Override
    public boolean apply(SemToken semToken) {
        return true;
    }
}
