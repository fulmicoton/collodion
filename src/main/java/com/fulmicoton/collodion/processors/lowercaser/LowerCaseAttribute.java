package com.fulmicoton.collodion.processors.lowercaser;

import org.apache.lucene.util.Attribute;

public interface LowerCaseAttribute extends Attribute, CharSequence {
    void copyBuffer(char[] buffer, int offset, int length);
    char[] buffer();
    void setLength(int charTermLength);
}
