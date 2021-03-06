package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.common.Annotation;
import com.fulmicoton.collodion.common.AnnotationAttribute;
import com.fulmicoton.collodion.common.SparseQueue;
import com.fulmicoton.collodion.common.StateQueue;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.fulmicoton.collodion.processors.tokenpattern.ast.AST;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.Machine;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.TokenPatternMatchResult;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.TokenPatternMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TokenPatternFilter extends TokenFilter {

    //< Just a simple empty list to avoid building it again and again.
    private static final ImmutableList<Annotation> DO_NOTHING = ImmutableList.of();

    public static class Builder implements ProcessorBuilder<TokenPatternFilter> {

        public String path;
        public int maxLength = 10;
        private transient List<AST> patterns = null;

        @Override
        public void init(final Loader loader) throws IOException {
            if ((this.patterns != null) && (this.path != null)) {
                throw new IllegalArgumentException("Filepath defined even though patterns have been already defined dynamically.");
            }
            this.patterns = new ArrayList<>();
            final BufferedReader reader = loader.read(path);
            int numLine = 0;
            for (String line = reader.readLine();
                 line != null;
                 line = reader.readLine(), numLine++) {
                try {
                    this.readLine(numLine, line);
                }
                catch (final InvalidPatternException e) {
                    throw new IOException(e);
                }
            }
        }

        private void readLine(final int lineNumber, final String line) throws InvalidPatternException {
            final String noCommentLine = line.replaceFirst("#.*", "").trim();
            if (!noCommentLine.isEmpty()) {
                try {
                    final AST tokenPatternRule = AST.compile(noCommentLine);//TokenPatternMapping.parse(noCommentLine);
                    this.patterns.add(tokenPatternRule);
                }
                catch (final Exception e) {
                    throw new InvalidPatternException(lineNumber, noCommentLine, e.getMessage());
                }
            }
        }

        @Override
        public TokenPatternFilter createFilter(final TokenStream prev) throws IOException {
            final MachineBuilder machineBuilder = new MachineBuilder();
            for (final AST patternAST: this.patterns) {
                machineBuilder.addPattern(patternAST);
            }
            final Machine machine = machineBuilder.buildForSearch();
            return new TokenPatternFilter(prev, machine, maxLength);
        }
    }

    final SemToken semToken;
    final int maxLength;

    final StateQueue stateQueue;
    private final TokenPatternMatcher machineRunner;
    private int cursor = 0;
    private int emitted = 0;
    private State state;

    private final AnnotationAttribute annotationAttribute;

    protected TokenPatternFilter(final TokenStream input,
                                 final Machine machine,
                                 final int maxLength) {
        super(input);
        this.maxLength = maxLength;
        this.stateQueue = StateQueue.forSourceWithSize(input, maxLength);
        this.machineRunner = machine.matcher();
        this.annotationAttribute = input.getAttribute(AnnotationAttribute.class);
        this.semToken = new SemToken(input);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.cursor = 0;
        this.stateQueue.reset();
        this.machineRunner.reset(0);
        this.emitted = 0;
        this.state = new Start();
    }

    public boolean loadNextToken() throws IOException {
        if (TokenPatternFilter.this.cursor < TokenPatternFilter.this.emitted + stateQueue.length()) {
            this.stateQueue.loadState(this.cursor);
            this.cursor += 1;
            return true;
        }
        else {
            if (!input.incrementToken()) {
                return false;
            }
            else {
                TokenPatternFilter.this.cursor += 1;
                assert !stateQueue.isFull();
                stateQueue.push();
                return true;
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public interface State {
        // updates the state and returns
        // - null if there will no next token.
        // - the next state.
        State incrementToken() throws IOException;
    }

    public State makeOutputState(final TokenPatternMatchResult matchResult) {
        // Simple implementation first, we output the name of the pattern
        // as an annotation + its groups.
        final int shift = matchResult.start(0);
        final int end = matchResult.end(0);
        final int matchLength = end - shift;
        final SparseQueue<List<Annotation>> annotationsQueue = new SparseQueue<List<Annotation>>(matchLength, DO_NOTHING);
        final int groupCount = matchResult.groupCount();
        for (int groupId = 0; groupId <= groupCount; groupId++) {
            final int startGroup = matchResult.start(groupId) - shift;
            if (startGroup == -1) {
                continue;
            }
            final int endGroup = matchResult.end(groupId) - shift;
            final SparseQueue.Element<List<Annotation>> lastWrittenAnnotations = annotationsQueue.lastWrittenPosition();
            final AnnotationKey annotationKey = matchResult.getAnnotationForGroupId(groupId);
            final Annotation annotation = new Annotation(annotationKey, endGroup - startGroup);
            if ((lastWrittenAnnotations != null) && (lastWrittenAnnotations.position == startGroup)) {
                lastWrittenAnnotations.val.add(annotation);
            }
            else {
                annotationsQueue.add(startGroup, Lists.newArrayList(annotation));
            }
        }
        return new OutputMatch(annotationsQueue);
    }

    public class GreedyMatching implements State {

        final int matchStart;
        TokenPatternMatchResult match;

        GreedyMatching(final TokenPatternMatchResult match) {
            this.match = match;
            this.matchStart = match.start(0);
            machineRunner.killThreadsWithLowerPriority(this.matchStart, match.patternId);
        }

        State selectMatch() throws IOException {
            final State outputMatch = makeOutputState(match);
            machineRunner.reset(cursor);
            if ((matchStart - emitted) > 0) {
                TokenPatternFilter.this.cursor = match.end(0);
                return new Flush(matchStart - emitted, outputMatch).incrementToken();
            }
            else {
                return outputMatch.incrementToken();
            }
        }

        @Override
        public State incrementToken() throws IOException {
            // focus on finding the best match.
            while (machineRunner.hasActiveThreads()) {
                if (stateQueue.isFull()) {
                    emitted += 1;
                    stateQueue.pop();
                    return this;
                }
                if (emitted > this.match.start(0)) {
                    // we cannot progress more, or we will start
                    // releasing tokens that belong to our current match.
                    return selectMatch();
                }
                if (!loadNextToken()) {
                    return selectMatch();
                }
                final TokenPatternMatchResult newMatch = machineRunner.search(semToken);
                if (newMatch != null) {
                    if (newMatch.start(0) >= emitted) {
                        if (newMatch.hasStrictlyHigherPriority(this.match)) {
                            this.match = newMatch;
                        }
                    }
                }
            }
            return selectMatch();
        }
    }

    public class Start implements State {
        Start() {
            machineRunner.reset(cursor);
        }

        @Override
        public State incrementToken() throws IOException {
            if (stateQueue.isFull()) {
                emitted += 1;
                stateQueue.pop();
                return this;
            }
            while (loadNextToken()) {
                final TokenPatternMatchResult match = machineRunner.search(semToken);
                if (match != null) {
                    return new GreedyMatching(match).incrementToken();
                }
                if (stateQueue.isFull()) {
                    emitted += 1;
                    stateQueue.pop();
                    return this;
                }
            }
            // we just flush everything
            return new Flush(-1, null).incrementToken();
        }
    }

    class OutputMatch implements State {
        private final SparseQueue<List<Annotation>> annotationQueue;

        private OutputMatch(final SparseQueue<List<Annotation>> annotationQueue) {
            this.annotationQueue = annotationQueue;
        }

        @Override
        public State incrementToken() throws IOException {
            final List<Annotation> annotations = this.annotationQueue.poll();
            stateQueue.pop();
            emitted += 1;
            for (final Annotation annotation: annotations) {
                annotationAttribute.add(annotation.key, annotation.numTokens);
            }
            if (this.annotationQueue.isEmpty()) {
                return new Start();
            }
            else {
                return this;
            }
        }
    }

    public class Flush implements State {
        /**
         * Outputs one by one <numTokens> elements within the queue,
         * at each call of incrementTokens.
         *
         * Once numTokens have been emitted, returns the next
         * state as defined in next.
         */
        int numTokens;
        final State next;

        Flush(final int numTokens, final State next) {
            assert numTokens != 0;
            this.numTokens = numTokens;
            this.next = next;
        }

        @Override
        public State incrementToken() throws IOException {
            this.numTokens -= 1;
            if (stateQueue.isEmpty()) {
                return null;
            }
            stateQueue.pop();
            emitted += 1;
            if (this.numTokens == 0) {
                return this.next;
            }
            else {
                return this;
            }
        }
    }

    @Override
    public final boolean incrementToken() throws IOException {
        this.state = this.state.incrementToken();
        if (this.state == null) {
            return false;
        }
        return true;
    }
}
