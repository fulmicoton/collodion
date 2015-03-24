package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.multiregexp.Token;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

import static com.fulmicoton.semantic.tokenpattern.TokenT.ANNOTATION;
import static com.fulmicoton.semantic.tokenpattern.TokenT.CLOSE_PARENTHESIS;
import static com.fulmicoton.semantic.tokenpattern.TokenT.COUNT;
import static com.fulmicoton.semantic.tokenpattern.TokenT.DOT;
import static com.fulmicoton.semantic.tokenpattern.TokenT.OPEN_PARENTHESIS;
import static com.fulmicoton.semantic.tokenpattern.TokenT.QUESTION_MARK;
import static com.fulmicoton.semantic.tokenpattern.TokenT.STAR;

public class TokenPatternTest {

    public static void testTokenizer(String ptn, TokenT... expectedTokenTypes) {
        final Iterator<Token<TokenT>> tokenIt = TokenPattern.LEXER.scan(ptn).iterator();
        for (TokenT tokenType: expectedTokenTypes) {
            final Token<TokenT> token = tokenIt.next();
            Assert.assertEquals(tokenType, token.type);
        }
        Assert.assertFalse(tokenIt.hasNext());
    }

    @Test
    public void testTokenization() {
        testTokenizer(".*", DOT, STAR);
        testTokenizer("<abc>{1,2}", ANNOTATION, COUNT);
        testTokenizer("<abc>", ANNOTATION);
        testTokenizer("(<abc>?)<bcd>", OPEN_PARENTHESIS, ANNOTATION, QUESTION_MARK, CLOSE_PARENTHESIS, ANNOTATION);

    }


}