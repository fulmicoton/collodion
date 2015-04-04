package com.fulmicoton.semantic.tokenpattern.nfa;

public class Matcher<T> {

    final boolean matches;
    final int nbGroups;
    final Groups.GroupSegment[] groupSegments;

    private Matcher(final boolean matches,
                   final Groups groups,
                   final int nbGroups) {
        this.nbGroups = nbGroups;
        this.matches = matches;
        if (groups != null) {
            this.groupSegments = groups.groupSegments(this.nbGroups);
        }
        else {
            this.groupSegments = null;
        }
    }

    public boolean matches() {
        return this.matches;
    }

    public static <T> Matcher<T> doesMatch(final Groups groups, final int nbGroups) {
        return new Matcher<>(true, groups, nbGroups);
    }

    public static <T> Matcher<T> doesNotMatch(final int nbGroups) {
        return new Matcher<>(false, null, nbGroups);
    }


    public int start(int group) {
        if (!this.matches) return -1;
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
        if (!this.matches) return -1;
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
        return Math.max(0, this.nbGroups - 1);
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
