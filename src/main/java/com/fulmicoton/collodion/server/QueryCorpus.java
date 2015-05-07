package com.fulmicoton.collodion.server;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.AnalysisExecutor;
import com.fulmicoton.collodion.corpus.Corpus;
import com.fulmicoton.collodion.corpus.FilteredCorpus;
import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.fulmicoton.collodion.processors.tokenpattern.TokenPatternFilter;
import com.fulmicoton.collodion.server.tasks.AnalysisTask;
import com.fulmicoton.collodion.server.tasks.ContainAnnotation;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import org.apache.lucene.document.Document;

class QueryCorpus extends CacheLoader<QueryCorpus.Key, Corpus> {

    private final AnalysisExecutor executor;

    static class Key {
        final Corpus corpus;
        final CollodionAnalyzer analyzer;
        final String query;

        Key(final Corpus corpus,
            final CollodionAnalyzer analyzer,
            final String query)
        {
            this.query = query;
            this.corpus = corpus;
            this.analyzer = analyzer;
        }
    }

    QueryCorpus(final AnalysisExecutor executor) {
        this.executor = executor;
    }


    @Override
    public Corpus load(final Key key) throws Exception {
        final ProcessorBuilder processorBuilder = TokenPatternFilter
                .builder()
                .addPattern(key.query);
        final AnalysisExecutor executor = this.executor;
        final CollodionAnalyzer analyzerWithQuery = key.analyzer.append(processorBuilder);
        final Predicate<Document> grepPredicate = new Predicate<Document>() {
            @Override
            public boolean apply(Document doc) {
                final AnnotationKey annKey = AnnotationKey.of("pattern0.0");
                final AnalysisTask<Boolean> grepTask = new ContainAnnotation(analyzerWithQuery, annKey);
                try {
                    return executor.call("text", doc, grepTask);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return FilteredCorpus.filter(key.corpus, grepPredicate);
    }
}
