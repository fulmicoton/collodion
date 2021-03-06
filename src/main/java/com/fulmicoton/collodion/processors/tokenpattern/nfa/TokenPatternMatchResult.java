package com.fulmicoton.collodion.processors.tokenpattern.nfa;

import com.fulmicoton.collodion.processors.AnnotationKey;
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

    public AnnotationKey getAnnotationForGroupId(final int groupId) {
        return this.groupAllocator.annotationKeys.get(groupId);
    }

    public boolean matches() {
        return this.matches;
    }

    public static TokenPatternMatchResult doesMatch(final int patternId, final Groups groups, final GroupAllocator groupAllocator) {
        return new TokenPatternMatchResult(patternId, true, groups, groupAllocator);
    }

    public static TokenPatternMatchResult doesNotMatch(final int patternId, final GroupAllocator groupAllocator) {
        return new TokenPatternMatchResult(patternId, false, null, groupAllocator);
    }

    public int start(final int group) {
        if ((group < 0) || !this.matches) {
            return -1;
        }
        final Groups.GroupSegment groupSegment = this.groupSegments[group];
        if (groupSegment == null) {
            // Java's spec would be
            // throw new IllegalStateException("Not match available");
            // here, but I feel like this is dumb.
            return -1;
        }
        return groupSegment.start;
    }


    public int end(final int group) {
        if ((group < 0) || !this.matches) {
            return -1;
        }
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
        return Math.max(0, this.groupAllocator.getNumGroups() - 1);
    }

    public int start(final String groupName) {
        return this.start(this.groupAllocator.getGroupIdFromName(groupName));
    }

    public int end(final String groupName) {
        return this.end(this.groupAllocator.getGroupIdFromName(groupName));
    }

    public boolean hasStrictlyHigherPriority(final TokenPatternMatchResult other) {
        if (this.start(0) != other.start(0)) {
            return this.start(0) < other.start(0);
        }
        if (this.patternId != other.patternId) {
            return this.patternId < other.patternId;
        }
        return this.end(0) > other.start(0);
    }
}
