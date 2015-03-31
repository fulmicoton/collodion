package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.Annotation;

public class SemToken {

    private final Annotation annotation;

    public SemToken(Annotation annotation) {
        this.annotation = annotation;
    }

    public boolean hasAnnotation(Annotation annotation) {
        return this.annotation == annotation;
    }
}
