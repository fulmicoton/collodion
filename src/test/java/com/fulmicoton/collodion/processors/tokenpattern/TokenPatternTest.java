package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.tokenpattern.ast.AST;
import com.fulmicoton.collodion.processors.tokenpattern.ast.CapturingGroupAST;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.Machine;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.TokenPatternMatchResult;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.TokenPatternMatcher;
import com.fulmicoton.collodion.common.AnnotationAttributeImpl;
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
        for (final char token: testString.toCharArray()) {
            final AnnotationAttributeImpl vocabularyAttribute = new AnnotationAttributeImpl();
            vocabularyAttribute.add(AnnotationKey.of(String.valueOf(token)));
            tokenList.add(new SemToken(vocabularyAttribute));
        }
        return tokenList;
    }

    public void testTokenPatternSearch(final String ptn,
                                       final String testString,
                                       final int... expectedPositions) throws Exception {
        final List<SemToken> tokenList = makeTokenList(testString);
        final MachineBuilder machineBuilder = new MachineBuilder();
        final AST ptnAST = AST.compile(ptn);
        final CapturingGroupAST capturingGroupAST = new CapturingGroupAST(ptnAST, AnnotationKey.of("ROOT"));
        machineBuilder.addPattern(capturingGroupAST);
        final Machine machine = machineBuilder.buildForSearch();

        final TokenPatternMatcher runner = machine.matcher();
        final Iterator<SemToken> tokenIt = tokenList.iterator();
        final List<Integer> actualPositions = new ArrayList<>();
        int cursor = 0;
        while (tokenIt.hasNext()) {
            final SemToken semToken = tokenIt.next();
            cursor+=1;
            final TokenPatternMatchResult matchResult = runner.search(semToken);
            if (matchResult != null) {
                actualPositions.add(matchResult.start(0));
                actualPositions.add(matchResult.end(0));
                runner.reset(cursor);
            }
        }
        final int[] actualPositionsArr = Ints.toArray(actualPositions);
        Assert.assertArrayEquals(actualPositionsArr, expectedPositions);
    }

    public void testTokenPatternMatch(final String ptn, final String testString) throws Exception {
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
            catch (final IllegalStateException e) {
                startJava = -1;
            }
            try {
                endJava = javaMatch.end(groupId);
            }
            catch (final IllegalStateException e) {
                endJava = -1;
            }
            Assert.assertEquals(startJava, match.start(groupId));
            Assert.assertEquals(endJava, match.end(groupId));
        }
    }

    @Test
    public void testPatternSearch() throws Exception {
        // TODO we unfortunately don't know how to do greedy search today.
        testTokenPatternSearch("[a]+", "aa", 0, 1, 1, 2);
        testTokenPatternSearch("[a]+", "aba", 0, 1, 2, 3);
    }

    @Test
    public void testPatternNFA() throws Exception {
        testTokenPatternMatch("(?<grp1>[a])", "a");
        testTokenPatternMatch("(?<grp1>[a])(?<grp2>[b])", "ab");
        testTokenPatternMatch("(?<grp1>[a])*", "aaa");
        testTokenPatternMatch("(?<grp1>[a]|[b])*", "aba");
        testTokenPatternMatch("(?<grp1>[a])+", "aaa");
        testTokenPatternMatch("(?<grp1>[a])[b]", "ab");
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
        testTokenPatternMatch("(?:[b]|[a])+", "abb");
        testTokenPatternMatch("[b]|(?:[a])", "b");
        testTokenPatternMatch("[b]|(?:[a])", "a");
        testTokenPatternMatch("(?<grp1>[b]|[a])+", "");
        testTokenPatternMatch(".+", "ab");
        testTokenPatternMatch("[a]+", "");
        testTokenPatternMatch(".+", "");
        testTokenPatternMatch(".+", "a");
        testTokenPatternMatch("(?<grp3>(?:[a]|[b])*)", "aabaab");
    }


    @Test
    public void testPatternNamedGroup() throws Exception {
        final TokenPattern tokenPattern = TokenPattern.compile("[a](?<patternone>[a][b])[c](?<patterntwo>[d])[e]");
        final List<SemToken> tokenList = makeTokenList("aabcde");
        final TokenPatternMatchResult matchResult = tokenPattern.match(tokenList.iterator());
        Assert.assertTrue(matchResult.matches());
        Assert.assertEquals(matchResult.start("patternone"), 1);
        Assert.assertEquals(matchResult.end("patternone"), 3);
        Assert.assertEquals(matchResult.start("patterntwo"), 4);
        Assert.assertEquals(matchResult.end("patterntwo"), 5);
        final String errorMsg = "Group named patternthree is unknown. Available groupNames are patterntwo, TEST, patternone.";
        try {
            matchResult.start("patternthree");
            Assert.fail("should have thrown");
        }
        catch (final IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), errorMsg);
        }
        try {
            matchResult.end("patternthree");
            Assert.fail("should have thrown");
        }
        catch (final IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), errorMsg);
        }
}
}
