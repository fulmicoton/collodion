package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.multiregexp.Lexer;
import static com.fulmicoton.semantic.tokenpattern.TokenT.*;
import com.fulmicoton.semantic.tokenpattern.parsing.Literal;
import com.fulmicoton.semantic.tokenpattern.parsing.OrRule;
import com.fulmicoton.semantic.tokenpattern.parsing.Rule;

public class TokenPattern {

    final static Lexer<TokenT> LEXER = new Lexer<TokenT>()
        .addRule(OPEN_PARENTHESIS, "\\(")
        .addRule(CLOSE_PARENTHESIS, "\\)")
        .addRule(PLUS, "\\+")
        .addRule(DOT, "\\.")
        .addRule(STAR, "\\*")
        .addRule(QUESTION_MARK, "\\?")
        .addRule(COUNT, "\\{[0-9]+\\}")
        .addRule(COUNT, "\\{[0-9]+,[0-9]+\\}")
        .addRule(ANNOTATION, "\\<[a-zA-Z\\.]+\\>")
    ;

    private static Rule<TokenT> buildGrammar() {
        final OrRule<TokenT> expr = new OrRule<>();
        return expr
            .addSeqRule(OPEN_PARENTHESIS, expr, CLOSE_PARENTHESIS)
            .addSeqRule(expr, STAR)
            .addSeqRule(expr, PLUS)
            .addSeqRule(expr, QUESTION_MARK)
            .addSeqRule(expr, COUNT)
            .addSeqRule(ANNOTATION);
    }

    final static Rule<TokenT> GRAMMAR = buildGrammar();



}
