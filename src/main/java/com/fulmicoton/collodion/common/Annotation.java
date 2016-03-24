package com.fulmicoton.collodion.common;


import com.fulmicoton.collodion.processors.AnnotationKey;

public class Annotation {
    public AnnotationKey key;
    public int numTokens;

    public Annotation(final AnnotationKey key) {
        this(key, 1);
    }

    public Annotation(final AnnotationKey key, final int numTokens) {
        this.key = key;
        this.numTokens = numTokens;
    }

    public String toString() {
        return this.key.name() + "(" + this.numTokens + ")";
    }

    public Annotation typedClone() {
        return new Annotation(this.key, this.numTokens);
    }
}
