package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.tokenpattern.nfa.Machine;
import com.fulmicoton.semantic.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.semantic.tokenpattern.nfa.Matcher;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenPattern that = (TokenPattern) o;
        if (!patternStr.equals(that.patternStr)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return patternStr.hashCode();
    }

    public String toString() {
        return "TokenPattern(" + this.patternStr + ")";
    }

    public static TokenPattern compile(final String pattern) {
        final MachineBuilder machine = new MachineBuilder();
        int patternId = machine.add(pattern);
        return new TokenPattern(pattern, patternId, machine.build());
    }

    public Matcher match(final Iterator<SemToken> tokens) {
        return machine.match(tokens).get(this.patternId);
    }


}
