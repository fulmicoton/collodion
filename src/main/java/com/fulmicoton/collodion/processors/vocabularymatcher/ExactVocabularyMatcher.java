package com.fulmicoton.collodion.processors.vocabularymatcher;

import com.fulmicoton.collodion.common.Index;
import com.fulmicoton.collodion.processors.AnnotationKey;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.fst.Util;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ExactVocabularyMatcher extends VocabularyMatcher {

    private final Iterator<AnnotationKey> EMPTY_ITERATOR = ImmutableSet.<AnnotationKey>of().iterator();
    private final FST<Long> fst;
    private final Index<AnnotationKey> annotationMapping;

    ExactVocabularyMatcher(final List<Rule> rules, final CharSequence charSequence) {
        super(charSequence);
        final Index.Builder<AnnotationKey> annotationIndexBuilder = Index.builder();
        final PositiveIntOutputs positiveInts = PositiveIntOutputs.getSingleton();
        final Builder<Long> fstBuilder = new Builder<>(FST.INPUT_TYPE.BYTE1, positiveInts);
        final BytesRefBuilder scratchBytes = new BytesRefBuilder();
        final IntsRefBuilder scratchInts = new IntsRefBuilder();
        try {
            for (final Rule rule: rules) {
                scratchBytes.copyChars(rule.value);
                final int annotationId = annotationIndexBuilder.get(rule.annotation);
                fstBuilder.add(Util.toIntsRef(scratchBytes.get(), scratchInts), (long)annotationId);
            }
            this.annotationMapping = annotationIndexBuilder.build(new AnnotationKey[0]);
            this.fst = fstBuilder.finish();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<AnnotationKey> match() {
        try {
            final Long ruleId = Util.get(fst, new BytesRef(this.charSequence));
            if (ruleId == null) {
                return EMPTY_ITERATOR;
            }
            else {
                return Iterators.singletonIterator(annotationMapping.elFromId((int)(long)ruleId));
            }
        }
        catch(final IOException e) {
            throw new RuntimeException(e);
        }
    }


}
