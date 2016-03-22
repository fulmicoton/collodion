package com.fulmicoton.collodion.processors.sequencematcher;

import com.fulmicoton.collodion.common.Annotation;
import com.fulmicoton.collodion.common.AnnotationAttribute;
import com.fulmicoton.collodion.common.Index;
import com.fulmicoton.collodion.common.JSON;
import com.fulmicoton.collodion.common.StateQueue;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SequenceVocabularyFilter extends TokenFilter {

    public static class Builder implements ProcessorBuilder<SequenceVocabularyFilter>  {


        // TODO can be optimized.
        private String path;
        private transient Index.Builder<MatchingMethodAndTerm> termIndex;
        private transient List<SequenceRule> sequences;




        @Override
        public void init(final Loader loader) throws IOException {
            final InputStream inputStream = loader.open(path);
            if (inputStream == null) {
                throw new FileNotFoundException("Could not find vocabulary file at path : " + this.path);
            }
            this.termIndex = Index.builder();
            this.sequences = Lists.newArrayList();
            loadRules(inputStream);
        }

        private void loadRules(final InputStream inputStream) throws IOException {
            final InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            try (final BufferedReader bufferedReader = new BufferedReader(reader)) {
                String ruleJson = bufferedReader.readLine();
                while (ruleJson != null) {
                    final Rule rule = JSON.fromJson(ruleJson, Rule.class);
                    this.addRule(rule);
                    ruleJson = bufferedReader.readLine();
                }

            }
        }

        @Override
        public SequenceVocabularyFilter createFilter(final TokenStream prev) throws IOException {
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
                final VocabularyMatcher vocabularyMatcher = method.createMatcher(prev, e.getValue());
                vocabularyMatchers.add(vocabularyMatcher);
            }

            final List<Annotation> annotations = Lists.newArrayList();
            for (final SequenceRule sequenceRule: this.sequences) {
                annotations.add(new Annotation(sequenceRule.annotationKey, sequenceRule.sequence.length));
            }
            int maxLength = 1;
            final AhoCorasick ahoCorasick = new AhoCorasick();
            int i = 0;
            for (final SequenceRule rule: this.sequences) {
                ahoCorasick.insert(rule.sequence, i);
                maxLength = Ints.max(rule.sequence.length, maxLength);
                i += 1;
            }
            return new SequenceVocabularyFilter(prev, vocabularyMatchers, ahoCorasick, annotations, maxLength);
        }

        private void addRule(final Rule rule) {
            final String[] tokens = WHITESPACE.split(rule.value); //< TODO use a proper tokenizer some day.
            final int[] sequenceTermIds = new int[tokens.length];
            for (int i=0; i<tokens.length; i++) {
                final MatchingMethodAndTerm matchingMethodAndTerm = new MatchingMethodAndTerm(rule.method, tokens[i]);
                sequenceTermIds[i] = this.termIndex.get(matchingMethodAndTerm);
            }
            this.sequences.add(new SequenceRule(rule.annotation, sequenceTermIds));
        }


    }

    private final List<VocabularyMatcher> vocabularyMatchers;
    private final List<Annotation> annotations;
    private final AhoCorasick ahoCorasick;
    private final StateQueue stateQueue;
    private boolean tokenRemaining;

    private final AnnotationAttribute annotationAttribute;

    private Map<Integer, List<Annotation>> annotationsMap;
    private Set<AhoCorasick.Node> curNodes;
    private int consumedToken = 0;
    private int emittedToken = 0;


    @Override
    public void reset() throws IOException {
        this.input.reset();
        this.emittedToken = 0;
        this.consumedToken = 0;
        this.tokenRemaining = true;
        this.annotationsMap.clear();
        this.curNodes = ImmutableSet.of(ahoCorasick.getRoot());
    }


    SequenceVocabularyFilter(
            final TokenStream tokenStream,
            final List<VocabularyMatcher> vocabularyMatchers,
            final AhoCorasick ahoCorasick,
            final List<Annotation> annotations,
            final int maxLength)
    {
        super(tokenStream);
        this.annotations = annotations;
        this.vocabularyMatchers = vocabularyMatchers;
        this.ahoCorasick = ahoCorasick;
        this.annotationsMap = Maps.newHashMap();
        this.curNodes = Sets.newHashSet(ahoCorasick.getRoot());
        this.tokenRemaining = true;
        this.stateQueue = StateQueue.forSourceWithSize(tokenStream, maxLength);
        this.annotationAttribute = tokenStream.getAttribute(AnnotationAttribute.class);

    }

    public void emitTokenFromQueue() {
        this.stateQueue.pop();
        final List<Annotation> annotations = this.annotationsMap.get(this.emittedToken);
        if (annotations != null) {
            for (final Annotation ann: annotations) {
                this.annotationAttribute.add(ann.key, ann.numTokens);
            }
        }
        this.emittedToken += 1;
        // TODO merge annotation
    }


    private void addAnnotation(final Annotation annotation) {
        final int annStart = this.consumedToken - annotation.numTokens + 1;
        List<Annotation> annotations = this.annotationsMap.get(annStart);
        if (annotations == null) {
            annotations = Lists.newArrayList();
            // TODO  make sure there cannot be any duplicates
            this.annotationsMap.put(annStart, annotations);
        }
        annotations.add(annotation);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (this.tokenRemaining) {
            while (this.input.incrementToken()) {
                final MatchList matchList = new MatchList();
                for (final VocabularyMatcher vocabularyMatcher: this.vocabularyMatchers) {
                    vocabularyMatcher.match(matchList);
                }
                final Set<AhoCorasick.Node> nextNodes = new HashSet<>();
                final TIntSet matchingRuleIds = new TIntHashSet();
                for (final AhoCorasick.Node node: this.curNodes) {
                    for (final int termId: matchList) {
                        final AhoCorasick.Node nextNode = node.goTo(termId);
                        nextNodes.add(nextNode);
                        matchingRuleIds.addAll(nextNode.terminals);
                    }
                }
                if (!nextNodes.isEmpty()) {
                    this.curNodes = nextNodes;
                }
                else {
                    this.curNodes = ImmutableSet.of(this.ahoCorasick.getRoot());
                }
                final TIntIterator it = matchingRuleIds.iterator();
                while (it.hasNext()) {
                    final int matchingRuleId = it.next();
                    final Annotation annotation = this.annotations.get(matchingRuleId);
                    addAnnotation(annotation);
                }

                this.consumedToken += 1;
                stateQueue.push();
                if (stateQueue.isFull()) {
                    emitTokenFromQueue();
                    return true;
                }
            }
            this.tokenRemaining = false;
        }

        if (!this.stateQueue.isEmpty()) {
            this.emitTokenFromQueue();
            return true;
        }
        else {
            return false;
        }

        //this.annotationAttr.reset(); //< not sure about that one.
//        if (!res) {
//            return false;
//        }
        //while (annotationIterator.hasNext()) {
        //    this.annotationAttr.add(annotationIterator.next());
        //
        //  return true;
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
            return ((matchingMethod == that.matchingMethod) && (this.term.equals(that.term)) );

        }

        @Override
        public int hashCode() {
            return this.matchingMethod.hashCode()  + 31 * this.term.hashCode();
        }
    }
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

}
