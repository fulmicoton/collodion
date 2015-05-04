package com.fulmicoton.collodion.processors.vocabularymatcher;

import com.fulmicoton.collodion.processors.AnnotationKey;
import org.apache.lucene.util.Attribute;

public interface VocabularyAttribute extends Attribute {
    public void reset();
    public void add(final AnnotationKey annotation);
    public void add(final AnnotationKey annotation, int nbTokens);
    public boolean contains(final AnnotationKey annotation);
}
