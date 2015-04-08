package com.fulmicoton.common;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Index<T> {


    public static class Builder<T> {

        private final Map<T, Integer> elToId = Maps.newHashMap();

        public int get(final T el) {
            final Integer index = this.elToId.get(el);
            if (index != null) return index;
            final int newIndex = this.elToId.size();
            this.elToId.put(el, newIndex);
            return newIndex;
        }

        public Index<T> build(T[] typedArr) {
            final T[] idToEl = this.buildIndex(typedArr);
            return new Index<>(this.elToId, idToEl);
        }

        private T[] buildIndex(T[] arr) {
            final ArrayList<T> index = new ArrayList<>(Collections.<T>nCopies(this.elToId.size(), null));
            for (Map.Entry<T, Integer> e: this.elToId.entrySet()) {
                index.set(e.getValue(), e.getKey());
            }
            return index.toArray(arr);
        }

        public boolean contains(T el) {
            return this.elToId.containsKey(el);
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }


    //--------------------------

    public Index(final Map<T, Integer> elToId,
                 final T[] idToEl) {
        this.elToId = elToId;
        this.idToEl = idToEl;
    }

    private final Map<T, Integer> elToId;
    private final T[] idToEl;

    public int size() {
        return this.idToEl.length;
    }

    public T elFromId(int id) {
        return this.idToEl[id];
    }

    public boolean contains(final T el) {
        return this.elToId.containsKey(el);
    }

    public int get(final T el) {
        final Integer elId = this.elToId.get(el);
        if (elId == null) {
            return -1;
        }
        else {
            return elId;
        }
    }
    /*
    public Map<T, Integer> getMap() {
        return ImmutableMap.copyOf(this.elToId);
    }
    */


}
