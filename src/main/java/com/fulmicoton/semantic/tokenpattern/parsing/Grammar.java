package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
import com.fulmicoton.multiregexp.Token;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Grammar<T, V> {

    public final OrRule<T> expr;

    public final Map<Rule<T>, Emitter<T, V>> emitterMap = new HashMap<>();

    public Grammar() {
        this.expr = new OrRule<>();
        this.emitterMap.put(expr, new Emitter<T,V>() {

            @Override
            public V emit(List<V> childrenEmission, List<Token<T>> tokens) {
                return childrenEmission.get(0);
            }
        });
    }

    public Grammar<T, V> addRule(final Rule<T> rule, final Emitter<T, V> emitter) {
        this.expr.addRule(rule);
        this.emitterMap.put(rule, emitter);
        return this;
    }

    public IndexBuilder<Rule<T>> getRuleIndex() {
        final IndexBuilder<Rule<T>> indexBuilder = new IndexBuilder<>();
        indexBuilder.get(this.expr);
        final Queue<Rule<T>> toVisit = new LinkedList<>();
        final IndexBuilder<Rule<T>> fakeIndexBuilder = new IndexBuilder<Rule<T>>() {
            @Override
            public int get(final Rule<T> el) {
                if (!indexBuilder.contains(el)) {
                    toVisit.add(el);
                }
                return indexBuilder.get(el);
            }
        };
        toVisit.add(this.expr);
        while (!toVisit.isEmpty()) {
            toVisit.poll().matcher(fakeIndexBuilder);
        }
        return indexBuilder;
    }
}
