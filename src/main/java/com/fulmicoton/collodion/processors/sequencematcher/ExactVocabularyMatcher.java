package com.fulmicoton.collodion.processors.sequencematcher;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ExactVocabularyMatcher extends VocabularyMatcher {

    private static final Iterator<AnnotationKey> EMPTY_ITERATOR = ImmutableSet.<AnnotationKey>of().iterator();
    private final FST<Long> fst;


    ExactVocabularyMatcher(final List<TermAndId> termAndIds, final CharSequence charSequence) {
        super(charSequence);
        Collections.sort(termAndIds);
        final PositiveIntOutputs positiveInts = PositiveIntOutputs.getSingleton();
        final Builder<Long> fstBuilder = new Builder<>(FST.INPUT_TYPE.BYTE1, positiveInts);
        final BytesRefBuilder scratchBytes = new BytesRefBuilder();
        final IntsRefBuilder scratchInts = new IntsRefBuilder();
        try {
            for (final TermAndId termAndId: termAndIds) {
                scratchBytes.copyChars(termAndId.term);
                fstBuilder.add(Util.toIntsRef(scratchBytes.get(), scratchInts), (long)termAndId.id);
            }
            this.fst = fstBuilder.finish();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void match(final MatchList matchList) {
        try {
            final Long ruleId = Util.get(fst, new BytesRef(this.charSequence));
            if (ruleId != null) {
                matchList.add(ruleId.intValue());
            }
        }
        catch(final IOException e) {
            throw new RuntimeException(e);
        }
    }


}
