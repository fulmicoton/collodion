package com.fulmicoton.collodion.processors;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Pattern;

public class AnnotationKey {

    // private static final Pattern ANNOTATION_PTN = Pattern.compile("[A-Za-z0-9_\\./]+");

    // TODO addPattern namespacing.
    private final String annotation;
    private static final Map<String, AnnotationKey> ANNOTATION_MAP = Maps.newHashMap();
    public static final AnnotationKey NONE = AnnotationKey.of("____NULL___");

    private AnnotationKey(final String annotation) {
        this.annotation = annotation;
    }

    public static boolean isValidName(final String annotationName) {
        //return ANNOTATION_PTN.matcher(annotationName).matches();
        return true;
    }

    public static synchronized AnnotationKey of(final String annotation) {
        if (!isValidName(annotation)) {
            throw new IllegalArgumentException("Annotation name " + annotation + "is not valid");
        }
        final AnnotationKey cachedAnnotation = ANNOTATION_MAP.get(annotation);
        if (cachedAnnotation != null) {
            return cachedAnnotation;
        }
        final AnnotationKey newAnnotation = new AnnotationKey(annotation);
        AnnotationKey.ANNOTATION_MAP.put(annotation, newAnnotation);
        return newAnnotation;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object o) {
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
