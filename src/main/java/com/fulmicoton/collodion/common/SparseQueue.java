package com.fulmicoton.collodion.common;

import com.google.common.collect.Lists;

import java.util.List;

public class SparseQueue<T> {

    private final List<Element<T>> points = Lists.newArrayList();
    private final T defaultElement;
    private int nextOffsetId;
    private int readCur;
    private int writeCur;
    private final int totalSize;

    public static class Element<T> {
        public final int position;
        public final T val;

        private Element(final int position, final T val) {
            this.position = position;
            this.val = val;
        }
    }

    public SparseQueue(final int totalSize,
                       final T defaultElement) {
        this.defaultElement = defaultElement;
        this.nextOffsetId = 0;
        this.readCur = 0;
        this.writeCur = -1;
        this.totalSize = totalSize;
    }

    public void add(final int position, final T el) {
        assert position > this.writeCur;
        assert position < this.totalSize;
        this.writeCur = position;
        this.points.add(new Element<>(position, el));
    }

    public int size() {
        return this.totalSize - this.readCur;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public Element<T> lastWrittenPosition() {
        if (this.points.isEmpty()) {
            return null;
        }
        else {
            return this.points.get(this.points.size() - 1);
        }
    }

    public T poll() {
        final int readCur = this.readCur;
        this.readCur += 1;
        if (this.nextOffsetId < this.points.size()) {
            final Element<T> nextElement = this.points.get(this.nextOffsetId);
            if (nextElement.position == readCur) {
                this.nextOffsetId += 1;
                return nextElement.val;
            }
        }
        return this.defaultElement;
    }
}
