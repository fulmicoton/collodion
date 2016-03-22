package com.fulmicoton.collodion.processors.sequencematcher;

class MatchList {

    private static final int MAX_MATCHED_TERMS = 1024;

    int[] matchedTerms = new int[MAX_MATCHED_TERMS];
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
}
