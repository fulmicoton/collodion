package com.fulmicoton.collodion.tokenizer;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.Reader;

public class SolilessTokenizer extends Tokenizer {
    /**
     * Because Lucene's analyzers have for main
     * purpose search, most lucene tokenizers tend to drop
     * all of the whitespaces / punctuation.
     *
     * This class proxifies a Lucene Tokenizer into
     * a tokenizer that justs adds in the "missing tokens"
     * under the type "JUNK", with a position increment of 0.
     *
     * (nb: The nonsensical naming is actually funny for
     * French speakers. Sot-L'y-Laisse (Morons would have left
     * it) is the name of a part of poultry, that is small and
     * so close to the bone, that many people don't eat it
     * although it is very tasty.)
     */

    private GoAgainReader stayBackReader;
    private final Tokenizer underlyingTokenizer;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

    private final CharTermAttribute underlyingTermAttr;
    private final OffsetAttribute underlyingOffsetAttr;
    private final PositionIncrementAttribute underLyingPosIncrAttr;
    private final TypeAttribute underLyingTypeAttr;

    private char[] buffer = new char[1000];

    private State state;

    public SolilessTokenizer(final Tokenizer tokenizer,
                             final Reader reader) {
        super(reader);
        this.underlyingTokenizer = tokenizer;
        this.underlyingOffsetAttr = this.underlyingTokenizer.getAttribute(OffsetAttribute.class);
        this.underLyingPosIncrAttr = this.getAttribute(PositionIncrementAttribute.class);
        this.underlyingTermAttr = this.underlyingTokenizer.getAttribute(CharTermAttribute.class);
        this.underLyingTypeAttr = this.underlyingTokenizer.getAttribute(TypeAttribute.class);
    }

    @Override
    public void reset() throws IOException {
        super.reset();

        this.state = State.INITIAL;
        if (!(this.input instanceof GoAgainReader)) {
            this.input = new GoAgainReader(this.input);
            this.underlyingTokenizer.setReader(this.input);
        }
        this.underlyingTokenizer.reset();
        this.stayBackReader = (GoAgainReader)this.input;
    }

    static enum State {
        INITIAL,
        PENDING
    }


    private void copyUnderlying() {
        final char[] origBuffer = this.underlyingTermAttr.buffer();
        if (origBuffer.length > this.termAtt.buffer().length) {
            this.termAtt.resizeBuffer(origBuffer.length);
        }
        this.termAtt.copyBuffer(origBuffer, 0, this.underlyingTermAttr.length());
        this.termAtt.setLength(this.underlyingTermAttr.length());
        this.offsetAtt.setOffset(this.underlyingOffsetAttr.startOffset(), this.underlyingOffsetAttr.endOffset());
        this.posIncrAtt.setPositionIncrement(this.underLyingPosIncrAttr.getPositionIncrement());
        this.typeAtt.setType(this.underLyingTypeAttr.type());
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (this.state == State.PENDING) {
            copyUnderlying();
            this.stayBackReader.advance(this.termAtt.length());
            this.state = State.INITIAL;
            return true;
        }
        else {
            final int expectedOffset = this.underlyingOffsetAttr.endOffset();
            boolean hasToken = this.underlyingTokenizer.incrementToken();
            if (!hasToken) return false;
            final int givenOffset = this.underlyingOffsetAttr.startOffset();
            if (this.underlyingOffsetAttr.startOffset() >  expectedOffset) {
                this.state = State.PENDING;
                this.typeAtt.setType("<JUNK>");
                final int junkLength = givenOffset - expectedOffset;
                if (junkLength > termAtt.buffer().length) {
                    termAtt.resizeBuffer(junkLength);
                }
                this.termAtt.setLength(junkLength);
                if (junkLength > buffer.length) {
                    buffer = new char[junkLength];
                }
                this.stayBackReader.reread(buffer, 0, junkLength);
                this.stayBackReader.advance(junkLength);
                this.termAtt.copyBuffer(buffer, 0, junkLength);
                this.posIncrAtt.setPositionIncrement(0);
                return true;
            }
            else {
                this.copyUnderlying();
                this.stayBackReader.advance(this.termAtt.length());
                return true;
            }
        }

    }

    @Override
    public void close() throws IOException {
        super.close();
        this.underlyingTokenizer.close();
    }
}
