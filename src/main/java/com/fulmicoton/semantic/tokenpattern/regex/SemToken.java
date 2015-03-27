package com.fulmicoton.semantic.tokenpattern.regex;

import com.fulmicoton.semantic.Annotation;

public class SemToken {

    private final Annotation annotation;

    public SemToken(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }


    public boolean hasAnnotation(Annotation annotation) {
        return this.annotation == annotation;
    }
}
