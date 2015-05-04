package com.fulmicoton.collodion.common;


import com.fulmicoton.collodion.processors.AnnotationKey;

public class Annotation {
    public AnnotationKey key;
    public int nbTokens;

    public Annotation(final AnnotationKey key) {
        this(key, 1);
    }

    public Annotation(final AnnotationKey key, final int nbTokens) {
        this.key = key;
        this.nbTokens = nbTokens;
    }

    public String toString() {
        return this.key.name();
    }
}
