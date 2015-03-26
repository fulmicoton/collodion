package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;

public interface Rule<T> {
    RuleMatcher<T> matcher(final IndexBuilder<Rule<T>> indexBuilder);
}
