package com.fulmicoton.corpus;

public class Document {
    final private int docId;
    final private String text;

    Document(int docId, String text) {
        this.docId = docId;
        this.text = text;
    }

    public int docId() {
        return this.docId;
    }

    public String getText() {
        return this.text;
    }

}
