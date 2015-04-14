package com.fulmicoton.collodion.processors.tokenpattern.parsing;

import com.fulmicoton.collodion.common.Index;

public interface Rule<T> {
    RuleMatcher<T> matcher(final Index<Rule<T>> indexBuilder);
}
