package com.fulmicoton.semantic.tokenpattern.ast;


import com.fulmicoton.semantic.Annotation;

public class AnnotationPatternAST extends PredicatePatternAST {

    private final Annotation annotation;

    public AnnotationPatternAST(Annotation annotation) {
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
