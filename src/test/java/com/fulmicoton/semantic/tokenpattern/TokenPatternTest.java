package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken;
import com.fulmicoton.semantic.tokenpattern.regex.SemToken;
import com.fulmicoton.semantic.tokenpattern.regex.TokenPattern;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.ANNOTATION;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.CLOSE_PARENTHESIS;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.COUNT;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.DOT;
import static com.fulmicoton.semantic.tokenpattern.regex.RegexPatternToken.OR;
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
        testTokenizer("<abc>|<bcd>", ANNOTATION, OR, ANNOTATION);
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
        testParser("<abc><bcd>+", "<abc><bcd>(<bcd>)*");
        testParser("(<abc><bcd>)+", "<abc><bcd>(<abc><bcd>)*");
        testParser("(<b>|<a>)+", "(<b>)|(<a>)((<b>)|(<a>))*");
    }


    public void testTokenPatternMatch(String ptn, String testString, boolean expected) {
        final String[] tokens = testString.split(" ");
        final List<SemToken> tokenList = new ArrayList<>();
        for (String token: tokens) {
            tokenList.add(new SemToken(Annotation.of(token)));
        }
        Assert.assertEquals(TokenPattern.compile(ptn).match(tokenList.iterator()).matches(), expected);
    }

    @Test
    public void testPatternNFA() {
        /*
        testTokenPatternMatch("<a>*", "a a a", true);
        testTokenPatternMatch("<a>+", "a a b", false);
        testTokenPatternMatch("<a>+<b>+<a>", "a a a", false);
        testTokenPatternMatch("<a>+<b>*<a>", "a a a", true);
        testTokenPatternMatch("<a>?<a><b>", "a a b", true);
        testTokenPatternMatch("<a>?<a><b>", "a b", true);
        testTokenPatternMatch("<a><b>", "a b", true);
        testTokenPatternMatch("<a>{2,3}", "a a", true);
        testTokenPatternMatch("<b><a>{2,3}", "b a a", true);
        testTokenPatternMatch("<b><a>{2,3}", "b a", false);
        testTokenPatternMatch("<b><a>{2,3}", "b a a a", true);
        testTokenPatternMatch("<b><a>{2,3}", "b a a a a", false);
        testTokenPatternMatch("<b>|<a>", "a", true);
        testTokenPatternMatch("<b>|<a>", "b", true);
        */
        testTokenPatternMatch("(<b>|<a>)+", "abb", true);
        //testTokenPatternMatch("(<b>|<a>)+", "", false);
    }

}
