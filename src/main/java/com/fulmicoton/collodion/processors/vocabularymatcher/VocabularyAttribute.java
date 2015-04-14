package com.fulmicoton.collodion.processors.vocabularymatcher;

import com.fulmicoton.collodion.processors.Annotation;
import org.apache.lucene.util.Attribute;

public interface VocabularyAttribute extends Attribute {
    public void reset();
    public void add(final Annotation annotation);
    public boolean contains(final Annotation annotation);
}
