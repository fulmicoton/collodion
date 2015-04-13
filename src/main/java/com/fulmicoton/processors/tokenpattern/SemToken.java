package com.fulmicoton.processors.tokenpattern;

import com.fulmicoton.processors.Annotation;
import com.fulmicoton.processors.vocabularymatcher.VocabularyAttribute;
import org.apache.lucene.analysis.TokenStream;

public class SemToken {

    final VocabularyAttribute vocabularyAttribute;
    public SemToken(VocabularyAttribute vocabularyAttrbute) {
        this.vocabularyAttribute = vocabularyAttrbute;
    }

    public SemToken(final TokenStream tokenStream) {
        this(tokenStream.getAttribute(VocabularyAttribute.class));
    }

    public boolean hasAnnotation(Annotation annotation) {
        return this.vocabularyAttribute.contains(annotation);
    }

    public String toString() {
        return "" + this.vocabularyAttribute;
    }
}
