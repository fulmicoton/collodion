package com.fulmicoton.processors.tokenpattern.parsing;

import com.fulmicoton.common.Index;

public interface Rule<T> {
    RuleMatcher<T> matcher(final Index<Rule<T>> indexBuilder);
}
