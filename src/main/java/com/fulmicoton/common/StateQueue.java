package com.fulmicoton.common;

import org.apache.lucene.util.AttributeSource;

public class StateQueue {

    private static class SavedToken {
        AttributeSource.State state;
    }
    private final SavedToken[] savedTokens;
    private final AttributeSource source;
    private int start = 0;
    private int end = 0;
    private int length = 0;

    private StateQueue(final AttributeSource source, SavedToken[] savedTokens) {
        this.source = source;
        this.savedTokens = savedTokens;
    }

    public void push() {
        assert (length < savedTokens.length);
        final SavedToken savedToken = this.savedTokens[this.end];
        savedToken.state = this.source.captureState();
        end = (end + 1) % this.savedTokens.length;
        length += 1;
    }

    public int size() {
        return this.length;
    }

    public boolean isEmpty() {
        return this.length == 0;
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

    public static StateQueue forSourceWithSize(final AttributeSource attrSource, int nbTokens) {
        SavedToken[] savedTokens = new SavedToken[nbTokens];
        for (int i=0; i<nbTokens; i++) {
            savedTokens[i] = new SavedToken();
        }
        return new StateQueue(attrSource, savedTokens);
    }

}
