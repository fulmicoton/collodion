package com.fulmicoton.semantic.tokenpattern;

import java.util.HashMap;
import java.util.Map;

public class GroupAllocator {

    int nbGroup = 0;

    public Map<String, Integer> nameToGroupId = new HashMap<>();

    public int allocateUnnamedGroup() {
        return nbGroup++;
    }

    public int allocateNamedGroup(final String name) {
        final int groupId = nbGroup++;
        this.nameToGroupId.put(name, groupId);
        return groupId;
    }

    public int getNbGroups() {
        return nbGroup;
    }

}
