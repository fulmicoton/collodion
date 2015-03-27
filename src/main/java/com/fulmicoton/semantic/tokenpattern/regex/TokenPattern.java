package com.fulmicoton.semantic.tokenpattern.regex;

import com.fulmicoton.multiregexp.Lexer;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;
import com.fulmicoton.semantic.tokenpattern.parsing.Emitter;
import com.fulmicoton.semantic.tokenpattern.parsing.Grammar;
import com.fulmicoton.semantic.tokenpattern.parsing.LRParser;
import com.fulmicoton.semantic.tokenpattern.parsing.Rule;

import java.util.List;

import static com.fulmicoton.semantic.tokenpattern.parsing.SequenceRule.seq;

public abstract class TokenPattern {

    final static Lexer<RegexPatternToken> LEXER = new Lexer<RegexPatternToken>()
        .addRule(RegexPatternToken.OPEN_PARENTHESIS, "\\(")
        .addRule(RegexPatternToken.CLOSE_PARENTHESIS, "\\)")
        .addRule(RegexPatternToken.PLUS, "\\+")
        .addRule(RegexPatternToken.DOT, "\\.")
        .addRule(RegexPatternToken.STAR, "\\*")
        .addRule(RegexPatternToken.QUESTION_MARK, "\\?")
        .addRule(RegexPatternToken.COUNT, "\\{[0-9]+\\}")
        .addRule(RegexPatternToken.COUNT, "\\{[0-9]+,[0-9]+\\}")
        .addRule(RegexPatternToken.ANNOTATION, "\\<[a-zA-Z\\.]+\\>")
    ;

    public abstract String toDebugString();

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.toDebugString() +"]";
    }

    private static Grammar<RegexPatternToken, TokenPattern>  buildGrammar() {
        final Grammar<RegexPatternToken, TokenPattern> grammar = new Grammar<>();
        final Rule<RegexPatternToken> EXPR = grammar.expr;
        return grammar
            .addRule(seq(RegexPatternToken.OPEN_PARENTHESIS, EXPR, RegexPatternToken.CLOSE_PARENTHESIS),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return childrenEmission.get(1);
                        }
                    })
            .addRule(RegexPatternToken.DOT, new Emitter<RegexPatternToken, TokenPattern>() {
                @Override
                public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                    return new DotPattern();
                }
            })
            .addRule(seq(EXPR, RegexPatternToken.STAR),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new StarPattern(childrenEmission.get(0));
                        }
                    })
            .addRule(seq(EXPR, RegexPatternToken.PLUS),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            final TokenPattern pattern = childrenEmission.get(0);
                            return new ChainPattern(pattern, new StarPattern(pattern));
                        }
                    })

            .addRule(seq(EXPR, RegexPatternToken.QUESTION_MARK),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new RepeatPattern(childrenEmission.get(0), 0, 1);
                        }
                    })
            .addRule(seq(EXPR, RegexPatternToken.COUNT),
                        new Emitter<RegexPatternToken, TokenPattern>() {
                            @Override
                            public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                                final Token<RegexPatternToken> lastToken = tokens.get(tokens.size() - 1);
                                final String match = lastToken.str;
                                final String countString = match.substring(1, match.length() - 1);
                                String[] parts = countString.split(",");
                                if (parts.length == 1) {
                                    int val = Integer.valueOf(parts[0]);
                                    return new RepeatPattern(childrenEmission.get(0), val, val);
                                }
                                else {
                                    int minCount = Integer.valueOf(parts[0]);
                                    int maxCount = Integer.valueOf(parts[1]);
                                    return new RepeatPattern(childrenEmission.get(0), minCount, maxCount);
                                }
                            }
                        })
            .addRule(RegexPatternToken.ANNOTATION,
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            final String match = tokens.get(0).str;
                            final String annotationName = match.substring(1, match.length() - 1);
                            final Annotation annotation = Annotation.of(annotationName);
                            return new AnnotationPattern(annotation);
                        }
                    })
            .addRule(seq(EXPR, EXPR),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new ChainPattern(childrenEmission.get(0), childrenEmission.get(1));
                        }
                    });
    }
    final static Grammar<RegexPatternToken, TokenPattern> GRAMMAR = buildGrammar();
    final static LRParser<RegexPatternToken, TokenPattern> PARSER = new LRParser<>(LEXER, GRAMMAR);
    public static TokenPattern compile(final String regex) {
        return PARSER.parse(regex);
    }
    public abstract SimpleState<SemToken> buildMachine(final SimpleState<SemToken> fromState);
}
