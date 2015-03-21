package com.fulmicoton.semantic.vocabularymatcher;

import com.fulmicoton.semantic.Annotation;

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
