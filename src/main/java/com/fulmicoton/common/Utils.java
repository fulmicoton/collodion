package com.fulmicoton.common;

import com.fulmicoton.semantic.debug.AttrTypePair;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Attribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {

    public static JsonElement toJson(final TokenStream input) throws IOException {
        final Iterator<Class<? extends Attribute>> attributeClassIt = input.getAttributeClassesIterator();
        final List<AttrTypePair> attributes = new ArrayList<>();
        while (attributeClassIt.hasNext()) {
            final Class<? extends Attribute> attrClass = attributeClassIt.next();
            final Attribute attr = input.getAttribute(attrClass);
            attributes.add(new AttrTypePair(attrClass, attr));
        }
        final JsonArray jsonArray = new JsonArray();
        while (input.incrementToken()) {
            final JsonObject jsonObj = new JsonObject();
            for (AttrTypePair attrTypePair: attributes) {
                attrTypePair.updateJson(jsonObj);
            }
            jsonArray.add(jsonObj);
        }
        return jsonArray;
    }

}
