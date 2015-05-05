package com.fulmicoton.collodion.processors.tokenpattern;

import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/**
 * Helps attributing a single "small" group id for each group of each
 * pattern.
 *
 * TODO (low priority) remove reference to multiGroupAllocator by using a builder pattern.
 */
public class GroupAllocator {

    private final MultiGroupAllocator multiGroupAllocator; //< father objects
    public final int offset; //< the groups for this pattern are within [offset, offset+nbGroups[
    public final Map<String, Integer> nameToGroupId = new HashMap<>();
    private int nbGroups = 0;

    GroupAllocator(final int offset,
                   final MultiGroupAllocator multiGroupAllocator) {
        this.offset = offset;
        this.multiGroupAllocator = multiGroupAllocator;
    }

    /**
     * @return the id for a newly encounterred unnamed group.
     */
    public int allocateUnnamedGroup() {
        nbGroups++;
        return this.multiGroupAllocator.allocate();
    }


    /**
     * @return the id for a newly encounterred named group.
     * and saves the assocation between the group name and the group id
     * in a map.
     */
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
