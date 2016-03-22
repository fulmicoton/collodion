package com.fulmicoton.collodion.processors.sequencematcher;

import com.fulmicoton.collodion.common.Index;
import com.fulmicoton.collodion.processors.AnnotationKey;
import com.google.common.collect.Lists;
import org.apache.lucene.analysis.TokenStream;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

class SequenceVocabularyMatcher {

    private final List<VocabularyMatcher> vocabularyMatchers;
    private final List<AnnotationKey> annotations;
    private final AhoCorasick ahoCorasick;

    SequenceVocabularyMatcher(
            final List<VocabularyMatcher> vocabularyMatchers,
            final AhoCorasick ahoCorasick,
            final List<AnnotationKey> annotations)
    {
        this.vocabularyMatchers = vocabularyMatchers;
        this.ahoCorasick = ahoCorasick;
        this.annotations = annotations;

    }

    static class MatchingMethodAndTerm {
        final MatchingMethod matchingMethod;
        final String term;
        private MatchingMethodAndTerm(final MatchingMethod matchingMethod, final String term) {
            this.matchingMethod = matchingMethod;
            this.term = term;
        }

        @Override
        public boolean equals(final Object o) {
            final MatchingMethodAndTerm that = (MatchingMethodAndTerm)o;
            return ((matchingMethod != that.matchingMethod) && (this.term.equals(that.term)) );

        }

        @Override
        public int hashCode() {
            return this.matchingMethod.hashCode()  + 31 * this.term.hashCode();
        }
    }
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    static class Builder {
        private final Index.Builder<MatchingMethodAndTerm> termIndex = Index.builder();
        private final List<SequenceRule> sequences = Lists.newArrayList();

        public void addRule(final Rule rule) {
            final String[] tokens = WHITESPACE.split(rule.value); //< TODO use a proper tokenizer some day.
            final int[] sequenceTermIds = new int[tokens.length];
            for (int i=0; i<tokens.length; i++) {
                final MatchingMethodAndTerm matchingMethodAndTerm = new MatchingMethodAndTerm(rule.method, rule.value);
                sequenceTermIds[i] = this.termIndex.get(matchingMethodAndTerm);
            }
            this.sequences.add(new SequenceRule(rule.annotation, sequenceTermIds));
        }

        SequenceVocabularyMatcher build(final TokenStream tokenStream) {
            final EnumMap<MatchingMethod, List<TermAndId>> vocabularies = new EnumMap<>(MatchingMethod.class);
            for (final Map.Entry<MatchingMethodAndTerm, Integer> e: this.termIndex.getMap().entrySet()) {
                final List<TermAndId> termAndIds;
                final MatchingMethod matchingMethod = e.getKey().matchingMethod;
                if (!vocabularies.containsKey(matchingMethod)) {
                    termAndIds = Lists.newArrayList();
                    vocabularies.put(matchingMethod, termAndIds);
                }
                else {
                    termAndIds = vocabularies.get(matchingMethod);
                }
                termAndIds.add(new TermAndId(e.getKey().term, e.getValue()));
            }
            final List<VocabularyMatcher> vocabularyMatchers = Lists.newArrayList();
            for (final Map.Entry<MatchingMethod, List<TermAndId>> e: vocabularies.entrySet()) {
                final MatchingMethod method = e.getKey();
                method.createMatcher(tokenStream, e.getValue());
            }

            final List<AnnotationKey> annotations = Lists.newArrayList();
            for (final SequenceRule sequenceRule: this.sequences) {
                annotations.add(sequenceRule.annotationKey);
            }
            final AhoCorasick ahoCorasick = new AhoCorasick();
            int i = 0;
            for (final SequenceRule rule: this.sequences) {
                ahoCorasick.insert(rule.sequence, i);
                i += 1;
            }
            return new SequenceVocabularyMatcher(vocabularyMatchers, ahoCorasick, annotations);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

}
