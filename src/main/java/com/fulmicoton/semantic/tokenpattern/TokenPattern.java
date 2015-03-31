package com.fulmicoton.semantic.tokenpattern;


import com.fulmicoton.semantic.tokenpattern.ast.SemToken;
import com.fulmicoton.semantic.tokenpattern.ast.TokenPatternAST;
import com.fulmicoton.semantic.tokenpattern.nfa.Machine;
import com.fulmicoton.semantic.tokenpattern.nfa.Matcher;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;

import java.util.Iterator;

public class TokenPattern {

    private final String patternStr;
    private final Machine<SemToken> machine;

    public TokenPattern(String patternStr, Machine<SemToken> machine) {
        this.patternStr = patternStr;
        this.machine = machine;
    }

    public static TokenPattern compile(final String pattern) {
        final TokenPatternAST tokenPatternAST = TokenPatternAST.compile(pattern);
        final SimpleState<SemToken> initialState = new SimpleState<>();
        final SimpleState<SemToken> endState = tokenPatternAST.buildMachine(initialState);
        final Machine<SemToken> machine = new Machine<>(initialState, endState);
        return new TokenPattern(pattern, machine);
    }

    public Matcher<SemToken> match(final Iterator<SemToken> tokens) {
        return machine.match(tokens);
    }

}
