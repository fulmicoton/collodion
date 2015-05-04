package com.fulmicoton.collodion.corpus;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.apache.lucene.document.Document;

import java.util.Iterator;
import java.util.List;

public class FilteredCorpus implements Corpus {

    private final Corpus corpus;
    private final List<Integer> docIds;

    public FilteredCorpus(final Corpus corpus,
                          final List<Integer> docIds) {
        this.corpus = corpus;
        this.docIds = docIds;
    }

    @Override
    public int size() {
        return this.docIds.size();
    }

    @Override
    public Document get(int docId) {
        final int toId = this.docIds.get(docId);
        return this.corpus.get(toId);
    }

    @Override
    public Iterator<Document> iterator() {
        final Iterator<Integer> docIdIt = this.docIds.iterator();
        final Corpus corpus = this.corpus;
        return new Iterator<Document>() {
            @Override
            public boolean hasNext() {
                return docIdIt.hasNext();
            }

            @Override
            public Document next() {
                final int nextId = docIdIt.next();
                return corpus.get(nextId);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Corpus filter(final Corpus corpus, final Predicate<Document> predicate) {
        final List<Integer> keptDocIds = Lists.newArrayList();
        int docId = 0;
        final Iterator<Document> docIt = corpus.iterator();
        while (docIt.hasNext()) {
            final Document doc = docIt.next();
            if (predicate.apply(doc)) {
                keptDocIds.add(docId);
            }
            docId++;
        }
        return new FilteredCorpus(corpus, keptDocIds);
    }
}
