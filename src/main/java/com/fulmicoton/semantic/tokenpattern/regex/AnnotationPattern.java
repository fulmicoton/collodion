package com.fulmicoton.semantic.tokenpattern.regex;


import com.fulmicoton.semantic.Annotation;

public class AnnotationPattern extends PredicatePattern {

    private final Annotation annotation;

    public AnnotationPattern(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public String toDebugString() {
        return "<" + this.annotation + ">";
    }

    @Override
    public boolean apply(SemToken semToken) {
        return semToken.hasAnnotation(annotation);
    }
}
