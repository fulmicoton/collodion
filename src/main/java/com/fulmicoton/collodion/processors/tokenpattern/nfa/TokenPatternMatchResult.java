package com.fulmicoton.collodion.processors.tokenpattern.nfa;

import com.fulmicoton.collodion.processors.tokenpattern.GroupAllocator;

public class TokenPatternMatchResult {

    public final int patternId;
    final boolean matches;
    final GroupAllocator groupAllocator;
    final Groups.GroupSegment[] groupSegments;

    private TokenPatternMatchResult(final int patternId,
                                    final boolean matches,
                                    final Groups groups,
                                    final GroupAllocator groupAllocator) {
        this.patternId = patternId;
        this.groupAllocator = groupAllocator;
        this.matches = matches;
        if (groups != null) {
            this.groupSegments = groups.groupSegments(this.groupAllocator);
        }
        else {
            this.groupSegments = null;
        }
    }

    public boolean matches() {
        return this.matches;
    }

    public static TokenPatternMatchResult doesMatch(int patternId, final Groups groups, final GroupAllocator groupAllocator) {
        return new TokenPatternMatchResult(patternId, true, groups, groupAllocator);
    }

    public static TokenPatternMatchResult doesNotMatch(int patternId, final GroupAllocator groupAllocator) {
        return new TokenPatternMatchResult(patternId, false, null, groupAllocator);
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

}
