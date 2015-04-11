package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.tokenpattern.nfa.TokenPatternMatchResult;
import com.fulmicoton.semantic.tokenpattern.nfa.TokenPatternMatcher;
import com.fulmicoton.semantic.vocabularymatcher.VocabularyAttributeImpl;
import com.google.common.primitives.Ints;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
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
            VocabularyAttributeImpl vocabularyAttribute = new VocabularyAttributeImpl();
            vocabularyAttribute.add(Annotation.of(String.valueOf(token)));
            tokenList.add(new SemToken(vocabularyAttribute));
        }
        return tokenList;
    }

    public void testTokenPatternSearch(String ptn, String testString, int... expectedPositions) {
        final List<SemToken> tokenList = makeTokenList(testString);
        final TokenPattern tokenPattern = TokenPattern.compile(ptn);
        TokenPatternMatcher runner = tokenPattern.matcher();
        final Iterator<SemToken> tokenIt = tokenList.iterator();
        final List<Integer> actualPositions = new ArrayList<>();
        while (tokenIt.hasNext()) {
            final SemToken semToken = tokenIt.next();
            final TokenPatternMatchResult matchResult = runner.search(semToken);
            if (matchResult != null) {
                actualPositions.add(matchResult.start(0));
                actualPositions.add(matchResult.end(0));
            }
        }
        int[] actualPositionsArr = Ints.toArray(actualPositions);
        Assert.assertArrayEquals(actualPositionsArr, expectedPositions);
    }

    public void testTokenPatternMatch(String ptn, String testString) {
        final List<SemToken> tokenList = makeTokenList(testString);
        final java.util.regex.Matcher javaMatch = translateToJavaMatch(ptn, testString);
        final TokenPattern tokenPattern = TokenPattern.compile(ptn);
        final TokenPatternMatchResult match = tokenPattern.match(tokenList.iterator());
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
    public void testPatternSearch() {
        // TODO we unfortunately don't know how to do greedy search today.
        testTokenPatternSearch("[a]+", "aa", 0, 1, 1, 2);
        testTokenPatternSearch("[a]+", "aba", 0, 1, 2, 3);
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
        final List<SemToken> tokenList = makeTokenList("aabcde");
        final TokenPatternMatchResult matchResult = tokenPattern.match(tokenList.iterator());
        Assert.assertTrue(matchResult.matches());
        Assert.assertEquals(matchResult.start("patternone"), 1);
        Assert.assertEquals(matchResult.end("patternone"), 3);
        Assert.assertEquals(matchResult.start("patterntwo"), 4);
        Assert.assertEquals(matchResult.end("patterntwo"), 5);
        final String errorMsg = "Group named patternthree is unknown. Available groupNames are patterntwo, patternone.";
        try {
            matchResult.start("patternthree");
            Assert.fail("should have thrown");
        }
        catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), errorMsg);
        }
        try {
            matchResult.end("patternthree");
            Assert.fail("should have thrown");
        }
        catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), errorMsg);
        }
}
}
