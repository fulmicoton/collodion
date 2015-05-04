package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.common.AnnotationAttribute;
import org.apache.lucene.analysis.TokenStream;

public class SemToken {

    final AnnotationAttribute annotationAttribute;
    public SemToken(AnnotationAttribute vocabularyAttrbute) {
        this.annotationAttribute = vocabularyAttrbute;
    }

    public SemToken(final TokenStream tokenStream) {
        this(tokenStream.getAttribute(AnnotationAttribute.class));
    }

    public boolean hasAnnotation(AnnotationKey annotation) {
        return this.annotationAttribute.contains(annotation);
    }

    public String toString() {
        return "" + this.annotationAttribute;
    }
}
