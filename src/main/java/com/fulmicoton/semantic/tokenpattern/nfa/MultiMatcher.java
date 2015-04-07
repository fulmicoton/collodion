package com.fulmicoton.semantic.tokenpattern.nfa;


public class MultiMatcher {

    private final TokenPatternMatchResult[] matchResult;

    public MultiMatcher(final TokenPatternMatchResult[] matchResult) {
        this.matchResult = matchResult;
    }

    public TokenPatternMatchResult get(final int tokenPatternId) {
        return this.matchResult[tokenPatternId];
    }
}
