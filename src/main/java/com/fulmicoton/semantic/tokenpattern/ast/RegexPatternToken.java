package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.tokenpattern.parsing.Match;
import com.fulmicoton.semantic.tokenpattern.parsing.Rule;
import com.fulmicoton.semantic.tokenpattern.parsing.RuleMatcher;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * The list of tokens used to parse a token regular expression language.
 * Yeah, that sounds a bit meta and incestuous, but one gets
 * used to it.
 *
 * The actual lexing rules are in the AST class.
 */
public enum RegexPatternToken implements Rule<RegexPatternToken>, RuleMatcher<RegexPatternToken> {

    OPEN_NON_GROUPING,
    OPEN_NAMED_GROUP,
    OPEN_PARENTHESIS,
    CLOSE_PARENTHESIS,
    ANNOTATION,
    OR,
    COUNT,
    DOT,
    PLUS,
    STAR,
    QUESTION_MARK;

    @Override
    public boolean evaluate(boolean[][][] table, int start, int l, final List<Token<RegexPatternToken>> tokens) {
        if (l != 1) {
            return false;
        }
        return tokens.get(start).type == this;
    }

    @Override
    public RuleMatcher<RegexPatternToken> matcher(IndexBuilder<Rule<RegexPatternToken>> indexBuilder) {
        return this;
    }

    @Override
    public List<Match<RegexPatternToken>> getMatches(boolean[][][] table, int start, int l) {
        return ImmutableList.of();
    }

}
