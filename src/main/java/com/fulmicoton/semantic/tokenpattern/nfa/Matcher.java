package com.fulmicoton.semantic.tokenpattern.nfa;

import com.fulmicoton.semantic.tokenpattern.GroupAllocator;

public class Matcher<T> {

    final boolean matches;
    final GroupAllocator groupAllocator;
    final Groups.GroupSegment[] groupSegments;

    private Matcher(final boolean matches,
                   final Groups groups,
                   final GroupAllocator groupAllocator) {
        this.groupAllocator = groupAllocator;
        this.matches = matches;
        if (groups != null) {
            this.groupSegments = groups.groupSegments(this.groupAllocator.getNbGroups());
        }
        else {
            this.groupSegments = null;
        }
    }


    public boolean matches() {
        return this.matches;
    }

    public static <T> Matcher<T> doesMatch(final Groups groups, final GroupAllocator groupAllocator) {
        return new Matcher<>(true, groups, groupAllocator);
    }

    public static <T> Matcher<T> doesNotMatch(final GroupAllocator groupAllocator) {
        return new Matcher<>(false, null, groupAllocator);
    }

    public int start(int group) {
        if (group < 0 || !this.matches) return -1;
        final Groups.GroupSegment groupSegment = this.groupSegments[group];
        if (groupSegment == null) {
            // Java's spec would be
            // throw new IllegalStateException("Not match available");
            // here, but I feel like this is dumb.
            return -1;
        }
        return groupSegment.start;
    }


    public int end(int group) {
        if (group < 0 || !this.matches) return -1;
        final Groups.GroupSegment groupSegment = this.groupSegments[group];
        if (groupSegment == null) {
            // Java's spec would be
            // throw new IllegalStateException("Not match available");
            // here, but I feel like this is dumb.
            return -1;
        }
        return groupSegment.end;
    }

    public int groupCount() {
        return Math.max(0, this.groupAllocator.getNbGroups() - 1);
    }


    public int start(final String groupName) {
        return this.start(this.groupAllocator.getGroupIdFromName(groupName));
    }

    public int end(final String groupName) {
        return this.end(this.groupAllocator.getGroupIdFromName(groupName));
    }

    /*
    @Override
    public int start() {
        return 0;
    }
    */

    /*
    @Override
    public int end() {
        return this.groupEnds[group];
    }
    */
//
//    @Override
//    public String group() {
//        return null;
//    }
//
//    @Override
//    public String group(int group) {
//        return null;
//    }
//
    /*
        0 is not taken in account hence the -1.
     */
//
}
