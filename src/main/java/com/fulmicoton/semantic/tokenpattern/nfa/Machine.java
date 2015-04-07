package com.fulmicoton.semantic.tokenpattern.nfa;

import com.fulmicoton.semantic.tokenpattern.MultiGroupAllocator;
import com.fulmicoton.semantic.tokenpattern.SemToken;

import java.util.Iterator;


public class Machine {

    final int[] statesResults;
    final int nbPatterns;
    final int[][] transitions;
    final Predicate[][] predicates;
    final int[][] openGroups;
    final int[][] closeGroups;
    final MultiGroupAllocator multiGroupAllocator;

    public Machine(final int[] statesResults,
                   final int nbPatterns,
                   final int[][] transitions,
                   final Predicate[][] predicates,
                   final int[][] openGroups,
                   final int[][] closeGroups,
                   final MultiGroupAllocator multiGroupAllocator) {
        this.statesResults = statesResults;
        this.nbPatterns = nbPatterns;
        this.transitions = transitions;
        this.predicates = predicates;
        this.openGroups = openGroups;
        this.closeGroups = closeGroups;
        this.multiGroupAllocator = multiGroupAllocator;
    }


    public TokenPatternMatcher matcher() {
        return new TokenPatternMatcher(this);
    }

    public MultiMatcher match(final Iterator<SemToken> tokens) {
        TokenPatternMatcher runner = this.matcher();
        while (tokens.hasNext()) {
            runner.processToken(tokens.next());
        }
        return runner.matchers();
    }

    /*
    public Multi search(Iterator<SemToken> tokens) {
    }
    */
}
