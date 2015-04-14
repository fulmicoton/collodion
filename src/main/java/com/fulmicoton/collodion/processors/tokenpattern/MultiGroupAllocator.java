package com.fulmicoton.collodion.processors.tokenpattern;

import java.util.ArrayList;
import java.util.List;

public class MultiGroupAllocator {

    final List<GroupAllocator> allocators = new ArrayList<>();
    int nbGroups;

    int allocate() {
        return nbGroups++;
    }

    public GroupAllocator newAllocator() {
        final GroupAllocator groupAllocator = new GroupAllocator(this.nbGroups, this);
        this.allocators.add(groupAllocator);
        return groupAllocator;
    }

    public GroupAllocator get(int patternId) {
        return this.allocators.get(patternId);
    }

}
