package com.fulmicoton.semantic.tokenpattern.nfa;

import java.util.Arrays;

public class Matcher<T> {

    final boolean matches;
    final int nbGroups;
    final int[] groupStarts;
    final int[] groupEnds;

    private Matcher(final boolean matches,
                   final Groups groups,
                   final int nbGroups) {
        this.nbGroups = nbGroups;
        this.matches = matches;
        this.groupStarts = new int[nbGroups];
        this.groupEnds = new int[nbGroups];
        Arrays.fill(this.groupStarts, -1);
        Arrays.fill(this.groupEnds, -1);
        for (Groups groupCur: groups) {
            if (groupCur.op == Groups.OP.OPEN) {
                this.groupStarts[groupCur.groupId] = groupCur.offset;
            }
            else {
                this.groupEnds[groupCur.groupId] = groupCur.offset;
            }
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
        return this.groupStarts[group];
    }

    /*
    @Override
    public int end() {
        return this.groupEnds[group];
    }
    */

    public int end(int group) {
        return this.groupEnds[group];
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
