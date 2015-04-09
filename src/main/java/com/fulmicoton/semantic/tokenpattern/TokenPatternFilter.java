package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.common.StateQueue;
import com.fulmicoton.common.loader.Loader;
import com.fulmicoton.semantic.ProcessorBuilder;
import com.fulmicoton.semantic.tokenpattern.nfa.Machine;
import com.fulmicoton.semantic.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.semantic.tokenpattern.nfa.TokenPatternMatchResult;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final Machine machine;
    private final com.fulmicoton.semantic.tokenpattern.nfa.TokenPatternMatcher machineRunner;

    protected TokenPatternFilter(final TokenStream input,
                                 final Machine machine,
                                 final int maxLength) {
        super(input);
        this.maxLength = maxLength;
        this.stateQueue = StateQueue.forSourceWithSize(input, maxLength);
        this.machine = machine;
        this.machineRunner = machine.matcher();
        this.semToken = new SemToken(input);
    }


    @Override
    public void reset() throws IOException {
        super.reset();
        this.machineRunner.reset();
    }

    @Override
    public final boolean incrementToken() throws IOException {

        while (input.incrementToken()) {

            final TokenPatternMatchResult match = this.machineRunner.search(this.semToken);
            if (match != null) {
                // TODO we output the match as an annotation
                // TODO queue up group annotations
            }
            else {
                this.stateQueue.push();
                if (this.stateQueue.isFull()) {
                    // the queue is full, we need to release
                    // a token.
                    this.stateQueue.pop();
                    return true;
                }
            }
        }

        // if we reach the end of the stream, we just
        // output the saved/pending tokens.
        if (!this.stateQueue.isEmpty()) {
            this.stateQueue.pop();
            return true;
        }
        else {
            return false;
        }


    }
}
