package com.fulmicoton.semantic.tokenpattern;


import com.fulmicoton.semantic.Annotation;

public class AnnotationPattern extends TokenPattern {

    private final Annotation annotation;

    public AnnotationPattern(Annotation annotation) {
        this.annotation = annotation;
    }
}
