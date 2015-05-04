package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.common.StateQueue;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.Machine;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.TokenPatternMatchResult;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.TokenPatternMatcher;
import com.fulmicoton.collodion.processors.vocabularymatcher.Annotation;
import com.fulmicoton.collodion.processors.vocabularymatcher.VocabularyAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TokenPatternFilter extends TokenFilter {

    public static class Builder implements ProcessorBuilder<TokenPatternFilter> {

        public String path;
        public int maxLength = 10;
        private transient List<String> patterns = new ArrayList<>();

        @Override
        public void init(final Loader loader) throws IOException {
            final BufferedReader reader = loader.read(path);
            for (String line = reader.readLine();
                 line != null;
                 line = reader.readLine()) {
                this.readLine(line);
            }
        }

        private void readLine(final String line) {
            final String noCommentLine = line.replaceFirst("#\\.*", "").trim();
            if (line.length() == 0) return;
            this.patterns.add(noCommentLine);
        }

        @Override
        public TokenPatternFilter createFilter(TokenStream prev) throws IOException {
            final MachineBuilder machineBuilder = new MachineBuilder();
            for (final String pattern: this.patterns) {
                machineBuilder.add(pattern);
            }
            final Machine machine = machineBuilder.buildForSearch();
            return new TokenPatternFilter(prev, machine, maxLength);
        }
    }

    final StateQueue stateQueue;
    final SemToken semToken;

    final int maxLength;
    private final TokenPatternMatcher machineRunner;
    private int emitted = 0;
    private State state;

    private VocabularyAttribute vocabularyAttribute;
    private PositionIncrementAttribute positionIncrementAttribute;
    private PositionLengthAttribute positionLengthAttribute;

    protected TokenPatternFilter(final TokenStream input,
                                 final Machine machine,
                                 final int maxLength) {
        super(input);
        this.maxLength = maxLength;
        this.stateQueue = StateQueue.forSourceWithSize(input, maxLength);
        this.machineRunner = machine.matcher();
        this.semToken = new SemToken(input);
        this.positionIncrementAttribute = input.getAttribute(PositionIncrementAttribute.class);
        this.positionLengthAttribute = input.getAttribute(PositionLengthAttribute.class);
        this.vocabularyAttribute = input.getAttribute(VocabularyAttribute.class);
    }


    @Override
    public void reset() throws IOException {
        super.reset();
        this.emitted = 0;
        this.state = new Start();
        this.machineRunner.reset();
    }

    public interface State {
        // updates the state and returns
        // - null if there will no next token.
        // - the next state.
        public State incrementToken() throws IOException;
    }


    public State makeOutputState(final TokenPatternMatchResult matchResult) {
        // Simple implementation first, we output the name of the pattern
        // as an annotation + its groups.
        final Queue<List<Annotation>> annotations = new LinkedList<>();
        for (int groupId = 0; groupId < matchResult.groupCount(); groupId++) {
            
        }
        return new OutputMatch(annotations);
    }

    public class Start implements State {

        Start() {
            emitted = 0;
            machineRunner.reset();
        }

        @Override
        public State incrementToken() throws IOException {
            while (input.incrementToken()) {
                stateQueue.push();
                final TokenPatternMatchResult match = machineRunner.search(semToken);
                if (match != null) {
                    int matchStart = match.start(0);
                    final State outputMatch = makeOutputState(match);
                    if (matchStart - emitted > 0) {
                        return new Flush(matchStart - emitted, outputMatch).incrementToken();
                    }
                    else {
                        return outputMatch;
                    }
                }
                else {
                    if (stateQueue.isFull()) {
                        // the queue is full, we need to release
                        // a token.
                        emitted += 1;
                        stateQueue.pop();
                        return this;
                    }
                }
            }

            // we just flush everything
            return new Flush(-1, null).incrementToken();
        }
    }

    class OutputMatch implements State {

        private final Queue<List<Annotation>> annotationQueue;

        private OutputMatch(final Queue<List<Annotation>> annotationQueue) {
            this.annotationQueue = annotationQueue;
        }

        @Override
        public State incrementToken() throws IOException {
            final List<Annotation> annotations = this.annotationQueue.poll();
            stateQueue.pop();
            emitted += 1;
            for (final Annotation annotation: annotations) {
                vocabularyAttribute.add(annotation.key, annotation.nbTokens);
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
         * Outputs one by one <nbTokens> elements within the queue,
         * at each call of incrementTokens.
         *
         * Once nbTokens have been emitted, returns the next
         * state as defined in next.
         */
        int nbTokens;
        final State next;

        Flush(int nbTokens, State next) {
            assert nbTokens != 0;
            this.nbTokens = nbTokens;
            this.next = next;
        }

        @Override
        public State incrementToken() throws IOException {
            nbTokens -= 1;
            stateQueue.pop();
            emitted += 1;
            if ((nbTokens == 0) || stateQueue.isEmpty()) {
                return this.next;
            }
            else {
                return this;
            }
        }
    }



    @Override
    public final boolean incrementToken() throws IOException {
        if (this.state == null) {
            return false;
        }
        this.state = this.state.incrementToken();
        return true;
    }
}
