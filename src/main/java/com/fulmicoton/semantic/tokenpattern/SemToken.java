package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.vocabularymatcher.VocabularyAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

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
