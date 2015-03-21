package com.fulmicoton.semantic.stemmer;


import com.fulmicoton.common.SubCharSequence;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.RamUsageEstimator;

public class StemAttributeImpl extends AttributeImpl implements StemAttribute {

    private char[] termBuffer;
    private int termLength = 0;

    public StemAttributeImpl() {
        termBuffer = new char[300];
    }

    @Override
    public void clear() {
        this.termLength = 0;
    }

    @Override
    public void copyTo(AttributeImpl target) {
        StemAttributeImpl targetStem = (StemAttributeImpl)target;
        targetStem.termLength = this.termLength;
        if (this.termLength > targetStem.termBuffer.length) {
            targetStem.termBuffer = new char[termLength];
        }
        System.arraycopy(termBuffer, 0, targetStem, 0, this.termLength);
    }

    private void growTermBuffer(int newSize) {
        if(termBuffer.length < newSize){
            termBuffer = new char[ArrayUtil.oversize(newSize, RamUsageEstimator.NUM_BYTES_CHAR)];
        }
    }

    public void copyBuffer(char[] buffer, int offset, int length) {
        growTermBuffer(length);
        System.arraycopy(buffer, offset, termBuffer, 0, length);
        termLength = length;
    }

    @Override
    public int length() {
        return this.termLength;
    }

    @Override
    public char charAt(int index) {
        return this.termBuffer[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new SubCharSequence(this, start, end);
    }

    public String toString() {
        return String.copyValueOf(this.termBuffer, 0, this.termLength);
    }
}


