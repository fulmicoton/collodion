package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.vocabularymatcher.VocabularyAttribute;
import org.apache.lucene.analysis.TokenStream;

public class SemToken {

    final VocabularyAttribute vocabularyAttribute;
    public SemToken(VocabularyAttribute vocabularyAttrbute) {
        this.vocabularyAttribute = vocabularyAttrbute;
    }

    public SemToken(final TokenStream tokenStream) {
        this(tokenStream.getAttribute(VocabularyAttribute.class));
    }

    public boolean hasAnnotation(AnnotationKey annotation) {
        return this.vocabularyAttribute.contains(annotation);
    }

    public String toString() {
        return "" + this.vocabularyAttribute;
    }
}
