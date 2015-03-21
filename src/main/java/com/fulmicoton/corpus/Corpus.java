package com.fulmicoton.corpus;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Corpus implements Iterable<Document>{

    private static Logger LOG = Logger.getLogger(Corpus.class);


    public final List<Document> docs = new ArrayList<>();

    public static Corpus fromFile(File corpusFile) throws IOException {
        Corpus corpus = new Corpus();
        LineIterator lineIt = FileUtils.lineIterator(corpusFile, "utf-8");
        while (lineIt.hasNext()) {
            final String line = (String)lineIt.next();
            corpus.addDocument(line);
        }
        LOG.info("Loaded " + corpus.getNbDocuments() + " documents");
        return corpus;
    }

    private void addDocument(String text) {
        docs.add(new Document(this.docs.size(), text));
    }

    @Override
    public Iterator<Document> iterator() {
        return this.docs.iterator();
    }

    public Document get(int i) {
        return this.docs.get(i);
    }

    public int getNbDocuments() {
        return this.docs.size();
    }
}
