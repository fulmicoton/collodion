package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.multiregexp.Lexer;

public class TokenPattern {

    final static Lexer<PatternTokenType> LEXER = new Lexer<PatternTokenType>()
        .addRule(PatternTokenType.OPEN_PARENTHESIS, "\\(")
        .addRule(PatternTokenType.CLOSE_PARENTHESIS, "\\)")
        .addRule(PatternTokenType.PLUS, "\\+")
        .addRule(PatternTokenType.DOT, "\\.")
        .addRule(PatternTokenType.STAR, "\\*")
        .addRule(PatternTokenType.QUESTION_MARK, "\\?")
        .addRule(PatternTokenType.COUNT, "\\{[0-9]+\\}")
        .addRule(PatternTokenType.COUNT, "\\{[0-9]+,[0-9]+\\}")
        .addRule(PatternTokenType.ANNOTATION, "\\<[a-zA-Z\\.]+\\>")
    ;




}
