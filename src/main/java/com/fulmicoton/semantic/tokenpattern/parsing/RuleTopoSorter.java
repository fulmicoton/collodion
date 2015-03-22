package com.fulmicoton.semantic.tokenpattern.parsing;

import com.fulmicoton.common.IndexBuilder;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class RuleTopoSorter {
    private final Set<Rule> visited = Sets.newHashSet();
    private final IndexBuilder<Rule> orderedDependencies = new IndexBuilder<>();

    private void addRule(final Rule rule) {
        // does not fail on cycle.
        if (!visited.contains(rule)) {
            visited.add(rule);
            for (final Rule dependency : rule.dependencies()) {
                this.addRule(dependency);
            }
            this.orderedDependencies.getId(rule);
        }
    }

    public static IndexBuilder<Rule> sortedDependencies(List<Rule> rules) {
        final RuleTopoSorter topoSorter = new RuleTopoSorter();
        for (Rule rule: rules) {
            topoSorter.addRule(rule);
        }
        return topoSorter.orderedDependencies;
    }

}
