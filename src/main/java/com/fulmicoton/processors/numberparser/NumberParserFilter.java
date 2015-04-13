package com.fulmicoton.processors.numberparser;

import com.fulmicoton.common.StateQueue;
import com.fulmicoton.common.loader.Loader;
import com.fulmicoton.multiregexp.MultiPattern;
import com.fulmicoton.multiregexp.MultiPatternAutomaton;
import com.fulmicoton.processors.ProcessorBuilder;
import com.google.common.collect.Lists;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.List;

public class NumberParserFilter extends TokenFilter {


    public static class Builder implements ProcessorBuilder<NumberParserFilter> {

        @Override
        public void init(Loader loader) throws IOException {
        }

        @Override
        public NumberParserFilter createFilter(TokenStream prev) throws IOException {
            final List<String> patterns = Lists.newArrayList();
            final List<NumberInterpreter> numberInterpreters = Lists.newArrayList();

            // 12,000.12
            patterns.add("[0-9]+(\\,?[0-9]{3})*(\\.[0-9]+)?");
            numberInterpreters.add(NumberInterpreter.ENGLISH);

            // 12.000,12
            patterns.add("[0-9]+(\\.?[0-9]{3})*(\\,[0-9]+)?");
            numberInterpreters.add(NumberInterpreter.FRENCH);


            return new NumberParserFilter(prev, MultiPattern.of(patterns), numberInterpreters);
        }
    }


    // ------------------------


    final static int MAX_STATES = 3;
    final MultiPatternAutomaton automaton;
    final StringBuffer buffer = new StringBuffer(300);
    final StateQueue stateQueue = StateQueue.forSourceWithSize(this, MAX_STATES);
    final List<NumberInterpreter> numberInterpreters;
    final CharTermAttribute termAttr;
    final NumberAttribute numberAttribute;

    protected NumberParserFilter(final TokenStream input,
                                 final MultiPattern multiPattern,
                                 final List<NumberInterpreter> numberInterpreters) {
        super(input);
        this.automaton = multiPattern.makeAutomatonWithPrefix("");
        this.termAttr = input.getAttribute(CharTermAttribute.class);
        this.numberAttribute = input.addAttribute(NumberAttribute.class);
        this.numberInterpreters = numberInterpreters;
    }

    public boolean peekAhead(int posAhead) throws IOException {
        // stateQueue = 0, posAhead = 0 --> 1
        for (int i=0; i<posAhead - this.stateQueue.size() + 1; i++) {
            if (!this.input.incrementToken()) {
                return false;
            }
            this.stateQueue.push();
        }
        this.stateQueue.peekAhead(posAhead);
        return true;
    }


    @Override
    public final boolean incrementToken() throws IOException {
        int p = 0;
        int matchedPattern = -1;
        int matchedNbTokens = 0;
        outerloop:
        for (int nbToken = 0; nbToken < MAX_STATES; nbToken++) {
            // TODO Perf get rid of the extraneous copy in the default case.
            if (!peekAhead(nbToken)) {
                if (nbToken == 0) {
                    // no more tokens apparently.
                    return false;
                }
                else {
                    break outerloop;
                }
            }
            for (int charId = 0; charId < this.termAttr.length(); charId++) {
                char c = this.termAttr.charAt(charId);
                p = automaton.step(p, c);
                if (p < 0) {
                    break outerloop;
                }
                else {
                    int[] accepted = automaton.accept[p];
                    if (accepted.length > 0) {
                        matchedPattern = accepted[0];
                        matchedNbTokens = nbToken + 1;
                    }
                }
            }
        }
        if (matchedPattern == -1) {
            this.numberAttribute.reset();
            this.stateQueue.pop();
            return true;
        }
        else {
            assert matchedNbTokens > 0;
            buffer.setLength(0);
            for (int i=0; i<matchedNbTokens; i++) {
                this.stateQueue.pop();
                buffer.append(this.termAttr);
            }
            this.termAttr.setEmpty();
            this.termAttr.append(buffer);
            final NumberInterpreter numberInterpreter = this.numberInterpreters.get(matchedPattern);
            double val = numberInterpreter.read(buffer.toString());
            this.numberAttribute.setVal(val);
        }
        return true;
    }

    @Override
    public void reset() throws IOException{
        super.reset();
        stateQueue.reset();
    }
}
