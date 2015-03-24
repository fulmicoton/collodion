package com.fulmicoton.semantic.tokenpattern.parsing;


public class Match<T> {
    final Rule<T> rule;
    final int start;
    final int stop;

    public Match(Rule<T> rule, int start, int stop) {
        this.rule = rule;
        this.start = start;
        this.stop = stop;
    }

}
