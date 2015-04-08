package com.fulmicoton.semantic.debug;

import com.google.common.collect.ImmutableMap;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Attribute;

import java.util.Map;

class AttrTypePair {
    final String typeName;
    final Attribute attribute;
    final AttributeFormatter formatter;


    AttrTypePair(Class<? extends Attribute> type, Attribute attribute) {
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
                    TypeAttribute typeAttribute = (TypeAttribute)attribute;
                    return typeAttribute.type();
                }
            })
            .put(PositionIncrementAttribute.class, new AttributeFormatter() {
                @Override
                public String format(Attribute attribute) {
                    final PositionIncrementAttribute positionIncrementAttribute = (PositionIncrementAttribute) attribute;
                    return "+" + positionIncrementAttribute.getPositionIncrement();
                }
            })
            .put(PositionLengthAttribute.class, new AttributeFormatter() {
                @Override
                public String format(Attribute attribute) {
                    final PositionLengthAttribute positionLengthAttribute = (PositionLengthAttribute)attribute;
                    return "" + positionLengthAttribute.getPositionLength();
                }
            })
            .put(OffsetAttribute.class, new AttributeFormatter() {
                @Override
                public String format(Attribute attribute) {
                    final OffsetAttribute offsetAttribute = (OffsetAttribute)attribute;
                    return offsetAttribute.startOffset() + "-" + offsetAttribute.endOffset();
                }
            })
            .build();

    private static final AttributeFormatter DEFAULT_FORMATTER = new AttributeFormatter() {
        @Override
        public String format(final Attribute attribute) {
            return attribute.toString();
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

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("%" + ALIGN_SIZE + "s", this.typeName))
                        .append(": ")
                        .append(this.formatter.format(this.attribute))
                        .toString();
    }
}
