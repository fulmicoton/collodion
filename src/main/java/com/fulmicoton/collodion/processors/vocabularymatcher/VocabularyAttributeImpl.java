package com.fulmicoton.collodion.processors.vocabularymatcher;


import com.fulmicoton.collodion.common.Jsonable;
import com.fulmicoton.collodion.processors.AnnotationKey;
import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.lucene.util.AttributeImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VocabularyAttributeImpl extends AttributeImpl implements VocabularyAttribute, Jsonable, Iterable<Annotation> {

    private static final int MAX_NB_ANNOTATIONS = 20;
    private final Annotation[] annotations = new Annotation[MAX_NB_ANNOTATIONS];
    private int length = 0;

    @Override
    public void clear() {
        this.length = 0;
    }

    @Override
    public void copyTo(AttributeImpl target_) {
        VocabularyAttributeImpl target = (VocabularyAttributeImpl)target_;
        target.reset();
        for (final Annotation annotation: this) {
            target.add(annotation.key, annotation.nbTokens);
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
    public VocabularyAttributeImpl clone() {
        // call super.clone();
        final VocabularyAttributeImpl vocAttr = new VocabularyAttributeImpl();
        this.copyTo(vocAttr);
        return vocAttr;
    }

    @Override
    public boolean contains(final AnnotationKey annotation) {
        for (final Annotation ann: this) {
            if (ann.key == annotation) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void add(final AnnotationKey annotation) {
        this.add(annotation, 1);
    }

    @Override
    public void add(final AnnotationKey annotation, final int length) {
        if (this.length < MAX_NB_ANNOTATIONS) {
            this.annotations[this.length].key = annotation;
            this.length += 1;
        }
    }

    @Override
    public void updateJson(JsonObject jsonObject) {
        final JsonArray jsonArr = new JsonArray();
        for (final Annotation annotation: this) {
            final JsonObject itemJson = new JsonObject();
            itemJson.addProperty("nbTokens", annotation.nbTokens);
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
        };
    }
}
