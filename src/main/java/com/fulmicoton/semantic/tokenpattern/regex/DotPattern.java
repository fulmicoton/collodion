package com.fulmicoton.semantic.tokenpattern.regex;


public class DotPattern extends PredicatePattern {

    @Override
    public String toDebugString() {
        return ".";
    }

    @Override
    public boolean apply(SemToken semToken) {
        return true;
    }
}
