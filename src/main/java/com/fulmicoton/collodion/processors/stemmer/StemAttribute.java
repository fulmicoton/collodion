package com.fulmicoton.collodion.processors.stemmer;

import org.apache.lucene.util.Attribute;

public interface StemAttribute extends Attribute, CharSequence {
    void copyBuffer(char[] buffer, int offset, int length);

}
