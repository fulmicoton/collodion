package com.fulmicoton.collodion.processors.vocabularymatcher;

import com.fulmicoton.collodion.processors.Annotation;

public class Rule {
    public final MatchingMethod method;
    public final String value;
    public final Annotation annotation;

    public Rule(final MatchingMethod method,
                final String value,
                final Annotation annotation) {
        this.method = method;
        this.value = value;
        this.annotation = annotation;
    }
}
