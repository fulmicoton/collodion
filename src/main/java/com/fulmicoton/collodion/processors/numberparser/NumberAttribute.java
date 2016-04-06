package com.fulmicoton.collodion.processors.numberparser;

import com.fulmicoton.collodion.common.Jsonable;
import org.apache.lucene.util.Attribute;

public interface NumberAttribute extends Attribute, Jsonable {

    void reset();
    double val();
    void setVal(double val);
    boolean isSet();


}
