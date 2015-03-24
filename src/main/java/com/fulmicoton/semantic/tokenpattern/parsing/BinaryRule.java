package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Token;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

public class BinaryRule<T> implements Rule<T> {

    final Rule<T> left;
    final Rule<T> right;

    public BinaryRule(final Rule left,
                      final Rule right)
    {
        this.left = left;
        this.right = right;
    }

    public static <T> Rule<T> makeSequence(final List<Rule<T>> ruleSequence) {
        if (ruleSequence.size() == 1) {
            return ruleSequence.get(0);
        }
        else {
            final Rule<T> tailRule = makeSequence(ruleSequence.subList(1, ruleSequence.size()));
            return new BinaryRule(ruleSequence.get(0), tailRule);
        }
    }

    public static <T> Rule<T> makeSequence(Rule<T>... ruleSequence) {
        final List<Rule<T>> ruleList = Arrays.asList(ruleSequence);
        return makeSequence(ruleList);
    }


    @Override
    public RuleMatcher<T> matcher(IndexBuilder<Rule<T>> indexBuilder) {
        final BinaryRule<T> rule = this;
        final int leftRuleId = indexBuilder.getId(this.left);
        final int rightRuleId = indexBuilder.getId(this.right);
        return new RuleMatcher<T>() {

            public int searchSplit(boolean[][][] table, int start, int totalLength) {
                for (int leftLength = 1; leftLength < totalLength; leftLength++) {
                    final int rightLength = totalLength - leftLength;
                    if ((table[leftRuleId][start][leftLength]) &&
                            (table[rightRuleId][start + leftLength][rightLength]))
                    {
                        return leftLength;
                    }
                }
                return -1;
            }

            @Override
            public boolean evaluate(boolean[][][] table, int start, int totalLength, final List<Token<T>> tokens) {
                final int split = this.searchSplit(table, start, totalLength);
                return split >= 0;
            }

            @Override
            public List<Match<T>> getMatches(boolean[][][] table, int start, int l) {
                final int split = this.searchSplit(table, start, l);
                return ImmutableList.of(
                    new Match<T>(rule.left, start, start + split),
                    new Match<T>(rule.right, start + split, start + l)
                );
            }
        };
    }

    public List<Rule<T>> dependencies() {
        return ImmutableList.of(this.left, this.right);
    }


}
