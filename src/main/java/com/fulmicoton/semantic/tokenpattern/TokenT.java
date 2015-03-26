package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.parsing.Match;
import com.fulmicoton.semantic.tokenpattern.parsing.Rule;
import com.fulmicoton.semantic.tokenpattern.parsing.RuleMatcher;
import com.google.common.collect.ImmutableList;

import java.util.List;

public enum TokenT implements Rule<TokenT>, RuleMatcher<TokenT> {

    OPEN_PARENTHESIS,

    CLOSE_PARENTHESIS,

    ANNOTATION {
        public Annotation parse(final String match) {
            final String annotationName = match.substring(0, match.length() - 1);
            return Annotation.of(annotationName);
        }
    },

    COUNT,

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
    public List<Match<TokenT>> getMatches(boolean[][][] table, int start, int l) {
        return ImmutableList.of();
    }

}
