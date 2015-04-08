package com.fulmicoton.semantic.tokenpattern.parsing;


import com.fulmicoton.common.Index;
import com.fulmicoton.multiregexp.Token;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class SequenceRule<T> implements Rule<T> {

    private final List<BinaryRule<T>> rules;

    private SequenceRule(final List<BinaryRule<T>> rules) {
        this.rules = rules;
    }

    /**
     * The quirky API is just here to enforce more than two arguments
     * at compile time.
     */
    @SafeVarargs
    public static <T> SequenceRule<T> seq(Rule<T> a, Rule<T> b, Rule<T>... others) {
        List<Rule<T>> rules = Lists.newArrayList();
        rules.add(a);
        rules.add(b);
        Collections.addAll(rules, others);
        return of(rules);
    }

    private static <T> SequenceRule<T> of(List<Rule<T>> rules) {
        final List<BinaryRule<T>> binaryRules = makeChain(rules);
        return new SequenceRule<>(binaryRules);
    }

    public static <T> List<BinaryRule<T>> makeChain(final List<Rule<T>> subRules) {
        if (subRules.size() == 2) {
            return ImmutableList.of(new BinaryRule<>(subRules.get(0), subRules.get(1)));
        }
        else {
            final List<BinaryRule<T>> tail = makeChain(subRules.subList(1, subRules.size()));
            final BinaryRule<T> head = new BinaryRule<>(subRules.get(0), tail.get(0));
            return ImmutableList.<BinaryRule<T>>builder()
                    .add(head)
                    .addAll(tail)
                    .build();
        }
    }


    @Override
    public RuleMatcher<T> matcher(Index<Rule<T>> indexBuilder) {

        final List<RuleMatcher<T>> ruleMatchers = Lists.newArrayList();
        for (Rule<T> rule: this.rules) {
            ruleMatchers.add(rule.matcher(indexBuilder));
        }
        return new RuleMatcher<T>() {

            @Override
            public boolean evaluate(boolean[][][] table, int start, int l, List<Token<T>> tokens) {
                return ruleMatchers.get(0).evaluate(table, start, l, tokens);
            }

            @Override
            public List<Match<T>> getMatches(boolean[][][] table, int start, int l) {
                final List<Match<T>> matches = Lists.newArrayList();
                Match<T> rightMatch = null;
                for (final RuleMatcher<T> ruleMatcher: ruleMatchers) {
                    final List<Match<T>> leftRightMatch = ruleMatcher.getMatches(table, start, l);
                    matches.add(leftRightMatch.get(0));
                    rightMatch  = leftRightMatch.get(1);
                    start = rightMatch.start;
                    l = rightMatch.length;
                }
                matches.add(rightMatch);
                return matches;
            }
        };
    }

}
