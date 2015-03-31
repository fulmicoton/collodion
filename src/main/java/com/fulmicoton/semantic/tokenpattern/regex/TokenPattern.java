package com.fulmicoton.semantic.tokenpattern.regex;

import com.fulmicoton.multiregexp.Lexer;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.nfa.Machine;
import com.fulmicoton.semantic.tokenpattern.nfa.Matcher;
import com.fulmicoton.semantic.tokenpattern.nfa.SimpleState;
import com.fulmicoton.semantic.tokenpattern.parsing.Emitter;
import com.fulmicoton.semantic.tokenpattern.parsing.Grammar;
import com.fulmicoton.semantic.tokenpattern.parsing.LRParser;
import com.fulmicoton.semantic.tokenpattern.parsing.Rule;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.ANNOTATION;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.CLOSE_PARENTHESIS;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.COUNT;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.DOT;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.OR;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.OPEN_PARENTHESIS;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.QUESTION_MARK;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.STAR;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.PLUS;


import java.util.Iterator;
import java.util.List;

import static com.fulmicoton.semantic.tokenpattern.parsing.SequenceRule.seq;

public abstract class TokenPattern {

    public final static Lexer<RegexPatternToken> LEXER = new Lexer<RegexPatternToken>()
        .addRule(OPEN_PARENTHESIS, "\\(")
        .addRule(CLOSE_PARENTHESIS, "\\)")
        .addRule(PLUS, "\\+")
        .addRule(DOT, "\\.")
        .addRule(STAR, "\\*")
        .addRule(OR, "\\|")
        .addRule(QUESTION_MARK, "\\?")
        .addRule(COUNT, "\\{[0-9]+\\}")
        .addRule(COUNT, "\\{[0-9]+,[0-9]+\\}")
        .addRule(ANNOTATION, "\\<[a-zA-Z\\.]+\\>")
    ;

    public abstract String toDebugString();

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.toDebugString() +"]";
    }

    private static Grammar<RegexPatternToken, TokenPattern>  buildGrammar() {
        final Grammar<RegexPatternToken, TokenPattern> grammar = new Grammar<>();
        final Rule<RegexPatternToken> EXPR = grammar.expr;
        return grammar
            .addRule(seq(OPEN_PARENTHESIS, EXPR, CLOSE_PARENTHESIS),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return childrenEmission.get(1);
                        }
                    })
            .addRule(seq(EXPR, EXPR),
                        new Emitter<RegexPatternToken, TokenPattern>() {
                            @Override
                            public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                                return new ChainPattern(childrenEmission.get(0), childrenEmission.get(1));
                            }
                        })
            .addRule(DOT, new Emitter<RegexPatternToken, TokenPattern>() {
                @Override
                public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                    return new DotPattern();
                }
            })
            .addRule(seq(EXPR, STAR),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new StarPattern(childrenEmission.get(0));
                        }
                    })
            .addRule(seq(EXPR, PLUS),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            final TokenPattern pattern = childrenEmission.get(0);
                            return new ChainPattern(pattern, new StarPattern(pattern));
                        }
                    })
            .addRule(seq(EXPR, OR, EXPR),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new OrPattern(childrenEmission.get(0), childrenEmission.get(2));
                        }
                    })
            .addRule(seq(EXPR, QUESTION_MARK),
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new RepeatPattern(childrenEmission.get(0), 0, 1);
                        }
                    })
            .addRule(seq(EXPR, COUNT),
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
            .addRule(ANNOTATION,
                    new Emitter<RegexPatternToken, TokenPattern>() {
                        @Override
                        public TokenPattern emit(List<TokenPattern> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            final String match = tokens.get(0).str;
                            final String annotationName = match.substring(1, match.length() - 1);
                            final Annotation annotation = Annotation.of(annotationName);
                            return new AnnotationPattern(annotation);
                        }
                    });
    }
    final static Grammar<RegexPatternToken, TokenPattern> GRAMMAR = buildGrammar();
    final static LRParser<RegexPatternToken, TokenPattern> PARSER = new LRParser<>(LEXER, GRAMMAR);
    public static TokenPattern compile(final String regex) {
        return PARSER.parse(regex);
    }
    public Matcher<SemToken> match(final Iterator<SemToken> tokens) {
        final SimpleState<SemToken> initialState = new SimpleState<>();
        final SimpleState<SemToken> endState = this.buildMachine(initialState);
        final Machine<SemToken> machine = new Machine<>(initialState, endState);
        return machine.match(tokens);

    }
    public abstract SimpleState<SemToken> buildMachine(final SimpleState<SemToken> fromState);
}
