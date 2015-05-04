package com.fulmicoton.collodion.processors;

import com.google.common.collect.Maps;

import java.util.Map;

public class AnnotationKey {

    private final String annotation;
    private final static Map<String, AnnotationKey> annotationMap = Maps.newHashMap();
    public static final AnnotationKey NONE = AnnotationKey.of("");

    private AnnotationKey(final String annotation) {
        this.annotation = annotation;
    }

    public static synchronized AnnotationKey of(final String annotation) {
        final AnnotationKey cachedAnnotation = annotationMap.get(annotation);
        if (cachedAnnotation != null) return cachedAnnotation;
        final AnnotationKey newAnnotation = new AnnotationKey(annotation);
        AnnotationKey.annotationMap.put(annotation, newAnnotation);
        return newAnnotation;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        final AnnotationKey that = (AnnotationKey) o;
        //noinspection StringEquality
        return (this.annotation == that.annotation);
    }

    public String name() {
        return this.annotation;
    }

    @Override
    public int hashCode() {
        return annotation.hashCode();
    }

    public String toString() {
        return this.name();
    }

}