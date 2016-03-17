package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.common.Annotation;
import com.fulmicoton.collodion.processors.AnnotationKey;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helps attributing a single "small" group id for each group of each
 * pattern.
 *
 * TODO (low priority) remove reference to multiGroupAllocator by using a builder pattern.
 */
public class GroupAllocator {

    private final MultiGroupAllocator multiGroupAllocator; //< father objects
    public final int offset; //< the groups for this pattern are within [offset, offset+numGroups[
    public final List<AnnotationKey> annotationKeys;
    public final Map<String, Integer> nameToGroupId = new HashMap<>();
    private int numGroups = 0;

    GroupAllocator(final int offset,
                   final MultiGroupAllocator multiGroupAllocator) {
        this.offset = offset;
        this.multiGroupAllocator = multiGroupAllocator;
        this.annotationKeys = Lists.newArrayList();
    }

    /**
     * @return the id for a newly encountered named group.
     * and saves the association between the group name and the group id
     * in a map.
     */
    public int allocateNamedGroup(final AnnotationKey annotationKey) {
        this.numGroups++;
        this.annotationKeys.add(annotationKey);
        final int groupId = this.multiGroupAllocator.allocate();
        this.nameToGroupId.put(annotationKey.name(), groupId - this.offset);
        return groupId;
    }

    public AnnotationKey getKey(final int groupOrdinal) {
        return this.annotationKeys.get(groupOrdinal);
    }

    public int getNumGroups() {
        return numGroups;
    }

    public int getGroupIdFromName(final String groupName) {
        final Integer groupId = this.nameToGroupId.get(groupName);
        if (groupId == null) {
            throw new IllegalArgumentException("Group named " + groupName + " is unknown. Available groupNames are " + Joiner.on(", ").join(this.nameToGroupId.keySet()) + ".");
        }
        return groupId;
    }
}
