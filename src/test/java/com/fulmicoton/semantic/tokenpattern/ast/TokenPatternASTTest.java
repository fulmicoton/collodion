package com.fulmicoton.semantic.tokenpattern.ast;

import com.fulmicoton.multiregexp.Token;
import com.fulmicoton.semantic.tokenpattern.GroupAllocator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class TokenPatternASTTest {

    public static void testTokenizer(String ptn, RegexPatternToken... expectedRegexPatternTokenTypes) {
        final Iterator<Token<RegexPatternToken>> tokenIt = AST.LEXER.scan(ptn).iterator();
        for (RegexPatternToken regexPatternTokenType : expectedRegexPatternTokenTypes) {
            final Token<RegexPatternToken> token = tokenIt.next();
            Assert.assertEquals(regexPatternTokenType, token.type);
        }
        Assert.assertFalse(tokenIt.hasNext());
    }


    public static void testParser(String ptn, String expected) {
        final AST tokenPattern = AST.compile(ptn);
        final GroupAllocator groupAllocator = new GroupAllocator();
        tokenPattern.allocateGroups(groupAllocator);
        System.out.println(tokenPattern.toString());
        Assert.assertEquals(expected, tokenPattern.toDebugString());
    }

    @Test
    public void testTokenization() {
        testTokenizer(".*", RegexPatternToken.DOT, RegexPatternToken.STAR);
        testTokenizer("[abc]{1,2}", RegexPatternToken.ANNOTATION, RegexPatternToken.COUNT);
        testTokenizer("[abc]", RegexPatternToken.ANNOTATION);
        testTokenizer("([abc]?)[bcd]", RegexPatternToken.OPEN_PARENTHESIS, RegexPatternToken.ANNOTATION, RegexPatternToken.QUESTION_MARK, RegexPatternToken.CLOSE_PARENTHESIS, RegexPatternToken.ANNOTATION);
        testTokenizer("[abc]|[bcd]", RegexPatternToken.ANNOTATION, RegexPatternToken.OR, RegexPatternToken.ANNOTATION);
    }

    public boolean isValidExpression(String ptn) {
        try {
            AST.compile(ptn);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Test
    public void testASTCompileThrows() {
        Assert.assertTrue(isValidExpression("([a])"));
        Assert.assertFalse(isValidExpression("([a]"));
        Assert.assertFalse(isValidExpression(""));
    }

    @Test
    public void testASTCompile() {
        testParser("(([a]|[b])|.)+", "(0:(1:[a]|[b])|.)(0:(1:[a]|[b])|.)*");
        testParser("(?:[a]|[b])*", "(?:[a]|[b])*");
        testParser("(?:[a][b])*", "(?:[a][b])*");
        testParser("(.)", "(0:.)");
        testParser(".*", ".*");
        testParser("..", "..");
        testParser("[abc]{4,6}", "[abc]{4,6}");
        testParser("[abc]", "[abc]");
        testParser("(?:.)", ".");
        testParser("([abc]?)[bcd]", "(0:[abc]{0,1})[bcd]");
        testParser("[abc][bcd]+", "[abc][bcd][bcd]*");
        testParser("([abc][bcd])+", "(0:[abc][bcd])(0:[abc][bcd])*");
        testParser("([b]|[a])+", "(0:[b]|[a])(0:[b]|[a])*");
        testParser(".+", "..*");
        testParser("(?<aaa>[bbb])", "(?<aaa:0>[bbb])");
    }



}
