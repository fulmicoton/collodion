package com.fulmicoton.processors.numberparser;

import org.apache.lucene.util.Attribute;

public interface NumberAttribute extends Attribute {

    public void reset();
    public double val();
    public void setVal(double val);
    public boolean isSet();

}
