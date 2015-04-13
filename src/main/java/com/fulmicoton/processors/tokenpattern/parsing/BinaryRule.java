package com.fulmicoton.processors.tokenpattern.parsing;

import com.fulmicoton.common.Index;
import com.fulmicoton.multiregexp.Token;
import com.google.common.collect.ImmutableList;

import java.util.List;

class BinaryRule<T> implements Rule<T> {

    final Rule<T> left;
    final Rule<T> right;

    public BinaryRule(final Rule<T> left,
                      final Rule<T> right)
    {
        this.left = left;
        this.right = right;
    }

    @Override
    public RuleMatcher<T> matcher(Index<Rule<T>> indexBuilder) {
        final BinaryRule<T> rule = this;
        final int leftRuleId = indexBuilder.get(this.left);
        final int rightRuleId = indexBuilder.get(this.right);
        return new RuleMatcher<T>() {

            public int searchLeftLength(final boolean[][][] table, final int start, final int totalLength) {
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
                final int leftLength = this.searchLeftLength(table, start, totalLength);
                return leftLength >= 1;
            }

            @Override
            public List<Match<T>> getMatches(boolean[][][] table, int start, int totalLength) {
                final int leftLength = this.searchLeftLength(table, start, totalLength);
                final int rightLength = totalLength - leftLength;
                return ImmutableList.of(
                    new Match<>(rule.left, start, leftLength),
                    new Match<>(rule.right, start + leftLength, rightLength)
                );
            }
        };
    }

}
