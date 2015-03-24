package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.Annotation;

public enum PatternTokenType {

    OPEN_PARENTHESIS,

    CLOSE_PARENTHESIS,

    ANNOTATION {
        Annotation parse(final String match) {
            final String annotationName = match.substring(0, match.length() - 1);
            return Annotation.of(annotationName);
        }
    },

    COUNT {
        CountParam parse(final String match) throws ParsingError {
            final String countString = match.substring(0, match.length() - 1);
            String[] parts = countString.split(",");
            if (parts.length == 1) {
                int val = Integer.valueOf(parts[0]);
                return new CountParam(val, val);
            }
            else {
                int minCount = Integer.valueOf(parts[0]);
                int maxCount = Integer.valueOf(parts[1]);
                return new CountParam(minCount, maxCount);
            }
        }
    },

    DOT,

    PLUS,

    STAR,

    QUESTION_MARK

}
