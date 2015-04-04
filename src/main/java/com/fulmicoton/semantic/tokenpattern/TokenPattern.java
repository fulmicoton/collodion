package com.fulmicoton.semantic.tokenpattern;


import com.fulmicoton.semantic.tokenpattern.ast.AST;
import com.fulmicoton.semantic.tokenpattern.ast.CapturingGroupAST;
import com.fulmicoton.semantic.tokenpattern.nfa.Machine;
import com.fulmicoton.semantic.tokenpattern.nfa.Matcher;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;

import java.util.Iterator;

public class TokenPattern {

    private final String patternStr;
    final AST ast;
    private final Machine<SemToken> machine;

    public TokenPattern(final String patternStr,
                        final AST ast,
                        final Machine<SemToken> machine) {
        this.patternStr = patternStr;
        this.machine = machine;
        this.ast = ast;
    }

    public String toString() {
        return "TokenPattern(" + this.patternStr + ")";
    }

    public static TokenPattern compile(final String pattern) {
        final GroupAllocator groupAllocator = new GroupAllocator();
        final AST ast = new CapturingGroupAST(AST.compile(pattern));
        final StateImpl<SemToken> initialState = new StateImpl<>();
        ast.allocateGroups(groupAllocator);
        final StateImpl<SemToken> endState = ast.buildMachine(initialState);
        final Machine<SemToken> machine = new Machine<>(initialState, endState, groupAllocator);
        return new TokenPattern(pattern, ast, machine);
    }

    public Matcher<SemToken> match(final Iterator<SemToken> tokens) {
        return machine.match(tokens);
    }


}
