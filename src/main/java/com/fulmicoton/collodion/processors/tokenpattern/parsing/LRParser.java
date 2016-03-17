package com.fulmicoton.collodion.processors.tokenpattern.parsing;

import com.fulmicoton.collodion.common.Index;
import com.fulmicoton.multiregexp.Lexer;
import com.fulmicoton.multiregexp.Token;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class LRParser<T extends Enum, V> {


    private final Lexer<T> lexer;
    private final Grammar<T, V> grammar;

    private final Index<Rule<T>> ruleIndex;
    private final RuleMatcher<T>[] ruleMatchers;

        public LRParser(final Lexer<T> lexer,
                    final Grammar<T, V> grammar)
    {
        this.lexer = lexer;
        this.grammar = grammar;
        this.ruleIndex = grammar.getRuleIndex();
        this.ruleMatchers = (RuleMatcher<T>[])new RuleMatcher[this.ruleIndex.size()];
        for (int ruleId=0; ruleId<this.ruleIndex.size(); ruleId++) {
            final Rule<T> rule = this.ruleIndex.elFromId(ruleId);
            this.ruleMatchers[ruleId] = rule.matcher(this.ruleIndex);
        }

    }

    private RuleMatcher<T> matcherFromRule(final Rule<T> rule) {
        final int ruleId = this.ruleIndex.get(rule);
        return this.ruleMatchers[ruleId];
    }

    private static boolean[][][] makeParseTable(final int numRules, final int numTokens) {
        final boolean[][][] ruleMatchTable = new boolean[numRules][][];
        for (int ruleId=0; ruleId < numRules; ruleId++) {
            ruleMatchTable[ruleId] = new boolean[numTokens][];
            for (int start=0; start < numTokens; start++) {
                ruleMatchTable[ruleId][start] = new boolean[numTokens - start + 1];
            }
        }
        return ruleMatchTable;
    }

    public V parse(final String s) {
        final List<Token<T>> tokens = Lists.newArrayList(this.lexer.scan(s));
        return this.parse(tokens);
    }

    private V parse(final Match<T> match,
                    final boolean[][][] table,
                    final List<Token<T>> tokens) {
        final RuleMatcher<T> ruleMatcher = this.matcherFromRule(match.rule);
        final List<Match<T>> matches = ruleMatcher.getMatches(table, match.start, match.length);
        final List<V> childrenEmissions = new ArrayList<>();
        for (final Match m: matches) {
            final V childEmission = (V)this.parse(m, table, tokens);
            childrenEmissions.add(childEmission);
        }
        final Emitter<T, V> emitter = this.grammar.emitterMap.get(match.rule);
        if (emitter == null) {
            return null;
        }
        else {
            return emitter.emit(childrenEmissions, tokens.subList(match.start, match.start + match.length));
        }
    }

    private V parse(final List<Token<T>> tokens) {
        final int nbRules =  this.ruleIndex.size();
        final boolean[][][] table = makeParseTable(nbRules, tokens.size());
        for (int l = 1; l <= tokens.size(); l++) {
            for (int start = 0; start < tokens.size() - l + 1; start++) {
                for (int ruleId = nbRules - 1; ruleId >= 0; ruleId--) {
                    final RuleMatcher<T> ruleMatcher = this.ruleMatchers[ruleId];
                    table[ruleId][start][l] = ruleMatcher.evaluate(table, start, l, tokens);
                }
            }
        }
        final int grammarRuleId = this.ruleIndex.get(this.grammar.expr);
        if (!table[grammarRuleId][0][tokens.size()]) {
            throw new IllegalArgumentException("Invalid format");
        }
        final Match<T> match = new Match<>(this.grammar.expr, 0, tokens.size());
        return this.parse(match, table, tokens);
    }
}
