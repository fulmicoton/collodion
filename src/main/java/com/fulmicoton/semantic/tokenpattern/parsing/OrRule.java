package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Token;

import java.util.List;

public class OrRule<T> implements Rule<T> {

    final List<Rule<T>> rules;

    public OrRule(List<Rule<T>> rules) {
        this.rules = rules;
    }

    public OrRule addRule(Rule<T> rule) {
        this.rules.add(rule);
        return this;
    }

    @Override
    public RuleMatcher<T> matcher(IndexBuilder<Rule<T>> indexBuilder) {
        final int[] ruleIds = new int[this.rules.size()];
        for (int i=0; i<this.rules.size(); i++) {
            final Rule rule = this.rules.get(i);
            ruleIds[i] = indexBuilder.getId(rule);
        }
        return new RuleMatcher<T>() {
            @Override
            public boolean evaluate(boolean[][][] table, int start, int l, List<Token<T>> tokens) {
                for (int ruleId: ruleIds) {
                    if (table[ruleId][start][l]) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    public List<Rule<T>> dependencies() {
        return rules;
    }
}
