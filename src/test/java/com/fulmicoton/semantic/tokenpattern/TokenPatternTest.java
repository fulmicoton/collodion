package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.nfa.Matcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TokenPatternTest {

    private static java.util.regex.Matcher translateToJavaMatch(final String ptn, final String testString) {
        final Pattern javaPtn = Pattern.compile(ptn.replace("[", "").replace("]", ""));
        return javaPtn.matcher(testString);
    }

    public static List<SemToken> makeTokenList(final String testString) {
        final List<SemToken> tokenList = new ArrayList<>();
        for (char token: testString.toCharArray()) {
            tokenList.add(new SemToken(Annotation.of(String.valueOf(token))));
        }
        return tokenList;
    }

    public void testTokenPatternMatch(String ptn, String testString) {
        final List tokenList = makeTokenList(testString);
        final java.util.regex.Matcher javaMatch = translateToJavaMatch(ptn, testString);
        final TokenPattern tokenPattern = TokenPattern.compile(ptn);
        final Matcher match = tokenPattern.match(tokenList.iterator());
        Assert.assertEquals(match.matches(), javaMatch.matches());
        Assert.assertEquals(javaMatch.groupCount(), match.groupCount());
        for (int groupId=0; groupId<match.groupCount(); groupId++) {
            int startJava, endJava;
            try {
                startJava = javaMatch.start(groupId);
            }
            catch (IllegalStateException e) {
                startJava = -1;
            }
            try {
                endJava = javaMatch.end(groupId);
            }
            catch (IllegalStateException e) {
                endJava = -1;
            }
            Assert.assertEquals(startJava, match.start(groupId));
            Assert.assertEquals(endJava, match.end(groupId));
        }
    }

    @Test
    public void testPatternNFA() {
        testTokenPatternMatch("([a])", "a");
        testTokenPatternMatch("([a])([b])", "ab");
        testTokenPatternMatch("([a])*", "aaa");
        testTokenPatternMatch("([a]|[b])*", "aba");
        testTokenPatternMatch("([a])+", "aaa");
        testTokenPatternMatch("([a])[b]", "ab");
        testTokenPatternMatch("(?:[a])[b]", "ab");
        testTokenPatternMatch("[a]*", "aaa");
        testTokenPatternMatch("[a]+", "aab");
        testTokenPatternMatch("[a]+[b]+[a]", "aaa");
        testTokenPatternMatch("[a]+[b]*[a]", "aaa");
        testTokenPatternMatch("[a]?[a][b]", "aab");
        testTokenPatternMatch("[a]?[a][b]", "ab");
        testTokenPatternMatch("[a][b]", "ab");
        testTokenPatternMatch("[a]{2,3}", "aa");
        testTokenPatternMatch("[b][a]{2,3}", "baa");
        testTokenPatternMatch("[b][a]{2}", "baa");
        testTokenPatternMatch("[b][a]{2}", "baaa");
        testTokenPatternMatch("[b][a]", "aaaa");
        testTokenPatternMatch("[b][a]{2,3}", "ba");
        testTokenPatternMatch("[b][a]{2,3}", "baaa");
        testTokenPatternMatch("[b][a]{2,3}", "baaaa");
        testTokenPatternMatch("[b]|[a]", "a");
        testTokenPatternMatch("[b]|[a]", "b");
        testTokenPatternMatch("([b]|[a])+", "abb");
        testTokenPatternMatch("[b]|([a])", "b");
        testTokenPatternMatch("[b]|([a])", "a");
        testTokenPatternMatch("([b]|[a])+", "");
        testTokenPatternMatch(".+", "ab");
        testTokenPatternMatch("[a]+", "");
        testTokenPatternMatch(".+", "");
        testTokenPatternMatch(".+", "a");
        testTokenPatternMatch("((?:[a]|[b])*)", "aabaab");
    }


    @Test
    public void testPatternNamedGroup() {
        final TokenPattern tokenPattern = TokenPattern.compile("[a](?<patternone>[a][b])[c](?<patterntwo>[d])[e]");
        final List tokenList = makeTokenList("aabcde");
        final Matcher matcher = tokenPattern.match(tokenList.iterator());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals(matcher.start("patternone"), 1);
        Assert.assertEquals(matcher.end("patternone"), 3);
        Assert.assertEquals(matcher.start("patterntwo"), 4);
        Assert.assertEquals(matcher.end("patterntwo"), 5);
        final String errorMsg = "Group named patternthree is unknown. Available groupNames are patterntwo, patternone.";
        try {
            matcher.start("patternthree");
            Assert.fail("should have thrown");
        }
        catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), errorMsg);
        }
        try {
            matcher.end("patternthree");
            Assert.fail("should have thrown");
        }
        catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), errorMsg);
        }
}
}
