package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.tokenpattern.ast.AST;
import com.fulmicoton.semantic.tokenpattern.ast.CapturingGroupAST;
import com.fulmicoton.semantic.tokenpattern.nfa.Machine;
import com.fulmicoton.semantic.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.semantic.tokenpattern.nfa.Matcher;
import com.fulmicoton.semantic.tokenpattern.nfa.State;

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
        final State<SemToken> initialState = new State<>();
        ast.allocateGroups(groupAllocator);
        final State<SemToken> endState = ast.buildMachine(initialState);
        final MachineBuilder<SemToken> machine = new MachineBuilder<>(initialState, endState, groupAllocator);
        return new TokenPattern(pattern, ast, machine.build());
    }

    public Matcher<SemToken> match(final Iterator<SemToken> tokens) {
        return machine.match(tokens);
    }


}
