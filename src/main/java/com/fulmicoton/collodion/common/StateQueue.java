package com.fulmicoton.collodion.common;

import org.apache.lucene.util.AttributeSource;

public class StateQueue {

    private static class SavedToken {
        AttributeSource.State state;
    }
    private final SavedToken[] savedTokens;
    private final AttributeSource source;
    private int start;
    private int end;

    private StateQueue(final AttributeSource source,
                       final SavedToken[] savedTokens) {
        this.source = source;
        this.savedTokens = savedTokens;
        this.reset();
    }

    public int start() {
        return this.start;
    }

    public int end() {
        return this.end;
    }

    public void push() {
        assert (length() < savedTokens.length);
        final SavedToken savedToken = this.savedTokens[this.end % this.savedTokens.length];
        savedToken.state = this.source.captureState();
        this.end += 1;
    }

    public int length() {
        return this.end - this.start;
    }

    public void loadState(final int pos) {
        assert (pos >= start) && (pos < end);
        this.source.restoreState(this.savedTokens[pos % this.savedTokens.length].state);
    }

    public void reset() {
        this.start = 0;
        this.end = 0;
    }

    public boolean isEmpty() {
        return this.length() == 0;
    }

    public boolean isFull() {
        return this.length() == this.savedTokens.length;
    }

    public void pop() {
        assert !this.isEmpty();
        this.loadState(this.start);
        this.start += 1;
    }

    public String toString() {
        return "StateQueue(" + this.start + "," + this.end + ")";
    }

    public static StateQueue forSourceWithSize(final AttributeSource attrSource, int numTokens) {
        SavedToken[] savedTokens = new SavedToken[numTokens];
        for (int i=0; i<numTokens; i++) {
            savedTokens[i] = new SavedToken();
        }
        return new StateQueue(attrSource, savedTokens);
    }

}
