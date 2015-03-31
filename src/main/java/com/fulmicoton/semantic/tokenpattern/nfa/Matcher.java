package com.fulmicoton.semantic.tokenpattern.nfa;

import java.util.regex.MatchResult;

public class Matcher<T> /* implements MatchResult*/ {

    final boolean matches;

    public Matcher(boolean matches) {
        this.matches = matches;
    }

    public boolean matches() {
        return this.matches;
    }

//    @Override
//    public int start() {
//        return 0;
//    }
//
//    @Override
//    public int start(int group) {
//        return 0;
//    }
//
//    @Override
//    public int end() {
//        return 0;
//    }
//
//    @Override
//    public int end(int group) {
//        return 0;
//    }
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
//    @Override
//    public int groupCount() {
//        return 0;
//    }
//
}
