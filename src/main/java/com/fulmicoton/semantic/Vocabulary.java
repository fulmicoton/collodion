package com.fulmicoton.semantic;

import com.fulmicoton.JSON;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

public class Vocabulary implements Iterable<Rule> {

    public static Vocabulary fromFile(File file) throws IOException {
        final List<String> lines = Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
        final List<Rule> rules = new ArrayList<>();
        for (String ruleJson: lines) {
            final Rule rule = JSON.GSON.fromJson(ruleJson, Rule.class);
            rules.add(rule);
        }
        return new Vocabulary(rules);
    }

    public final List<Rule> rules;

    public Vocabulary() {
        this.rules = new ArrayList<>();
    }

    public Vocabulary(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public Iterator<Rule> iterator() {
        return this.rules.iterator();
    }


    public EnumMap<MatchingMethod, List<Rule>> grouped() {
        final EnumMap<MatchingMethod, List<Rule>> rulesByMethod = new EnumMap<>(MatchingMethod.class);
        for (Rule rule: this) {
            List<Rule> rules = rulesByMethod.get(rule.method);
            if (rules == null) {
                rules = new ArrayList<>();
                rulesByMethod.put(rule.method, rules);
            }
            rules.add(rule);
        }
        return rulesByMethod;
    }
}
