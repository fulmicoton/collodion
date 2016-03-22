package com.fulmicoton.collodion.processors.sequencematcher;

import com.fulmicoton.collodion.processors.AnnotationKey;

public class Rule {
    public final MatchingMethod method;
    public final String value;
    public final AnnotationKey annotation;

    public Rule(final MatchingMethod method,
                final String value,
                final AnnotationKey annotation) {
        this.method = method;
        this.value = value;
        this.annotation = annotation;
    }
}
