package com.fulmicoton.semantic;


import com.sun.deploy.util.StringUtils;
import org.apache.lucene.util.AttributeImpl;

import java.util.ArrayList;
import java.util.List;

public class VocabularyAttributeImpl extends AttributeImpl implements VocabularyAttribute {

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
        target.length = 0;
        for (Annotation annotation: this.annotations) {
            target.add(annotation);
        }
    }

    public void reset() {
        this.length = 0;
    }

    public String toString() {
        final List<String> annotations = new ArrayList<>();
        for (int i=0; i<this.length; i++) {
            final Annotation ann = this.annotations[i];
            annotations.add(ann.toString());
        }
        return StringUtils.join(annotations, ",");

    }

    @Override
    public void add(final Annotation annotation) {
        if (this.length < MAX_NB_ANNOTATIONS) {
            this.annotations[this.length] = annotation;
            this.length += 1;
        }
    }
}
