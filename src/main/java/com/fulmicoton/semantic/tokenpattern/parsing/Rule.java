package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;

import java.util.List;

public interface Rule<T> {

    RuleMatcher<T> matcher(final IndexBuilder<Rule<T>> indexBuilder);
    List<Rule<T>> dependencies();

}
