package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.tokenpattern.PatternTokenType;

import java.util.List;

public interface RuleMatcher {
    boolean evaluate(boolean[][][] table, int start, int l, final List<Token<PatternTokenType>> tokens);
}
