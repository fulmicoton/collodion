package com.fulmicoton.collodion.processors.tokenpattern.nfa;

public class Thread {

    public final int start;
    public final int maxAccessiblePatternId;
    public final int state;
    public final Groups groups;

    Thread(final int start,
           final int maxAccessiblePatternId,
           final int state,
           final Groups groups) {
        this.start = start;
        this.maxAccessiblePatternId = maxAccessiblePatternId;
        this.state = state;
        this.groups = groups;
    }
}
