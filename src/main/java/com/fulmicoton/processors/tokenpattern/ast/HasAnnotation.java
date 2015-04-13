package com.fulmicoton.processors.tokenpattern.ast;

import com.fulmicoton.processors.Annotation;
import com.fulmicoton.processors.tokenpattern.SemToken;
import com.fulmicoton.processors.tokenpattern.nfa.Predicate;

import java.util.HashMap;
import java.util.Map;

public class HasAnnotation implements Predicate {

    private static final Map<Annotation, HasAnnotation> predicateCache = new HashMap<>();

    private final Annotation annotation;

    private HasAnnotation(final Annotation annotation) {
        this.annotation = annotation;
    }

    public static HasAnnotation of(final Annotation annotation) {
        HasAnnotation predicate = predicateCache.get(annotation);
        if (predicate == null) {
            predicate = new HasAnnotation(annotation);
            predicateCache.put(annotation, predicate);
        }
        return predicate;
    }

    public String toString() {
        return "Has(" + this.annotation.toString() + ")";
    }

    @Override
    public boolean apply(SemToken semToken) {
        return semToken.hasAnnotation(annotation);
    }

}
