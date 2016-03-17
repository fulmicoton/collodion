package com.fulmicoton.collodion.processors.tokenpattern;

import com.fulmicoton.collodion.processors.AnnotationKey;
import com.fulmicoton.collodion.processors.tokenpattern.ast.AST;
import com.fulmicoton.collodion.processors.tokenpattern.ast.CapturingGroupAST;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.Machine;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.MultiMatcher;
import com.fulmicoton.collodion.processors.tokenpattern.nfa.TokenPatternMatchResult;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

import static com.fulmicoton.collodion.processors.tokenpattern.TokenPatternTest.makeTokenList;

public class MultiPatternTest {


    private static int addPattern(final MachineBuilder machineBuilder,
                            final String ptn) throws Exception {
        final AST ast = AST.compile(ptn);
        final CapturingGroupAST capturingGroupAST = new CapturingGroupAST(ast, AnnotationKey.of("TEST"));
        return machineBuilder.addPattern(capturingGroupAST);
    }

    @Test
    public void testMultiPattern() throws Exception {
        final MachineBuilder machineBuilder = new MachineBuilder();
        Assert.assertEquals(0, addPattern(machineBuilder, "[a](?<toto1>[b][c])[d]"));
        Assert.assertEquals(1, addPattern(machineBuilder, "[a][b](?<toto2>[c][d])"));
        Assert.assertEquals(2, addPattern(machineBuilder, "(?<toto3>[a][b])(?<toto4>[c][d])[e]"));
        final Machine machine = machineBuilder.buildForMatch();
        final List<SemToken> semToken = makeTokenList("abcd");
        final MultiMatcher multiMatcher = machine.match(semToken.iterator());
        {
            final TokenPatternMatchResult matchResult = multiMatcher.get(0);
            Assert.assertTrue(matchResult.matches());
            Assert.assertEquals(1, matchResult.groupCount());
            Assert.assertEquals(1, matchResult.start(1));
            Assert.assertEquals(3, matchResult.end(1));
        }
        {
            final TokenPatternMatchResult matchResult = multiMatcher.get(1);
            Assert.assertTrue(matchResult.matches());
            Assert.assertEquals(1, matchResult.groupCount());
            Assert.assertEquals(2, matchResult.start(1));
            Assert.assertEquals(4, matchResult.end(1));
        }
        {
            final TokenPatternMatchResult matchResult = multiMatcher.get(2);
            Assert.assertFalse(matchResult.matches());
            Assert.assertEquals(2, matchResult.groupCount());
            Assert.assertEquals(-1, matchResult.start(1));
            Assert.assertEquals(-1, matchResult.end(1));
        }
    }
}
