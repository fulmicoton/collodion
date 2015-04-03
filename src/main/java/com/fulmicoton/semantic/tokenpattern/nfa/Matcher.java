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

    public static <T> Matcher<T> doesNotMatch() {
        return new Matcher<>(false, null, 0);
    }

    /*
    @Override
    public int start() {
        return 0;
    }
    */

    public int start(int group) {
        final Groups.GroupSegment groupSegment = this.groupSegments[group];
        if (groupSegment == null) {
            return -1;
        }
        else {
            return groupSegment.start;
        }
    }

    /*
    @Override
    public int end() {
        return this.groupEnds[group];
    }
    */

    public int end(int group) {
        final Groups.GroupSegment groupSegment = this.groupSegments[group];
        if (groupSegment == null) {
            return -1;
        }
        else {
            return groupSegment.end;
        }
    }
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
    public int groupCount() {
        return this.nbGroups;
    }
//
}
