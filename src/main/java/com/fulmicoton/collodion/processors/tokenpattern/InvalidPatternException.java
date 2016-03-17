package com.fulmicoton.collodion.processors.tokenpattern;

public class InvalidPatternException extends Exception {
    private static final long serialVersionUID = 8818586943931490287L;

    public final int lineNumber;
    public final String content;
    public final String explanation;


    public InvalidPatternException(final int line,
                                   final String content,
                                   final String explanation) {
        this.lineNumber = line;
        this.content = content;
        this.explanation = explanation;
    }

    public String toString() {
        return String.format("Invalid pattern at l.%d : %s.\n    >  %s\n", this.lineNumber, this.explanation, this.content);

    }
}
