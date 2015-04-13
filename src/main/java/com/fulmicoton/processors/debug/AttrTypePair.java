package com.fulmicoton.processors.debug;

import com.fulmicoton.common.Jsonable;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Attribute;

import java.util.Map;

public class AttrTypePair {

    public final String typeName;
    public final Attribute attribute;
    final AttributeFormatter formatter;

    public AttrTypePair(Class<? extends Attribute> type, Attribute attribute) {
        this.typeName = simplifyName(type.getSimpleName());
        this.attribute = attribute;
        if (ALIGN_SIZE < this.typeName.length()) {
            ALIGN_SIZE = this.typeName.length();
        }
        this.formatter = getFormatter(type);
    }

    private static String simplifyName(final String attrName) {
        if (attrName.endsWith("Attribute")) {
            return attrName.substring(0, attrName.length() - "Attribute".length());
        }
        else {
            return attrName;
        }
    }

    private static int ALIGN_SIZE = 0;
    private static Map<Class<? extends Attribute>, AttributeFormatter> FORMATTER_MAP = ImmutableMap.<Class<? extends Attribute>, AttributeFormatter>builder()
    .put(TypeAttribute.class, new AttributeFormatter() {
        @Override
        public String format(Attribute attribute) {
            final TypeAttribute typeAttribute = (TypeAttribute) attribute;
            return typeAttribute.type();
        }

        @Override
        public void updateJson(JsonObject jsonObj, Attribute attribute) {
            final TypeAttribute typeAttribute = (TypeAttribute) attribute;
            jsonObj.addProperty("type", typeAttribute.type());
        }

    })
    .put(PositionIncrementAttribute.class, new AttributeFormatter() {
        @Override
        public String format(Attribute attribute) {
            final PositionIncrementAttribute positionIncrementAttribute = (PositionIncrementAttribute) attribute;
            return "+" + positionIncrementAttribute.getPositionIncrement();
        }

        @Override
        public void updateJson(JsonObject jsonObj, Attribute attribute) {
            final PositionIncrementAttribute positionIncrementAttribute = (PositionIncrementAttribute) attribute;
            jsonObj.addProperty("posincr", positionIncrementAttribute.getPositionIncrement());
        }
    })
    .put(PositionLengthAttribute.class, new AttributeFormatter() {
        @Override
        public String format(Attribute attribute) {
            final PositionLengthAttribute positionLengthAttribute = (PositionLengthAttribute)attribute;
            return "" + positionLengthAttribute.getPositionLength();
        }

        @Override
        public void updateJson(JsonObject jsonObj, Attribute attribute) {
            final PositionLengthAttribute positionLengthAttribute = (PositionLengthAttribute) attribute;
            jsonObj.addProperty("poslength", positionLengthAttribute.getPositionLength());

        }
    })
    .put(OffsetAttribute.class, new AttributeFormatter() {
        @Override
        public String format(Attribute attribute) {
            final OffsetAttribute offsetAttribute = (OffsetAttribute)attribute;
            return offsetAttribute.startOffset() + "-" + offsetAttribute.endOffset();
        }

        @Override
        public void updateJson(JsonObject jsonObj, Attribute attribute) {
            final OffsetAttribute offsetAttribute = (OffsetAttribute)attribute;
            final JsonObject offsetJson = new JsonObject();
            offsetJson.addProperty("start", offsetAttribute.startOffset());
            offsetJson.addProperty("end", offsetAttribute.endOffset());
            jsonObj.add("offset", offsetJson);
        }
    })
    .build();

    private static final AttributeFormatter DEFAULT_FORMATTER = new AttributeFormatter() {
        @Override
        public String format(final Attribute attribute) {
            return attribute.toString();
        }

        @Override
        public void updateJson(JsonObject jsonObj, Attribute attribute) {
            if (attribute instanceof Jsonable) {
                ((Jsonable) attribute).updateJson(jsonObj);
            }
        }
    };

    private static AttributeFormatter getFormatter(Class<? extends Attribute> attrClass) {
        final AttributeFormatter formatter = FORMATTER_MAP.get(attrClass);
        if (formatter == null) {
            return DEFAULT_FORMATTER;
        } else {
            return formatter;
        }
    }

    public void updateJson(final JsonObject jsonObject) {
        this.formatter.updateJson(jsonObject, this.attribute);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("%" + ALIGN_SIZE + "s", this.typeName))
                        .append(": ")
                        .append(this.formatter.format(this.attribute))
                        .toString();
    }

}
