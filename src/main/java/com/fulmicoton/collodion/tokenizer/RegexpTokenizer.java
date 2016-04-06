package com.fulmicoton.collodion.tokenizer;

import com.fulmicoton.multiregexp.Lexer;
import com.fulmicoton.multiregexp.ScanException;
import com.fulmicoton.multiregexp.Scanner;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.Reader;

public class RegexpTokenizer extends Tokenizer {

    public static enum TokenType {
        WORD,
        SYMBOL,
        NUMBER,
        WHITESPACE
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.scanner.reset();

    }

    private final Scanner<TokenType> scanner;

    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);

    public static class Configuration {
        private final transient Lexer<TokenType> lexer;

        public Configuration(final Lexer<TokenType> lexer) {
            this.lexer = lexer;
        }
    }
    public RegexpTokenizer(final Reader input, final RegexpTokenizer.Configuration configuration) {
        super(input);
        this.scanner = configuration.lexer.scannerFor(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        try {
            while (this.scanner.next()) {
                if (this.scanner.type != TokenType.WHITESPACE) {
                    this.offsetAtt.setOffset(this.scanner.start, this.scanner.end);
                    typeAtt.setType(this.scanner.type.name());
                    this.termAtt.setEmpty();
                    this.termAtt.append(this.scanner.tokenString());
                    return true;
                }
            }
            return false;
        } catch (final ScanException e) {
            throw new IOException(e);
        }

    }
}
