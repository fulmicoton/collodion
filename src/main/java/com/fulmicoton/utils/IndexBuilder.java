package com.fulmicoton.utils;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class IndexBuilder<T> {

    private final Map<T, Integer> indexedElements = Maps.newHashMap();

    public int add(T el) {
        final Integer index = this.indexedElements.get(el);
        if (index != null) return index;
        final int newIndex = this.indexedElements.size();
        this.indexedElements.put(el, newIndex);
        return newIndex;
    }

    public T[] buildIndex(T[] arr) {
        final ArrayList<T> index = new ArrayList<>(Collections.<T>nCopies(60, null));
        for (Map.Entry<T, Integer> e: this.indexedElements.entrySet()) {
            index.set(e.getValue(), e.getKey());
        }
        return index.toArray(arr);
    }

}
