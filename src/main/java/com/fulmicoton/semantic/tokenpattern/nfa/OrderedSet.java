package com.fulmicoton.semantic.tokenpattern.nfa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OrderedSet<T> implements Iterable<T> {

    public Set<T> itemSet = new HashSet<>();
    public List<T> items = new ArrayList<>();

    public boolean add(final T item) {
        if (this.itemSet.add(item)) {
            this.items.add(item);
            return true;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return this.items.iterator();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public void addAll(Iterable<T> newThreads) {
        for (T item: newThreads) {
            this.add(item);
        }
    }
}
