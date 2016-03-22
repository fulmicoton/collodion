package com.fulmicoton.collodion.processors.sequencematcher;

import java.util.Iterator;

class MatchList implements Iterable<Integer> {

    private static final int MAX_MATCHED_TERMS = 1024;

    private int[] matchedTerms = new int[MAX_MATCHED_TERMS];
    private int length;

    MatchList() {
        this.reset();
    }

    public void add(final int i) {
        if (this.length >= this.matchedTerms.length) {
            this.extendCapacity(this.length);
        }
        this.matchedTerms[this.length] = i;
        this.length += 1;
    }

    public void reset() {
        this.length = 0;
    }

    private void extendCapacity(final int newLength) {
        final int[] newMatchedTerms = new int[newLength];
        System.arraycopy(this.matchedTerms, 0, newMatchedTerms, 0, this.matchedTerms.length);
        this.matchedTerms = newMatchedTerms;
    }

    @Override
    public Iterator<Integer> iterator() {
        final MatchList matchList = this;
        return new Iterator<Integer>() {

            int cur = 0;

            @Override
            public boolean hasNext() {
                return cur < matchList.length;
            }

            @Override
            public Integer next() {
                return matchList.matchedTerms[cur++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
