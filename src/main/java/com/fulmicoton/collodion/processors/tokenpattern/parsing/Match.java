package com.fulmicoton.collodion.processors.tokenpattern.parsing;


public class Match<T> {
    final Rule<T> rule;
    final int start;
    final int length;

    public Match(Rule<T> rule, int start, int length) {
        assert length > 0;
        this.rule = rule;
        this.start = start;
        this.length = length;
    }

}
