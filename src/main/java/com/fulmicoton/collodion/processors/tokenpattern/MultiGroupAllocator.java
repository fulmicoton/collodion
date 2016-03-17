package com.fulmicoton.collodion.processors.tokenpattern;

import java.util.ArrayList;
import java.util.List;

public class MultiGroupAllocator {
    /**
     * Given a list of pattern, each group is
     * attributed an id.
     *
     * The multi group allocator is in charge of delivering
     * as many contiguous group ids.
     * Each pattern will "own" a segment of id within
     * this space.
     *
     * The different pattern on the other hand, will
     * call their own GroupAllocator.
     */
    private final List<GroupAllocator> allocators = new ArrayList<>();
    private int numGroups; //< overall number of groups.

    int allocate() {
        return numGroups++;
    }

    /**
     * @return A new GroupAllocator for a new pattern.
     */
    public GroupAllocator newAllocator() {
        final GroupAllocator groupAllocator = new GroupAllocator(this.numGroups, this);
        this.allocators.add(groupAllocator);
        return groupAllocator;
    }

    /**
     * An allocator that has been created for pattern patternId.
     * i.e. the allocator that has been returned after the patternIdth call to
     * newAllocator()
     */
    public GroupAllocator get(final int patternId) {
        return this.allocators.get(patternId);
    }
}
