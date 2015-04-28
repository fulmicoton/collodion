package com.fulmicoton.collodion.corpus;


import org.apache.lucene.document.Document;

public interface Corpus extends Iterable<Document> {

    public int size();

    public Document get(int docId);
}
