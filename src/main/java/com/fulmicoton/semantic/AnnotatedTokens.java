package com.fulmicoton.semantic;

import java.util.Iterator;
import java.util.List;

public class AnnotatedTokens implements Iterable<AnnotatedToken> {

    final List<AnnotatedToken> annotatedTokens;

    AnnotatedTokens(List<AnnotatedToken> annotatedTokens) {
        this.annotatedTokens = annotatedTokens;
    }

    @Override
    public Iterator<AnnotatedToken> iterator() {
        return this.annotatedTokens.iterator();
    }
}
