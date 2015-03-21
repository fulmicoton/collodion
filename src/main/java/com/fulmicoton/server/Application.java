package com.fulmicoton.server;

import com.fulmicoton.corpus.Corpus;
import com.fulmicoton.corpus.Document;
import com.fulmicoton.corpus.Fragment;
import com.fulmicoton.semantic.SemanticAnalyzer;
import com.fulmicoton.semantic.Vocabulary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum Application {

    INSTANCE;

    public static Application get() {
        return INSTANCE;
    }

    private static final int RADIUS = 10;
    private SemanticAnalyzer semanticPipe;
    private Corpus corpus;
    private List<Fragment> fragments;

    Application() {
        try {
            this.corpus = Corpus.fromFile(new File("US.txt"));
            this.setVocabulary(new Vocabulary());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setVocabulary(Vocabulary vocabulary) {
        /*
        try {
            this.semanticPipe = SemanticAnalyzer.buildSemanticPipe(vocabulary);
            this.fragments = this.createFragments();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        */
    }

    public List<Fragment> createFragments() {
        List<Fragment> fragments = new ArrayList<>();
        /*
        for (Document doc: corpus){
            //Annotation annotatedDocument = //this.semanticPipe.process(doc.getText());
            Annotation annotatedDocument = null;
            for(CoreMap sentence: annotatedDocument.of(CoreAnnotations.SentencesAnnotation.class)) {
                final List<Token> tokens = new ArrayList<>();
                final List<Integer> tokenNumberIds = new ArrayList<>();
                int tokenId = 0;
                for (CoreLabel coreLabel: sentence.of(CoreAnnotations.TokensAnnotation.class)) {
                    Token token = new Token(coreLabel);
                    if ("NUMBER".equals(token.sem)) {
                        tokenNumberIds.add(tokenId);
                    }
                    tokens.add(token);
                    tokenId += 1;
                }
                for (Integer tokenNumberId: tokenNumberIds) {
                    final int startId = Math.max(0, tokenNumberId - RADIUS);
                    final int stopId = Math.min(tokens.size(), tokenNumberId + RADIUS);
                    final int relativeId = tokenNumberId - startId;
                    fragments.add(new Fragment(tokens.subList(startId, stopId), relativeId));
                }
            }
        }
        */
        return fragments;
    }


    public Vocabulary getVocabulary() {
        // return this.semanticPipe.getVocabulary();
        return null;
    }


    public Document getDocument(int i) {
        return this.corpus.get(i);
    }

    public int getNbDocuments() {
        return this.corpus.getNbDocuments();
    }

    public Fragment getFragment(int i) {
        return this.fragments.get(i);
    }

    public int getNbFragments() {
        return this.fragments.size();
    }

}
