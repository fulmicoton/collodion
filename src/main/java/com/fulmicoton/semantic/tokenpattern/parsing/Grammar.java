package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.multiregexp.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


}
