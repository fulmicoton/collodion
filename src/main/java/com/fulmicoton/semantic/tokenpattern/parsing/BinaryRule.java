package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.tokenpattern.PatternTokenType;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

class BinaryRule implements Rule {

    final Rule left;
    final Rule right;

    public BinaryRule(final Rule left,
                      final Rule right)
    {
        this.left = left;
        this.right = right;
    }

    public static Rule makeSequence(final List<Rule> ruleSequence) {
        if (ruleSequence.size() == 1) {
            return ruleSequence.get(0);
        }
        else {
            final Rule tailRule = makeSequence(ruleSequence.subList(1, ruleSequence.size()));
            return new BinaryRule(ruleSequence.get(0), tailRule);
        }
    }

    public static Rule makeSequence(Rule... ruleSequence) {
        return makeSequence(Arrays.asList(ruleSequence));
    }


    @Override
    public RuleMatcher matcher(IndexBuilder<Rule> indexBuilder) {
        final int leftRuleId = indexBuilder.getId(this.left);
        final int rightRuleId = indexBuilder.getId(this.right);
        return new RuleMatcher() {

            @Override
            public boolean evaluate(boolean[][][] table, int start, int totalLength, final List<Token<PatternTokenType>> tokens) {
                for (int leftLength = 1; leftLength<totalLength - 1; leftLength++) {
                    final int rightLength = totalLength - leftLength;
                    if ((table[leftRuleId][start][leftLength]) &&
                        (table[rightRuleId][start + leftLength][rightLength]))
                    {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public List<Rule> dependencies() {
        return ImmutableList.of(this.left, this.right);
    }


}
