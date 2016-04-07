package com.fulmicoton.collodion.processors.tokenpattern.nfa;


import com.fulmicoton.collodion.processors.tokenpattern.GroupAllocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kind of a linked list to handle group operation.
 * In order to avoid copying things here an there,
 * we keep this immutable datastruct.
 *
 * It's only at the very end that we read through
 * the chain of groups and actually compute what the group
 * segments were.
 */
public class Groups  {

    final int[] openGroupId;
    final int[] closeGroupId;
    final int offset;
    final int matchLength;
    final Groups next;

    public Groups(final int[] openGroupId,
                  final int[] closeGroupId,
                  final int offset,
                  final int matchLength,
                  final Groups next) {
        this.openGroupId = openGroupId;
        this.closeGroupId = closeGroupId;
        this.offset = offset;
        this.matchLength = matchLength;
        this.next = next;
    }

    public static class GroupSegment {

        int start = -1;
        int end = -1;

        GroupSegment(final int start, final int end) {
            this.start = start;
            this.end = end;
        }
    }

    GroupSegment[] groupSegments(final GroupAllocator groupAllocator) {
        final int numGroups = groupAllocator.getNumGroups();
        final GroupSegment[] complete = new GroupSegment[numGroups];
        final GroupSegment[] incomplete = new GroupSegment[numGroups];
        for (final Groups groups: this.reverseList()) {
            for (final int groupId: groups.openGroupId) {
                if ((groupAllocator.offset <= groupId) && (groupAllocator.offset + groupAllocator.getNumGroups() > groupId)) {
                    final GroupSegment newGroupSegment = new GroupSegment(groups.offset, -1);
                    incomplete[groupId - groupAllocator.offset] = newGroupSegment;
                }
            }
            for (final int groupId: groups.closeGroupId) {
                if ((groupAllocator.offset <= groupId) && (groupAllocator.offset + groupAllocator.getNumGroups() > groupId)) {
                    final GroupSegment groupSegment = incomplete[groupId - groupAllocator.offset];
                    assert groupSegment.start != -1;
                    groupSegment.end = groups.offset + groups.matchLength - 1;
                    complete[groupId - groupAllocator.offset] = groupSegment;
                }
            }
        }
        return complete;
    }

    private List<Groups> reverseList() {
        final List<Groups> groups = new ArrayList<>();
        for (Groups groupCur=this; groupCur!=null; groupCur=groupCur.next) {
            groups.add(groupCur);
        }
        Collections.reverse(groups);
        return groups;
    }
}
