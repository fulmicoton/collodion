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
    private int length;

    private StateQueue(final AttributeSource source, SavedToken[] savedTokens) {
        this.source = source;
        this.savedTokens = savedTokens;
        this.reset();
    }

    public void push() {
        assert (length < savedTokens.length);
        final SavedToken savedToken = this.savedTokens[this.end];
        savedToken.state = this.source.captureState();
        end = (end + 1) % this.savedTokens.length;
        length += 1;
    }

    public void reset() {
        this.start = 0;
        this.end = 0;
        this.length = 0;
    }

    public int size() {
        return this.length;
    }

    public boolean isEmpty() {
        return this.length <= 0;
    }

    public boolean isFull() {
        return this.length == this.savedTokens.length;
    }

    public void pop() {
        assert length > 0;
        this.source.restoreState(this.savedTokens[this.start].state);
        this.start = (this.start + 1) % this.savedTokens.length;
        this.length -= 1;
    }

    private int innerPos(int pos) {
        return (this.start + pos) % this.savedTokens.length;
    }

    public void peekAhead(int posAhead) {
        assert posAhead < this.length;
        final int innerPos = this.innerPos(posAhead);
        this.source.restoreState(this.savedTokens[innerPos].state);
    }

    public static StateQueue forSourceWithSize(final AttributeSource attrSource, int numTokens) {
        SavedToken[] savedTokens = new SavedToken[numTokens];
        for (int i=0; i<numTokens; i++) {
            savedTokens[i] = new SavedToken();
        }
        return new StateQueue(attrSource, savedTokens);
    }

}
