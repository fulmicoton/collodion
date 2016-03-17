package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.processors.tokenpattern.nfa.Machine;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.TokenPatternMatchResult;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.TokenPatternMatcher;

import java.util.Iterator;

public class TokenPattern {

    private final String patternStr;
    private final int patternId;
    private final Machine machine;

    public TokenPattern(final String patternStr,
                        final int patternId,
                        final Machine machine) {
        this.patternStr = patternStr;
        this.patternId = patternId;
        this.machine = machine;
    }

    public String toString() {
        return "TokenPattern(" + this.patternStr + ")";
    }

    public static TokenPattern compile(final String pattern) {
        final MachineBuilder machine = new MachineBuilder();
        final int patternId = machine.addPattern(pattern);
        return new TokenPattern(pattern, patternId, machine.buildForMatch());
    }

    public TokenPatternMatchResult match(final Iterator<SemToken> tokens) {
        return machine.match(tokens).get(this.patternId);
    }

    public TokenPatternMatcher matcher() {
        return machine.matcher();
    }
}
