package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.tokenpattern.ast.AST;
import com.fulmicoton.collodion.processors.tokenpattern.ast.CapturingGroupAST;
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

    // only for test.
    public static TokenPattern compile(final String pattern) throws Exception {
        final MachineBuilder machine = new MachineBuilder();
        final AST patternAST = AST.compile(pattern);
        final CapturingGroupAST capturingGroupAST = new CapturingGroupAST(patternAST, AnnotationKey.of("TEST"));
        final int patternId = machine.addPattern(capturingGroupAST);
        return new TokenPattern(pattern, patternId, machine.buildForMatch());
    }

    public TokenPatternMatchResult match(final Iterator<SemToken> tokens) {
        return machine.match(tokens).get(this.patternId);
    }

    public TokenPatternMatcher matcher() {
        return machine.matcher();
    }
}
