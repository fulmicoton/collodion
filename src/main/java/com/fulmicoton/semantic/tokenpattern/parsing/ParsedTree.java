package com.fulmicoton.semantic.tokenpattern.parsing;

import java.util.List;

public class ParsedTree<T> {

    //public final Rule<T> rule;
    public final List<ParsedTree> children;

    public ParsedTree(List<ParsedTree> children) {
        this.children = children;
    }
}
