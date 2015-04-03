package com.fulmicoton.semantic.tokenpattern;


import com.fulmicoton.semantic.tokenpattern.ast.GroupAllocator;
import com.fulmicoton.semantic.tokenpattern.ast.TokenPatternAST;
import com.fulmicoton.semantic.tokenpattern.nfa.Machine;
import com.fulmicoton.semantic.tokenpattern.nfa.Matcher;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

import java.util.Iterator;

public class TokenPattern {

    private final String patternStr;
    private final Machine<SemToken> machine;

    public TokenPattern(String patternStr, Machine<SemToken> machine) {
        this.patternStr = patternStr;
        this.machine = machine;
    }

    public String toString() {
        return "TokenPattern(" + this.patternStr + ")";
    }

    public static TokenPattern compile(final String pattern) {
        final TokenPatternAST tokenPatternAST = TokenPatternAST.compile(pattern);
        final StateImpl<SemToken> initialState = new StateImpl<>();
        final GroupAllocator groupAllocator = new GroupAllocator();
        tokenPatternAST.allocateGroups(groupAllocator);
        final StateImpl<SemToken> endState = tokenPatternAST.buildMachine(initialState);
        final Machine<SemToken> machine = new Machine<>(initialState, endState, groupAllocator);
        return new TokenPattern(pattern, machine);
    }

    public Matcher<SemToken> match(final Iterator<SemToken> tokens) {
        return machine.match(tokens);
    }


}
