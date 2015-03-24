package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.multiregexp.Token;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

import static com.fulmicoton.semantic.tokenpattern.PatternTokenType.ANNOTATION;
import static com.fulmicoton.semantic.tokenpattern.PatternTokenType.CLOSE_PARENTHESIS;
import static com.fulmicoton.semantic.tokenpattern.PatternTokenType.COUNT;
import static com.fulmicoton.semantic.tokenpattern.PatternTokenType.DOT;
import static com.fulmicoton.semantic.tokenpattern.PatternTokenType.OPEN_PARENTHESIS;
import static com.fulmicoton.semantic.tokenpattern.PatternTokenType.QUESTION_MARK;
import static com.fulmicoton.semantic.tokenpattern.PatternTokenType.STAR;

public class TokenPatternTest {

    public static void testTokenizer(String ptn, PatternTokenType... expectedTokenTypes) {
        final Iterator<Token<PatternTokenType>> tokenIt = TokenPattern.LEXER.scan(ptn).iterator();
        for (PatternTokenType tokenType: expectedTokenTypes) {
            final Token<PatternTokenType> token = tokenIt.next();
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
