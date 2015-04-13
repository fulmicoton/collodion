package com.fulmicoton.processors.numberparser;

import org.apache.lucene.util.AttributeImpl;

public class NumberAttributeImpl extends AttributeImpl implements NumberAttribute {

    private double val;

    @Override
    public void reset() {
        this.val = Double.NaN;
    }

    @Override
    public double val() {
        return this.val;
    }

    @Override
    public void setVal(double val) {
        this.val = val;
    }

    @Override
    public boolean isSet() {
        return !Double.isNaN(this.val);
    }

    @Override
    public String toString() {
        if (this.isSet()) {
            return "|" + this.val();
        }
        else {
            return "None";
        }
    }

    @Override
    public void clear() {
        this.reset();
    }

    @Override
    public void copyTo(AttributeImpl target) {

    }
}
