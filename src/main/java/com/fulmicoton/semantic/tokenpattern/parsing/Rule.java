package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;

import java.util.List;

public interface Rule {

    RuleMatcher matcher(final IndexBuilder<Rule> indexBuilder);
    List<Rule> dependencies();

}
