package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.multiregexp.Lexer;
import static com.fulmicoton.semantic.tokenpattern.TokenT.*;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.parsing.BinaryRule;
import com.fulmicoton.semantic.tokenpattern.parsing.Emitter;
import com.fulmicoton.semantic.tokenpattern.parsing.Grammar;
import com.fulmicoton.semantic.tokenpattern.parsing.Rule;

import java.util.List;

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

    private static Grammar buildGrammar() {
        final Grammar<TokenT, TokenPattern> grammar = new Grammar<>();
        final Rule<TokenT> EXPR = grammar.expr;
        return grammar
            .addRule(BinaryRule.makeSequence(OPEN_PARENTHESIS, EXPR, CLOSE_PARENTHESIS),
                    new Emitter<TokenT, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<TokenT>> tokens) {
                            return childrenEmission.get(1);
                        }
                    })
            .addRule(BinaryRule.makeSequence(EXPR, STAR),
                    new Emitter<TokenT, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<TokenT>> tokens) {
                            return new StarPattern(childrenEmission.get(0));
                        }
                    })
            .addRule(BinaryRule.makeSequence(EXPR, PLUS),
                    new Emitter<TokenT, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<TokenT>> tokens) {
                            final TokenPattern pattern = childrenEmission.get(0);
                            return new ChainPattern(pattern, new StarPattern(pattern));
                        }
                    })
            .addRule(BinaryRule.makeSequence(EXPR, QUESTION_MARK),
                    new Emitter<TokenT, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<TokenT>> tokens) {
                            return new RepeatPattern(childrenEmission.get(0), 0, 1);
                        }
                    })
            .addRule(ANNOTATION,
                    new Emitter<TokenT, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<TokenT>> tokens) {
                            final String match = tokens.get(0).str;
                            final String annotationName = match.substring(0, match.length() - 1);
                            final Annotation annotation = Annotation.of(annotationName);
                            return new AnnotationPattern(annotation);
                        }
                    })
            .addRule(new BinaryRule(EXPR, EXPR),
                    new Emitter<TokenT, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<TokenT>> tokens) {
                            return new ChainPattern(childrenEmission.get(0), childrenEmission.get(1));
                        }
                    });
    }

    final static Grammar<TokenT, TokenPattern> GRAMMAR = buildGrammar();

}
