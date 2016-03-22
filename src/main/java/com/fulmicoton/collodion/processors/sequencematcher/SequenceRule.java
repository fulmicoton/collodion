package com.fulmicoton.collodion.processors.sequencematcher;

import com.fulmicoton.collodion.processors.AnnotationKey;

class SequenceRule {
    final AnnotationKey annotationKey;
    final int[] sequence;

    SequenceRule(final AnnotationKey annotationKey, final int[] sequence) {
        this.annotationKey = annotationKey;
        this.sequence = sequence;
    }
}
