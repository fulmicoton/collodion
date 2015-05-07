package com.fulmicoton.collodion.server.tasks;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.AnnotationAttribute;
import com.fulmicoton.collodion.processors.AnnotationKey;
import org.apache.lucene.analysis.TokenStream;


public class ContainAnnotation extends AnalysisTask<Boolean> {

    final AnnotationKey annotationKey;

    public ContainAnnotation(final CollodionAnalyzer analyzer,
                             final AnnotationKey annotationKey) {
        super(analyzer);
        this.annotationKey = annotationKey;
    }

    @Override
    public Boolean process(TokenStream tokenStream) throws Exception {

        try {
            tokenStream.reset();
            final AnnotationAttribute annAttr = tokenStream.getAttribute(AnnotationAttribute.class);
            while (tokenStream.incrementToken()) {
                if (annAttr.contains(annotationKey)) {
                    return true;
                }
            }
            return false;
        }
        finally {
            tokenStream.end();
            tokenStream.close();
        }
    }
}
