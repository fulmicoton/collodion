package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
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
    public RuleMatcher<T> matcher(IndexBuilder<Rule<T>> indexBuilder) {
        final int[] ruleIds = new int[this.rules.size()];
        final List<Rule> rules = new ArrayList<Rule>();

        for (int i=0; i<this.rules.size(); i++) {
            final Rule rule = this.rules.get(i);
            ruleIds[i] = indexBuilder.getId(rule);
        }
        return new RuleMatcher<T>() {
            private int getMatchingRuleId(boolean[][][] table, int start, int l) {
                for (int ruleId: ruleIds) {
                    if (table[ruleId][start][l]) {
                        return ruleId;
                    }
                }
                return -1;
            }

            @Override
            public boolean evaluate(boolean[][][] table, int start, int l, List<Token<T>> tokens) {
                return this.getMatchingRuleId(table, start, l) > 0;
            }

            @Override
            public List<Match<T>> getMatches(boolean[][][] table, int start, int l) {
                final int ruleId =  this.getMatchingRuleId(table, start, l);
                final Rule rule = rules.get(Ints.indexOf(ruleIds, ruleId));
                final Match<T> match = new Match<T>(rule, start, l);
                return ImmutableList.of(match);
            }
        };
    }

    @Override
    public List<Rule<T>> dependencies() {
        return rules;
    }
}
