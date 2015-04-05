package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.SemToken;
import com.google.common.base.Predicate;

import java.util.HashMap;
import java.util.Map;

public class HasAnnotation implements Predicate<SemToken> {

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

    @Override
    public boolean apply(SemToken semToken) {
        return semToken.hasAnnotation(annotation);
    }

}
