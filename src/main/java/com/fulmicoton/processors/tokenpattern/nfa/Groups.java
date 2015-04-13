package com.fulmicoton.processors.tokenpattern.nfa;


import com.fulmicoton.processors.tokenpattern.GroupAllocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kind of a linked list to handle group operation.
 * In order to avoid copying things here an there,
 * we use an immutable thread.
 *
 * When a thread is forked, the child can use the father
 * group structure.
 *
 * It's only at the very end that we read through
 * the chain of groups and actually compute what the group
 * segments were.
 */
public class Groups  {


    final int[] openGroupId;
    final int[] closeGroupId;
    final int offset;
    final Groups next;

    public Groups(final int[] openGroupId,
                  final int[] closeGroupId,
                  final int offset,
                  final Groups next) {
        this.openGroupId = openGroupId;
        this.closeGroupId = closeGroupId;
        this.offset = offset;
        this.next = next;
    }

    public static class GroupSegment {

        int start = -1;
        int end = -1;

        GroupSegment(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    GroupSegment[] groupSegments(final GroupAllocator groupAllocator) {
        final int nbGroups = groupAllocator.getNbGroups();
        final GroupSegment[] complete = new GroupSegment[nbGroups];
        final GroupSegment[] incomplete = new GroupSegment[nbGroups];
        for (Groups groups: this.reverseList()) {
            for (int groupId: groups.openGroupId) {
                if ((groupAllocator.offset <= groupId) && (groupAllocator.offset + groupAllocator.getNbGroups() > groupId)) {
                    final GroupSegment newGroupSegment = new GroupSegment(groups.offset, -1);
                    incomplete[groupId - groupAllocator.offset] = newGroupSegment;
                }
            }
            for (int groupId: groups.closeGroupId) {
                if ((groupAllocator.offset <= groupId) && (groupAllocator.offset + groupAllocator.getNbGroups() > groupId)) {
                    final GroupSegment groupSegment = incomplete[groupId - groupAllocator.offset];
                    assert groupSegment.start != -1;
                    groupSegment.end = groups.offset;
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
