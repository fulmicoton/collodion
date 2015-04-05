package com.fulmicoton.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class IndexBuilder<T> {

    protected final Map<T, Integer> indexedElements = Maps.newHashMap();

    private boolean immutable;

    public int size() {
        return this.indexedElements.size();
    }

    public int get(final T el) {
        final Integer index = this.indexedElements.get(el);
        if (index != null) return index;
        if (immutable) throw new IllegalArgumentException();
        final int newIndex = this.indexedElements.size();
        this.indexedElements.put(el, newIndex);
        return newIndex;
    }

    public boolean contains(final T el) {
        return this.indexedElements.containsKey(el);
    }

    public Map<T, Integer> getMap() {
        return ImmutableMap.copyOf(this.indexedElements);
    }

    public T[] buildIndex(T[] arr) {
        final ArrayList<T> index = new ArrayList<>(Collections.<T>nCopies(this.indexedElements.size(), null));
        for (Map.Entry<T, Integer> e: this.indexedElements.entrySet()) {
            index.set(e.getValue(), e.getKey());
        }
        return index.toArray(arr);
    }

    public void setImmutable() {
        this.immutable = true;
    }
}
