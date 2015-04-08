package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.Index;

public interface Rule<T> {
    RuleMatcher<T> matcher(final Index<Rule<T>> indexBuilder);
}
