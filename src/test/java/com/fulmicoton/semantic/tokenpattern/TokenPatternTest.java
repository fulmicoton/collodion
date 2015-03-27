package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken;
import com.fulmicoton.semantic.tokenpattern.regex.TokenPattern;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.ANNOTATION;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.CLOSE_PARENTHESIS;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.COUNT;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.DOT;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.OPEN_PARENTHESIS;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.QUESTION_MARK;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.STAR;

public class TokenPatternTest {

    public static void testTokenizer(String ptn, RegexPatternToken... expectedRegexPatternTokenTypes) {
        final Iterator<Token<RegexPatternToken>> tokenIt = TokenPattern.LEXER.scan(ptn).iterator();
        for (RegexPatternToken regexPatternTokenType : expectedRegexPatternTokenTypes) {
            final Token<RegexPatternToken> token = tokenIt.next();
            Assert.assertEquals(regexPatternTokenType, token.type);
        }
        Assert.assertFalse(tokenIt.hasNext());
    }


    public static void testParser(String ptn, String expected) {
        final TokenPattern tokenPattern = TokenPattern.compile(ptn);
        Assert.assertEquals(tokenPattern.toDebugString(), expected);
    }

    @Test
    public void testTokenization() {
        testTokenizer(".*", DOT, STAR);
        testTokenizer("<abc>{1,2}", ANNOTATION, COUNT);
        testTokenizer("<abc>", ANNOTATION);
        testTokenizer("(<abc>?)<bcd>", OPEN_PARENTHESIS, ANNOTATION, QUESTION_MARK, CLOSE_PARENTHESIS, ANNOTATION);
    }


    @Test
    public void testParser() {
        testParser("(.)", ".");
        testParser(".*", "(.)*");
        testParser("..", "..");
        testParser("<abc>{4,6}", "(<abc>){4,6}");
        testParser("<abc>", "<abc>");
        testParser("(.)", ".");
        testParser("(<abc>?)<bcd>", "(<abc>){0,1}<bcd>");
    }

}
