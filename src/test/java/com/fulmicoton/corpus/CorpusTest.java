package com.fulmicoton.corpus;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

public class CorpusTest {

    @Test
    public void testCorpus() throws IOException {
        final Corpus corpus = CorpusImpl.fromPath("corpus/corpus-simple.json");
        Assert.assertEquals(corpus.size(), 2);
        Assert.assertEquals(corpus.get(1).get("title"), "title2");
    }

}
