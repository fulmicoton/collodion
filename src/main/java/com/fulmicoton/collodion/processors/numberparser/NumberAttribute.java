package com.fulmicoton.collodion.processors.numberparser;

import com.fulmicoton.collodion.common.Jsonable;
import org.apache.lucene.util.Attribute;

public interface NumberAttribute extends Attribute, Jsonable {

    public void reset();
    public double val();
    public void setVal(double val);
    public boolean isSet();

}
