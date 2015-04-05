package com.fulmicoton.semantic.tokenpattern.nfa;


public class MultiMatcher {

    private final Matcher[] matchers;

    public MultiMatcher(final Matcher[] matchers) {
        this.matchers = matchers;
    }

    public Matcher get(final int tokenPatternId) {
        return this.matchers[tokenPatternId];
    }
}
