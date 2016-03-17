package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.tokenpattern.ast.AST;

class TokenPatternMapping {
    final AnnotationKey annotationKey;
    final AST definition;

    TokenPatternMapping(final AnnotationKey annotationKey, final AST definition) {
        this.annotationKey = annotationKey;
        this.definition = definition;
    }

    public static TokenPatternMapping parse(final String line) throws Exception {
        final String[] parts = line.split(":=");
        if (parts.length != 2) {
            throw new Exception("The lineNumber " + line + "is not a proper pattern definition. It should look like <name> := <pattern definition>");
        }
        final AnnotationKey annotationKey = AnnotationKey.of(parts[0].trim());
        final AST ast = AST.compile(parts[1]);
        return new TokenPatternMapping(annotationKey, ast);
    }
}
