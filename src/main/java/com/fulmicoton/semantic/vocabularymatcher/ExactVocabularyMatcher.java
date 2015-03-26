package com.fulmicoton.semantic.vocabularymatcher;

import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.common.IndexBuilder;
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

    private final Iterator<Annotation> EMPTY_ITERATOR = ImmutableSet.<Annotation>of().iterator();
    private final FST<Long> fst;
    private final Annotation[] annotationMapping;

    ExactVocabularyMatcher(List<Rule> rules, CharSequence charSequence) {
        super(rules, charSequence);
        final IndexBuilder<Annotation> annotationIndexBuilder = new IndexBuilder<>();
        final PositiveIntOutputs PositiveInts = PositiveIntOutputs.getSingleton();
        final Builder<Long> fstBuilder = new Builder<>(FST.INPUT_TYPE.BYTE1, PositiveInts);
        final BytesRefBuilder scratchBytes = new BytesRefBuilder();
        final IntsRefBuilder scratchInts = new IntsRefBuilder();
        try {
            for (final Rule rule: rules) {
                scratchBytes.copyChars(rule.value);
                final int annotationId = annotationIndexBuilder.get(rule.annotation);
                fstBuilder.add(Util.toIntsRef(scratchBytes.get(), scratchInts), (long)annotationId);
            }
            this.annotationMapping = annotationIndexBuilder.buildIndex(new Annotation[0]);
            this.fst = fstBuilder.finish();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<Annotation> match() {
        try {
            Long ruleId = Util.get(fst, new BytesRef(this.charSequence));
            if (ruleId == null) {
                return EMPTY_ITERATOR;
            }
            else {
                return Iterators.singletonIterator(annotationMapping[(int) (long) ruleId]);
            }
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }


}
