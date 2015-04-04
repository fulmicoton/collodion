package com.fulmicoton.semantic.tokenpattern.nfa;


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

    public static enum OP {
        OPEN,
        CLOSE
    }
    final OP op;
    final int groupId;
    final int offset;
    final Groups next;

    public Groups(final OP op,
                  final int groupId,
                  final int offset,
                  final Groups next) {
        this.op = op;
        this.groupId = groupId;
        this.offset = offset;
        this.next = next;
    }

    public static Groups openGroup(Groups groups, int groupId, int offset) {
        return new Groups(OP.OPEN, groupId, offset, groups);
    }


    public static Groups closeGroup(Groups groups, int groupId, int offset) {
        return new Groups(OP.CLOSE, groupId, offset, groups);
    }

    public static class GroupSegment {
        int start = -1;
        int end = -1;

        GroupSegment(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    GroupSegment[] groupSegments(int nbGroups) {
        final GroupSegment[] complete = new GroupSegment[nbGroups];
        final GroupSegment[] incomplete = new GroupSegment[nbGroups];
        for (Groups groups: this.reverseList()) {
            final GroupSegment groupSegment = incomplete[groups.groupId];
            if (groups.op == OP.OPEN) {
                final GroupSegment newGroupSegment = new GroupSegment(groups.offset, -1);
                incomplete[groups.groupId] = newGroupSegment;
            }
            else {
                assert groupSegment.start != -1;
                groupSegment.end = groups.offset;
                complete[groups.groupId] = groupSegment;
            }
        }
        return complete;
    }

    private List<Groups> reverseList() {
        List<Groups> groups = new ArrayList<>();
        for (Groups groupCur=this; groupCur!=null; groupCur=groupCur.next) {
            groups.add(groupCur);
        }
        Collections.reverse(groups);
        return groups;
    }
}
