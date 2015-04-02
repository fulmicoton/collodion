package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.multiregexp.Lexer;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.fulmicoton.semantic.tokenpattern.nfa.StateImpl;
import com.fulmicoton.semantic.tokenpattern.parsing.Emitter;
import com.fulmicoton.semantic.tokenpattern.parsing.Grammar;
import com.fulmicoton.semantic.tokenpattern.parsing.LRParser;
import com.fulmicoton.semantic.tokenpattern.parsing.Rule;

import java.util.List;

import static com.fulmicoton.semantic.tokenpattern.ast.RegexPatternToken.*;
import static com.fulmicoton.semantic.tokenpattern.parsing.SequenceRule.seq;

public abstract class TokenPatternAST {

    public final static Lexer<RegexPatternToken> LEXER = new Lexer<RegexPatternToken>()
        .addRule(OPEN_NON_GROUPING, "\\(\\?\\:")
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

    private static Grammar<RegexPatternToken, TokenPatternAST>  buildGrammar() {
        final Grammar<RegexPatternToken, TokenPatternAST> grammar = new Grammar<>();
        final Rule<RegexPatternToken> EXPR = grammar.expr;
        return grammar
            .addRule(seq(OPEN_NON_GROUPING, EXPR, CLOSE_PARENTHESIS),
                        new Emitter<RegexPatternToken, TokenPatternAST>() {
                            @Override
                            public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                                return new NonCapturingGroupAST(childrenEmission.get(1));
                            }
                        })
            .addRule(seq(OPEN_PARENTHESIS, EXPR, CLOSE_PARENTHESIS),
                    new Emitter<RegexPatternToken, TokenPatternAST>() {
                        @Override
                        public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new CapturingGroupAST(childrenEmission.get(1));
                        }
                    })
            .addRule(seq(EXPR, EXPR),
                        new Emitter<RegexPatternToken, TokenPatternAST>() {
                            @Override
                            public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                                return new ChainPatternAST(childrenEmission.get(0), childrenEmission.get(1));
                            }
                        })
            .addRule(DOT, new Emitter<RegexPatternToken, TokenPatternAST>() {
                @Override
                public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                    return new DotPatternAST();
                }
            })
            .addRule(seq(EXPR, STAR),
                    new Emitter<RegexPatternToken, TokenPatternAST>() {
                        @Override
                        public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new StarPatternAST(childrenEmission.get(0));
                        }
                    })
            .addRule(seq(EXPR, PLUS),
                    new Emitter<RegexPatternToken, TokenPatternAST>() {
                        @Override
                        public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            final TokenPatternAST pattern = childrenEmission.get(0);
                            return new ChainPatternAST(pattern, new StarPatternAST(pattern));
                        }
                    })
            .addRule(seq(EXPR, OR, EXPR),
                    new Emitter<RegexPatternToken, TokenPatternAST>() {
                        @Override
                        public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new OrPatternAST(childrenEmission.get(0), childrenEmission.get(2));
                        }
                    })
            .addRule(seq(EXPR, QUESTION_MARK),
                    new Emitter<RegexPatternToken, TokenPatternAST>() {
                        @Override
                        public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            return new RepeatPatternAST(childrenEmission.get(0), 0, 1);
                        }
                    })
            .addRule(seq(EXPR, COUNT),
                        new Emitter<RegexPatternToken, TokenPatternAST>() {
                            @Override
                            public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
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
                    new Emitter<RegexPatternToken, TokenPatternAST>() {
                        @Override
                        public TokenPatternAST emit(List<TokenPatternAST> childrenEmission, List<Token<RegexPatternToken>> tokens) {
                            final String match = tokens.get(0).str;
                            final String annotationName = match.substring(1, match.length() - 1);
                            final Annotation annotation = Annotation.of(annotationName);
                            return new AnnotationPatternAST(annotation);
                        }
                    });
    }
    final static Grammar<RegexPatternToken, TokenPatternAST> GRAMMAR = buildGrammar();
    final static LRParser<RegexPatternToken, TokenPatternAST> PARSER = new LRParser<>(LEXER, GRAMMAR);
    public static TokenPatternAST compile(final String regex) {
        return PARSER.parse(regex);
    }
    public abstract StateImpl<SemToken> buildMachine(final StateImpl<SemToken> fromState, final GroupAllocator groupAllocator);
}
