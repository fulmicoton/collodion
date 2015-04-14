package com.fulmicoton.collodion.processors.tokenpattern.nfa;

public class Thread {

    public final int state;
    public final Groups groups;

    Thread(final int state,
           final Groups groups) {
        this.state = state;
        this.groups = groups;
    }
}
