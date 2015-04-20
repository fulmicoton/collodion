package com.fulmicoton.collodion.utils;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.corpus.Corpus;
import com.fulmicoton.collodion.corpus.SimpleCorpus;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.google.common.collect.ImmutableList;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Benchmark {



    private static long timeRun(final Corpus corpus,
                         final CollodionAnalyzer analyzer) throws IOException {

        long start = System.currentTimeMillis();
        for (final Document doc: corpus) {
            final String text = doc.get("text");
            final TokenStream tokenStream = analyzer.tokenStream("text", text);
            tokenStream.reset();
            int nbTokens = 0;
            while (tokenStream.incrementToken()) {
                nbTokens += 1;
            }
            tokenStream.close();
        }
        long stop = System.currentTimeMillis();
        return stop - start;
    }

    public static void bench(
            final Corpus corpus,
            final CollodionAnalyzer analyzer) throws IOException {

        ImmutableList<ProcessorBuilder> processorBuilders = analyzer.processorBuilders();
        List<Double> stepTimes = new ArrayList<>();
        for (int i = 0; i <= processorBuilders.size(); i++) {
            final List<ProcessorBuilder> subListProcessorBuilders = processorBuilders.subList(0, i);
            final CollodionAnalyzer partialAnalyzer = new CollodionAnalyzer(subListProcessorBuilders);
            timeRun(corpus, partialAnalyzer);
            long result = timeRun(corpus, partialAnalyzer);
            final double averagePerDoc = (double)result / corpus.size();
            System.out.println(averagePerDoc);
            stepTimes.add(averagePerDoc);
        }
    }

    public static void main(String[] args) throws Exception {
        final Corpus corpus = SimpleCorpus.fromPath("US.json");
        final CollodionAnalyzer collodionAnalyzer = CollodionAnalyzer.fromPath("pipeline-benchmark.json");
        bench(corpus, collodionAnalyzer);
    }

}
