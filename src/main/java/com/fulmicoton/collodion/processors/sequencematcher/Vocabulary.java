package com.fulmicoton.collodion.processors.sequencematcher;

import com.fulmicoton.collodion.common.JSON;
import com.fulmicoton.collodion.common.loader.Loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

public class Vocabulary implements Iterable<Rule> {

    public static Vocabulary fromStream(final InputStream inputStream) throws IOException {
        final InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final List<Rule> rules = new ArrayList<>();
        String ruleJson = bufferedReader.readLine();
        while (ruleJson != null) {
            final Rule rule = JSON.fromJson(ruleJson, Rule.class);
            rules.add(rule);
            ruleJson = bufferedReader.readLine();
        }
        return new Vocabulary(rules);
    }


    public static Vocabulary load(String path) throws IOException {
        return fromStream(Loader.DEFAULT_LOADER.open(path));
    }

    public final List<Rule> rules;

    public Vocabulary() {
        this.rules = new ArrayList<>();
    }

    public Vocabulary(final List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public Iterator<Rule> iterator() {
        return this.rules.iterator();
    }

//    public SequenceVocabularyMatcher toSequenceVocabulary() {
//        final SequenceVocabularyMatcher.Builder sequenceVocabularyBuilder = SequenceVocabularyMatcher.builder();
//        for (final Rule rule: this.rules) {
//            sequenceVocabularyBuilder.addRule(rule);
//        }
//        return sequenceVocabularyBuilder.build();
//    }

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
