package com.fulmicoton.collodion.processors.debug;

import com.google.gson.JsonObject;
import org.apache.lucene.util.Attribute;

interface AttributeFormatter {

    public String format(final Attribute attribute);
    public void updateJson(final JsonObject jsonObj, final Attribute attribute);

}
