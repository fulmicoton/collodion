package com.fulmicoton.semantic.tokenpattern.parsing;

import java.util.List;

public class ParsedTree {

    public final Rule rule;
    public final List<ParsedTree> children;

    public ParsedTree(List<ParsedTree> children) {
        this.children = children;
    }
}
