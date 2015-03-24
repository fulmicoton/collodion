package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class RuleTopoSorter<T> {
    private final Set<Rule> visited = Sets.newHashSet();
    private final IndexBuilder<Rule> orderedDependencies = new IndexBuilder<>();

    private void addRule(final Rule<T> rule) {
        // does not fail on cycle.
        if (!visited.contains(rule)) {
            visited.add(rule);
            for (final Rule<T> dependency : rule.dependencies()) {
                this.addRule(dependency);
            }
            this.orderedDependencies.getId(rule);
        }
    }

    public static <T> IndexBuilder<Rule<T>> sortedDependencies(List<Rule<T>> rules) {
        final RuleTopoSorter topoSorter = new RuleTopoSorter();
        for (Rule rule: rules) {
            topoSorter.addRule(rule);
        }
        return topoSorter.orderedDependencies;
    }

}
