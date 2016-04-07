package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.tokenpattern.GroupAllocator;
import com.fulmicoton.collodion.processors.tokenpattern.SemToken;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.Predicate;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.State;
import com.fulmicoton.collodion.processors.tokenpattern.parsing.Emitter;
import com.fulmicoton.collodion.processors.tokenpattern.parsing.Grammar;
import com.fulmicoton.collodion.processors.tokenpattern.parsing.LRParser;
import com.fulmicoton.collodion.processors.tokenpattern.parsing.Rule;
import com.fulmicoton.multiregexp.Lexer;
import com.fulmicoton.multiregexp.Token;

import java.util.List;

import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.ANNOTATION;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.CLOSE_PARENTHESIS;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.COUNT;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.DOT;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.OPEN_NAMED_GROUP;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.OPEN_NON_GROUPING;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.OPEN_PARENTHESIS;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.OR;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.PLUS;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.ASSIGN;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.QUESTION_MARK;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.STAR;
import static com.fulmicoton.collodion.processors.tokenpattern.ast.RegexPatternToken.NAME;
import static com.fulmicoton.collodion.processors.tokenpattern.parsing.SequenceRule.seq;

public abstract class AST {

    public static Predicate ALWAYS_TRUE = new Predicate() {

        @Override
        public String toString() {
            return "TRUE";
        }

        @Override
        public boolean apply(final SemToken token) {
            return true;
        }
    };

    public static final Lexer<RegexPatternToken> LEXER = new Lexer<RegexPatternToken>()
        .addRule(ASSIGN, ":=")
        .addRule(OPEN_NON_GROUPING, "\\(\\?\\:")
        .addRule(OPEN_NAMED_GROUP, "\\(\\?\\<[A-Za-z0-9_\\.]+\\>")
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
        .addRule(NAME, "[a-zA-Z]+")
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
            .addRule(seq(NAME, ASSIGN, EXPR),
                    new Emitter<RegexPatternToken, AST>() {
                        @Override
                        public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new CapturingGroupAST(childrenEmission.get(2), AnnotationKey.of(tokens.get(0).str));
                        }
                    })
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
                                return new CapturingGroupAST(childrenEmission.get(1), AnnotationKey.of(groupName));
                            }
                        })
            .addRule(seq(OPEN_PARENTHESIS, EXPR, CLOSE_PARENTHESIS),
                    new Emitter<RegexPatternToken, AST>() {
                        @Override
                        public AST emit(List<AST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return childrenEmission.get(1);
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
                    return new PredicatePatternAST(".", ALWAYS_TRUE);
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
                            public AST emit(final List<AST> childrenEmission,
                                            final List<Token<RegexPatternToken>> tokens) {
                                final Token<RegexPatternToken> lastToken = tokens.get(tokens.size() - 1);
                                final String match = lastToken.str;
                                final String countString = match.substring(1, match.length() - 1);
                                final String[] parts = countString.split(",");
                                if (parts.length == 1) {
                                    final int val = Integer.valueOf(parts[0]);
                                    return new RepeatPatternAST(childrenEmission.get(0), val, val);
                                }
                                else {
                                    final int minCount = Integer.valueOf(parts[0]);
                                    final int maxCount = Integer.valueOf(parts[1]);
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
                            final AnnotationKey annotation = AnnotationKey.of(annotationName);
                            final Predicate predicate = HasAnnotation.of(annotation);
                            return new PredicatePatternAST("[" + annotationName + "]", predicate);
                        }
                    });
    }

    private static final Grammar<RegexPatternToken, AST> GRAMMAR = buildGrammar();
    private static final LRParser<RegexPatternToken, AST> PARSER = new LRParser<>(LEXER, GRAMMAR);

    public static AST compile(final String regex) throws Exception {
        return PARSER.parse(regex);
    }

    public abstract State buildMachine(final int patternId, final State fromState);

    public abstract void allocateGroups(final GroupAllocator groupAllocator);
}
