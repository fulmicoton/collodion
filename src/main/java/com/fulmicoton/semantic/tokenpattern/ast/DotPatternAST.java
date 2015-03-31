package com.fulmicoton.semantic.tokenpattern.ast;


import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class DotPatternAST extends PredicatePatternAST {

    @Override
    public String toDebugString() {
        return ".";
    }

    @Override
    public Predicate<SemToken> predicate() {
        return Predicates.alwaysTrue();
    }
}
