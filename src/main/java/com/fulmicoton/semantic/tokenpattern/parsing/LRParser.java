package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Lexer;
import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.tokenpattern.ParsedTokenPattern;
import com.fulmicoton.semantic.tokenpattern.TokenT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LRParser<T extends Enum, V> {

    private final List<Rule<T>> grammar;
    private final Lexer<T> lexer;
    private final IndexBuilder<Rule<T>> ruleIndex;
    private final Rule<T>[] rules;
    private final RuleMatcher<T>[] ruleMatchers;
    private final Map<Rule<T>, Emitter<V>> emitterMap;

    public LRParser(final Lexer<T> lexer,
                    final List<Rule<T>> grammar)
    {
        this.lexer = lexer;
        this.grammar = grammar;
        this.ruleIndex = RuleTopoSorter.sortedDependencies(this.grammar);
        this.rules = this.ruleIndex.buildIndex(new Rule[0]);
        this.ruleMatchers = new RuleMatcher[this.rules.length];
        this.emitterMap = new HashMap<>();
        for (int ruleId=0; ruleId<this.rules.length; ruleId++) {
            final Rule rule = this.rules[ruleId];
            this.ruleMatchers[ruleId] = rule.matcher(this.ruleIndex);
        }

    }

    private static boolean[][][] makeParseTable(int nbRules, int nbTokens) {
        final boolean[][][] ruleMatchTable = new boolean[nbRules][][];
        for (int ruleId=0; ruleId < nbRules; ruleId++) {
            ruleMatchTable[ruleId] = new boolean[nbTokens-1][];
            for (int start=0; start < nbTokens; start++) {
                ruleMatchTable[ruleId][start] = new boolean[nbTokens - start];
            }
        }
        return ruleMatchTable;
    }

    private ParsedTokenPattern parse(final List<Token<TokenT>> tokens) {
        final boolean[][][] table = makeParseTable(this.rules.length, tokens.size());
        for (int l = 1; l < tokens.size(); l++) {
            for (int start=0; start < tokens.size() - l + 1; start++) {
                for (int ruleId=0; ruleId< this.rules.length; ruleId++) {
                    final RuleMatcher ruleMatcher = this.ruleMatchers[ruleId];
                    table[ruleId][start][l] = ruleMatcher.evaluate(table, start, l, tokens);
                }
            }
        }
        return null;
    }
}
