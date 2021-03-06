package com.fulmicoton.collodion.common;


import com.fulmicoton.collodion.processors.AnnotationKey;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.lucene.util.AttributeImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnnotationAttributeImpl extends AttributeImpl implements AnnotationAttribute, Jsonable {

    private static final int MAX_NUM_ANNOTATIONS = 100;
    private final Annotation[] annotations;
    private int length = 0;


    public AnnotationAttributeImpl() {
        this.annotations = new Annotation[MAX_NUM_ANNOTATIONS];
        for (int annotationId = 0; annotationId < MAX_NUM_ANNOTATIONS; annotationId++) {
            this.annotations[annotationId] = new Annotation(AnnotationKey.NONE);
        }
    }

    @Override
    public void clear() {
        this.length = 0;
    }

    @Override
    public void copyTo(final AttributeImpl target_) {
        final AnnotationAttributeImpl target = (AnnotationAttributeImpl)target_;
        target.reset();
        for (final Annotation annotation: this) {
            target.add(annotation.key, annotation.numTokens);
        }
    }

    public void reset() {
        this.length = 0;
    }

    public String toString() {
        final List<String> annotations = new ArrayList<>();
        for (final Annotation ann: this) {
            annotations.add(ann.toString());
        }
        return Joiner.on("; ").join(annotations);

    }

    @Override
    public AnnotationAttributeImpl clone() {
        final AnnotationAttributeImpl vocAttr = new AnnotationAttributeImpl();
        this.copyTo(vocAttr);
        return vocAttr;
    }

    @Override
    public List<Integer> contains(final AnnotationKey annotation) {
        final List<Integer> matchLength = Lists.newArrayList();
        for (final Annotation ann: this) {
            if (ann.key == annotation) {
                matchLength.add(ann.numTokens);
            }
        }
        return matchLength;
    }

    @Override
    public void add(final AnnotationKey annotation) {
        this.add(annotation, 1);
    }

    @Override
    public void add(final AnnotationKey annotationKey, final int numTokens) {
        if (this.length < MAX_NUM_ANNOTATIONS) {
            final Annotation annotation = this.annotations[this.length];
            annotation.key = annotationKey;
            annotation.numTokens = numTokens;
            this.length += 1;
        }
        else {
            throw new IllegalStateException("Exceeded maximum number of annotations");
        }
    }

    @Override
    public void updateJson(final JsonObject jsonObject) {
        final JsonArray jsonArr = new JsonArray();
        for (final Annotation annotation: this) {
            final JsonObject itemJson = new JsonObject();
            itemJson.addProperty("numTokens", annotation.numTokens);
            itemJson.addProperty("annotation", annotation.key.name());
            jsonArr.add(itemJson);
        }
        jsonObject.add("vocabulary", jsonArr);
    }

    @Override
    public Iterator<Annotation> iterator() {
        final int length = this.length;
        final Annotation[] annotations = this.annotations;
        return new Iterator<Annotation>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < length;
            }

            @Override
            public Annotation next() {
                return annotations[i++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
