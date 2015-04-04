package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.multiregexp.Lexer;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;
import com.fulmicoton.semantic.tokenpattern.parsing.Emitter;
import com.fulmicoton.semantic.tokenpattern.parsing.Grammar;
import com.fulmicoton.semantic.tokenpattern.parsing.LRParser;
import com.fulmicoton.semantic.tokenpattern.parsing.Rule;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.List;

import static com.fulmicoton.semantic.tokenpattern.ast.RegexPatternToken.*;
import static com.fulmicoton.semantic.tokenpattern.parsing.SequenceRule.seq;

public abstract class AST {

    public final static Lexer<RegexPatternToken> LEXER = new Lexer<RegexPatternToken>()
        .addRule(OPEN_NON_GROUPING, "\\(\\?\\:")
        .addRule(OPEN_NAMED_GROUP, "\\(\\?\\<[a-zA-Z]+\\>")
        .addRule(OPEN_PARENTHESIS, "\\(")
        .addRule(CLOSE_PARENTHESIS, "\\)")
        .addRule(PLUS, "\\+")
        .addRule(DOT, "\\.")
        .addRule(STAR, "\\*")
        .addRule(OR, "\\|")
        .addRule(QUESTION_MARK, "\\?")
        .addRule(COUNT, "\\{[0-9]+\\}")
        .addRule(COUNT, "\\{[0-9]+,[0-9]+\\}")
        .addRule(ANNOTATION, "\\[[a-zA-Z\\.]+\\]")
    ;

    public String toDebugStringWrapped() {
        return this.toDebugString();
    }

    public abstract String toDebugString();

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.toDebugString() +"]";
    }

    private static Grammar<RegexPatternToken, AST>  buildGrammar() {
        final Grammar<RegexPatternToken, AST> grammar = new Grammar<>();
        final Rule<RegexPatternToken> EXPR = grammar.expr;
        return grammar
            .addRule(seq(OPEN_NON_GROUPING, EXPR, CLOSE_PARENTHESIS),
                        new Emitter<RegexPatternToken, AST>() {
                            @Override
                            public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                                return childrenEmission.get(1);
                            }
                        })
            .addRule(seq(OPEN_NAMED_GROUP, EXPR, CLOSE_PARENTHESIS),
                        new Emitter<RegexPatternToken, AST>() {
                            @Override
                            public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                                final String tokenString = tokens.get(0).str;
                                final String groupName = tokenString.substring(3, tokenString.length() - 1);
                                return new CapturingGroupAST(childrenEmission.get(1), groupName);
                            }
                        })
            .addRule(seq(OPEN_PARENTHESIS, EXPR, CLOSE_PARENTHESIS),
                    new Emitter<RegexPatternToken, AST>() {
                        @Override
                        public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new CapturingGroupAST(childrenEmission.get(1), null);
                        }
                    })
            .addRule(seq(EXPR, EXPR),
                        new Emitter<RegexPatternToken, AST>() {
                            @Override
                            public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                                return new ChainPatternAST(childrenEmission.get(0), childrenEmission.get(1));
                            }
                        })
            .addRule(DOT, new Emitter<RegexPatternToken, AST>() {
                @Override
                public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                    return new PredicatePatternAST(".", Predicates.<SemToken>alwaysTrue());
                }
            })
            .addRule(seq(EXPR, STAR),
                    new Emitter<RegexPatternToken, AST>() {
                        @Override
                        public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new StarPatternAST(childrenEmission.get(0));
                        }
                    })
            .addRule(seq(EXPR, PLUS),
                    new Emitter<RegexPatternToken, AST>() {
                        @Override
                        public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            final AST pattern = childrenEmission.get(0);
                            return new ChainPatternAST(pattern, new StarPatternAST(pattern));
                        }
                    })
            .addRule(seq(EXPR, OR, EXPR),
                    new Emitter<RegexPatternToken, AST>() {
                        @Override
                        public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new OrPatternAST(childrenEmission.get(0), childrenEmission.get(2));
                        }
                    })
            .addRule(seq(EXPR, QUESTION_MARK),
                    new Emitter<RegexPatternToken, AST>() {
                        @Override
                        public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new RepeatPatternAST(childrenEmission.get(0), 0, 1);
                        }
                    })
            .addRule(seq(EXPR, COUNT),
                        new Emitter<RegexPatternToken, AST>() {
                            @Override
                            public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                                final Token<RegexPatternToken> lastToken = tokens.get(tokens.size() - 1);
                                final String match = lastToken.str;
                                final String countString = match.substring(1, match.length() - 1);
                                String[] parts = countString.split(",");
                                if (parts.length == 1) {
                                    int val = Integer.valueOf(parts[0]);
                                    return new RepeatPatternAST(childrenEmission.get(0), val, val);
                                }
                                else {
                                    int minCount = Integer.valueOf(parts[0]);
                                    int maxCount = Integer.valueOf(parts[1]);
                                    return new RepeatPatternAST(childrenEmission.get(0), minCount, maxCount);
                                }
                            }
                        })
            .addRule(ANNOTATION,
                    new Emitter<RegexPatternToken, AST>() {
                        @Override
                        public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            final String match = tokens.get(0).str;
                            final String annotationName = match.substring(1, match.length() - 1);
                            final Annotation annotation = Annotation.of(annotationName);
                            final Predicate<SemToken> predicate = new Predicate<SemToken>() {

                                @Override
                                public boolean apply(SemToken semToken) {
                                    return semToken.hasAnnotation(annotation);
                                }
                            };
                            return new PredicatePatternAST("[" + annotationName + "]", predicate);
                        }
                    });
    }

    private final static Grammar<RegexPatternToken, AST> GRAMMAR = buildGrammar();

    private final static LRParser<RegexPatternToken, AST> PARSER = new LRParser<>(LEXER, GRAMMAR);

    public static AST compile(final String regex) {
        return PARSER.parse(regex);
    }

    public abstract StateImpl<SemToken> buildMachine(final StateImpl<SemToken> fromState);

    public abstract void allocateGroups(final GroupAllocator groupAllocator);
}
