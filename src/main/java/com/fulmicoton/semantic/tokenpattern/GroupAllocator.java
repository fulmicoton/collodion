package com.fulmicoton.semantic.tokenpattern;

import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

public class GroupAllocator {

    int nbGroup = 0;

    public final Map<String, Integer> nameToGroupId = new HashMap<>();

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

    public int getGroupIdFromName(final String groupName) {
        final Integer groupId = this.nameToGroupId.get(groupName);
        if (groupId == null) {
            throw new IllegalArgumentException("Group named " + groupName + " is unknown. Available groupNames are " + Joiner.on(", ").join(this.nameToGroupId.keySet()) + ".");
        }
        return groupId;
    }
}
