package com.fulmicoton.semantic;

import com.google.common.collect.Maps;

import java.util.Map;

public class Annotation {

    private final String annotation;
    private final static Map<String, Annotation> annotationMap = Maps.newHashMap();
    public static final Annotation NONE = Annotation.of("");

    private Annotation(final String annotation) {
        this.annotation = annotation;
    }

    public static synchronized Annotation of(final String annotation) {
        final Annotation cachedAnnotation = annotationMap.get(annotation);
        if (cachedAnnotation != null) return cachedAnnotation;
        final Annotation newAnnotation = new Annotation(annotation);
        Annotation.annotationMap.put(annotation, newAnnotation);
        return newAnnotation;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        final Annotation that = (Annotation) o;
        //noinspection StringEquality
        return (this.annotation == that.annotation);
    }

    @Override
    public int hashCode() {
        return annotation.hashCode();
    }

    public String toString() {
        return this.annotation;
    }

}
