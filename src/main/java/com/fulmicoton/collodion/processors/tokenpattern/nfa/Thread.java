package com.fulmicoton.collodion.processors.tokenpattern.nfa;

public class Thread {

    public final int start;
    public final int minAccessiblePatternId;
    public final int state;
    public final Groups groups;

    Thread(final int start,
           final int minAccessiblePatternId,
           final int state,
           final Groups groups) {
        this.start = start;
        this.minAccessiblePatternId = minAccessiblePatternId;
        this.state = state;
        this.groups = groups;
    }

    public String toString() {
        return "Thread(" +
                "start=" + this.start + "," +
                "minPtn=" + this.minAccessiblePatternId + "," +
                "state=" + this.state + ")";
    }
}
