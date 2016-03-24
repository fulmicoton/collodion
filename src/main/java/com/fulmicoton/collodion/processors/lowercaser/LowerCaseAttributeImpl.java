package com.fulmicoton.collodion.processors.lowercaser;


import com.fulmicoton.collodion.common.Jsonable;
import com.fulmicoton.collodion.common.SubCharSequence;
import com.google.gson.JsonObject;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.RamUsageEstimator;

public class LowerCaseAttributeImpl extends AttributeImpl implements LowerCaseAttribute, Jsonable {

    private char[] termBuffer;
    private int termLength = 0;

    public LowerCaseAttributeImpl() {
        termBuffer = new char[300];
    }

    public char[] buffer() {
        return this.termBuffer;
    }

    @Override
    public void setLength(final int charTermLength) {
        this.growTermBuffer(charTermLength);
        this.termLength = charTermLength;
    }

    @Override
    public void clear() {
        this.termLength = 0;
    }

    @Override
    public void copyTo(final AttributeImpl target) {
        final LowerCaseAttributeImpl lowerCaseImpl = (LowerCaseAttributeImpl)target;
        lowerCaseImpl.termLength = this.termLength;
        if (this.termLength > lowerCaseImpl.termBuffer.length) {
            lowerCaseImpl.termBuffer = new char[termLength];
        }
        lowerCaseImpl.termLength = this.termLength;
        System.arraycopy(termBuffer, 0, lowerCaseImpl.termBuffer, 0, this.termLength);
    }

    public void growTermBuffer(final int newSize) {
        if(termBuffer.length < newSize){
            termBuffer = new char[ArrayUtil.oversize(newSize, RamUsageEstimator.NUM_BYTES_CHAR)];
        }
    }

    public void copyBuffer(final char[] buffer, final int offset, final int length) {
        growTermBuffer(length);
        System.arraycopy(buffer, offset, termBuffer, 0, length);
        termLength = length;
    }

    @Override
    public LowerCaseAttributeImpl clone() {
        final LowerCaseAttributeImpl clone = new LowerCaseAttributeImpl();
        this.copyTo(clone);
        return clone;
    }

    @Override
    public int length() {
        return this.termLength;
    }

    @Override
    public char charAt(final int index) {
        return this.termBuffer[index];
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return new SubCharSequence(this, start, end);
    }

    public String toString() {
        return String.copyValueOf(this.termBuffer, 0, this.termLength);
    }

    @Override
    public void updateJson(final JsonObject jsonObject) {
        jsonObject.addProperty("stem", this.toString());
    }
}


