package com.fulmicoton.semantic.tokenpattern;

import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

public class GroupAllocator {

    final MultiGroupAllocator multiGroupAllocator;
    public final int offset;
    int nbGroups = 0;
    public final Map<String, Integer> nameToGroupId = new HashMap<>();


    GroupAllocator(final int offset, final MultiGroupAllocator multiGroupAllocator) {
        this.offset = offset;
        this.multiGroupAllocator = multiGroupAllocator;
    }

    public int getGlobalGroupId(int localGroupId) {
        return this.offset + localGroupId;
    }

    public int allocateUnnamedGroup() {
        nbGroups++;
        return this.multiGroupAllocator.allocate();
    }

    public int allocateNamedGroup(final String name) {
        final int groupId = this.allocateUnnamedGroup();
        this.nameToGroupId.put(name, groupId - this.offset);
        return groupId;
    }

    public int getNbGroups() {
        return nbGroups;
    }

    public int getGroupIdFromName(final String groupName) {
        final Integer groupId = this.nameToGroupId.get(groupName);
        if (groupId == null) {
            throw new IllegalArgumentException("Group named " + groupName + " is unknown. Available groupNames are " + Joiner.on(", ").join(this.nameToGroupId.keySet()) + ".");
        }
        return groupId;
    }
}
