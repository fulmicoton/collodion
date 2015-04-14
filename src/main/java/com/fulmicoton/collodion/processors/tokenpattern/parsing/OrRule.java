package com.fulmicoton.collodion.processors.tokenpattern.parsing;

import com.fulmicoton.collodion.common.Index;
import com.fulmicoton.multiregexp.Token;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.List;

public class OrRule<T> implements Rule<T> {

    final List<Rule<T>> rules = new ArrayList<>();

    public OrRule addRule(Rule<T> rule) {
        this.rules.add(rule);
        return this;
    }

    @Override
    public RuleMatcher<T> matcher(Index<Rule<T>> indexBuilder) {
        final int[] ruleIds = new int[this.rules.size()];
        final List<Rule<T>> rules = this.rules;

        for (int i=0; i<this.rules.size(); i++) {
            final Rule<T> rule = this.rules.get(i);
            ruleIds[i] = indexBuilder.get(rule);
        }
        return new RuleMatcher<T>() {
            private int getMatchingRuleId(boolean[][][] table, int start, int length) {
                for (int ruleId: ruleIds) {
                    if (table[ruleId][start][length]) {
                        return ruleId;
                    }
                }
                return -1;
            }

            @Override
            public boolean evaluate(boolean[][][] table, int start, int l, List<Token<T>> tokens) {
                return this.getMatchingRuleId(table, start, l) >= 0;
            }

            @Override
            public List<Match<T>> getMatches(boolean[][][] table, int start, int length) {
                final int ruleId =  this.getMatchingRuleId(table, start, length);
                final Rule<T> rule = rules.get(Ints.indexOf(ruleIds, ruleId));
                final Match<T> match = new Match<>(rule, start, length);
                return ImmutableList.of(match);
            }
        };
    }
}
