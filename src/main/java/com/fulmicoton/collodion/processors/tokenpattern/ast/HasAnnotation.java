package com.fulmicoton.collodion.processors.tokenpattern.ast;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.tokenpattern.SemToken;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.Predicate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HasAnnotation implements Predicate {

    private static final Map<AnnotationKey, HasAnnotation> predicateCache = new HashMap<>();

    private final AnnotationKey annotation;

    private HasAnnotation(final AnnotationKey annotation) {
        this.annotation = annotation;
    }

    public static HasAnnotation of(final AnnotationKey annotation) {
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
    public List<Integer> apply(final SemToken semToken) {
        return semToken.hasAnnotation(annotation);
    }

}
