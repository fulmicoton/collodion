package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.tokenpattern.PatternTokenType;

import java.util.List;

public class Litteral implements Rule,  RuleMatcher {


    private final PatternTokenType patternTokenType;

    private Litteral(PatternTokenType patternTokenType) {
        this.patternTokenType = patternTokenType;
    }

    public static Litteral of(PatternTokenType tokenType) {
        return new Litteral(tokenType);
    }

    @Override
    public boolean evaluate(boolean[][][] table, int start, int l, final List<Token<PatternTokenType>> tokens) {
        if (l != 1) {
            return false;
        }
        return tokens.get(start).type == this.patternTokenType;
    }

    @Override
    public RuleMatcher matcher(IndexBuilder<Rule> indexBuilder) {
        return this;

    }

    @Override
    public List<Rule> dependencies() {
        return null;
    }
}
