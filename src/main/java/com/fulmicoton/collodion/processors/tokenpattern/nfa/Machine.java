package com.fulmicoton.collodion.processors.tokenpattern.nfa;

import com.fulmicoton.collodion.processors.tokenpattern.MultiGroupAllocator;
import com.fulmicoton.collodion.processors.tokenpattern.SemToken;

import java.util.Iterator;


public class Machine {

    final int[] statesResults;
    final int numPatterns;
    final int[][] transitions;
    final int[] minAccessiblePatternIds;
    final Predicate[][] predicates;
    final int[][] openGroups;
    final int[][] closeGroups;
    final MultiGroupAllocator multiGroupAllocator;

    public Machine(final int[] statesResults,
                   final int numPatterns,
                   final int[] minAccessiblePatternIds,
                   final int[][] transitions,
                   final Predicate[][] predicates,
                   final int[][] openGroups,
                   final int[][] closeGroups,
                   final MultiGroupAllocator multiGroupAllocator) {
        this.statesResults = statesResults;
        this.numPatterns = numPatterns;
        this.minAccessiblePatternIds = minAccessiblePatternIds;
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
        int offset = 0;
        final TokenPatternMatcher runner = this.matcher();
        while (tokens.hasNext()) {
            runner.processToken(offset, tokens.next());
            offset += 1;
        }
        return runner.matchers();
    }

}
