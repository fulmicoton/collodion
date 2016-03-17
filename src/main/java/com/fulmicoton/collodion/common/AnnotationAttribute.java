package com.fulmicoton.collodion.common;

import com.fulmicoton.collodion.processors.AnnotationKey;
import org.apache.lucene.util.Attribute;

public interface AnnotationAttribute extends Attribute, Iterable<Annotation> {
    public void reset();
    public void add(final AnnotationKey annotation);
    public void add(final AnnotationKey annotation, int numTokens);
    public boolean contains(final AnnotationKey annotation);
}
