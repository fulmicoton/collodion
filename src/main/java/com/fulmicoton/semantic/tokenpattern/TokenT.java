package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.parsing.Rule;
import com.fulmicoton.semantic.tokenpattern.parsing.RuleMatcher;
import com.google.common.collect.ImmutableList;

import java.util.List;

public enum TokenT implements Rule<TokenT>, RuleMatcher<TokenT> {

    OPEN_PARENTHESIS,

    CLOSE_PARENTHESIS,

    ANNOTATION {
        Annotation parse(final String match) {
            final String annotationName = match.substring(0, match.length() - 1);
            return Annotation.of(annotationName);
        }
    },

    COUNT {
        CountParam parse(final String match) throws ParsingError {
            final String countString = match.substring(0, match.length() - 1);
            String[] parts = countString.split(",");
            if (parts.length == 1) {
                int val = Integer.valueOf(parts[0]);
                return new CountParam(val, val);
            }
            else {
                int minCount = Integer.valueOf(parts[0]);
                int maxCount = Integer.valueOf(parts[1]);
                return new CountParam(minCount, maxCount);
            }
        }
    },

    DOT,

    PLUS,

    STAR,

    QUESTION_MARK;


    @Override
    public boolean evaluate(boolean[][][] table, int start, int l, final List<Token<TokenT>> tokens) {
        if (l != 1) {
            return false;
        }
        return tokens.get(start).type == this;
    }

    @Override
    public RuleMatcher<TokenT> matcher(IndexBuilder<Rule<TokenT>> indexBuilder) {
        return this;

    }

    @Override
    public List<Rule<TokenT>> dependencies() {
        return ImmutableList.of();
    }

}
