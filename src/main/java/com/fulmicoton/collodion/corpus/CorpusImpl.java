package com.fulmicoton.collodion.corpus;

import com.fulmicoton.collodion.common.JSON;
import com.fulmicoton.collodion.common.loader.Loader;
import com.fulmicoton.collodion.common.loader.ResourceLoader;
import com.google.common.collect.Lists;
import org.apache.lucene.document.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class CorpusImpl implements Corpus {

    private final List<Document> documents;

    private CorpusImpl(final List<Document> documents) {
        this.documents = documents;
    }

    public static CorpusImpl fromPath(final String path) throws IOException {
        return fromPath(path, ResourceLoader.DEFAULT_LOADER);
    }

    public static CorpusImpl fromPath(final String path, final Loader loader) throws IOException {
        return fromReader(loader.read(path));
    }

    private static final Pattern COMMENTS = Pattern.compile("#.*");
    public static CorpusImpl fromReader(final BufferedReader reader) throws IOException {
        final List<Document> documents = Lists.newArrayList();
        for (String line=reader.readLine();
             line!=null;
             line=reader.readLine()) {
            final String cleanLine = COMMENTS.matcher(line).replaceAll("").trim();
            if (!cleanLine.isEmpty()) {
                documents.add(JSON.fromJson(line, Document.class));
            }
        }
        return new CorpusImpl(documents);
    }

    @Override
    public int size() {
        return this.documents.size();
    }

    @Override
    public Document get(int docId) {
        return this.documents.get(docId);
    }

    @Override
    public Iterator<Document> iterator() {
        return this.documents.iterator();
    }
}
