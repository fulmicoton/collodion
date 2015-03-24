package com.fulmicoton.semantic.tokenpattern.parsing;

import java.util.HashMap;
import java.util.Map;

public class Grammar<T, V> {

    public final OrRule<T> expr = new OrRule<>();

    public final Map<Rule<T>, Emitter<T, V>> emitterMap = new HashMap<>();

    public Grammar<T, V> addRule(final Rule<T> rule, final Emitter<T, V> emitter) {
        this.expr.addRule(rule);
        this.emitterMap.put(rule, emitter);
        return this;
    }


}
