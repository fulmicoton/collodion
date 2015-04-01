package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.Annotation;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TokenPatternTest {


    public void testTokenPatternMatch(String ptn, String testString, boolean expected) {
        final String[] tokens = testString.length() > 0? testString.split(" "): new String[0];
        final List<SemToken> tokenList = new ArrayList<>();
        for (String token: tokens) {
            tokenList.add(new SemToken(Annotation.of(token)));
        }
        final TokenPattern tokenPattern = TokenPattern.compile(ptn);
        Assert.assertEquals(tokenPattern.match(tokenList.iterator()).matches(), expected);
    }

    @Test
    public void testPatternNFA() {
        testTokenPatternMatch("<a>*", "a a a", true);
        testTokenPatternMatch("<a>+", "a a b", false);
        testTokenPatternMatch("((<a>|<b>)|.)+", "a a b", true);
        testTokenPatternMatch("<a>+<b>+<a>", "a a a", false);
        testTokenPatternMatch("<a>+<b>*<a>", "a a a", true);
        testTokenPatternMatch("<a>?<a><b>", "a a b", true);
        testTokenPatternMatch("<a>?<a><b>", "a b", true);
        testTokenPatternMatch("<a><b>", "a b", true);
        testTokenPatternMatch("<a>{2,3}", "a a", true);
        testTokenPatternMatch("<b><a>{2,3}", "b a a", true);
        testTokenPatternMatch("<b><a>{2}", "b a a", true);
        testTokenPatternMatch("<b><a>{2}", "b a a a", false);
        testTokenPatternMatch("<b><a>", "a a a a", false);
        testTokenPatternMatch("<b><a>{2,3}", "b a", false);
        testTokenPatternMatch("<b><a>{2,3}", "b a a a", true);
        testTokenPatternMatch("<b><a>{2,3}", "b a a a a", false);
        testTokenPatternMatch("<b>|<a>", "a", true);
        testTokenPatternMatch("<b>|<a>", "b", true);
        testTokenPatternMatch("(<b>|<a>)+", "a b b", true);
        testTokenPatternMatch("(<b>|<a>)+", "", false);
        testTokenPatternMatch(".+", "ab", true);
        testTokenPatternMatch("<a>+", "", false);
        testTokenPatternMatch(".+", "", false);
        testTokenPatternMatch(".+", "a", true);
    }
}
