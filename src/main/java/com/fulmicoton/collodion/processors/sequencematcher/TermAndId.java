package com.fulmicoton.collodion.processors.sequencematcher;

import com.google.common.primitives.Ints;

class TermAndId implements Comparable<TermAndId> {
    public final String term;
    public final int id;

    TermAndId(final String term, final int id) {
        this.term = term;
        this.id = id;
    }

    @Override
    public int compareTo(final TermAndId other) {
        final int cmpTerm = this.term.compareTo(other.term);
        if (cmpTerm != 0) {
            return cmpTerm;
        }
        else {
            return Ints.compare(this.id, other.id);
        }
    }
}
