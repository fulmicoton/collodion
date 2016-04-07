package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.common.AnnotationAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.util.List;

public class SemToken {

    final AnnotationAttribute annotationAttribute;

    public SemToken(final AnnotationAttribute annotationAttribute) {
        this.annotationAttribute = annotationAttribute;
    }

    public SemToken(final TokenStream tokenStream) {
        this(tokenStream.getAttribute(AnnotationAttribute.class));
    }

    public List<Integer> hasAnnotation(final AnnotationKey annotation) {
        return this.annotationAttribute.contains(annotation);
    }

    public String toString() {
        return String.valueOf(this.annotationAttribute);
    }
}
