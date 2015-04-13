package com.fulmicoton.processors.tokenpattern;

import com.fulmicoton.processors.tokenpattern.nfa.Machine;
import com.fulmicoton.processors.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.processors.tokenpattern.nfa.TokenPatternMatchResult;
import com.fulmicoton.processors.tokenpattern.nfa.MultiMatcher;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

import static com.fulmicoton.processors.tokenpattern.TokenPatternTest.makeTokenList;

public class MultiPatternTest {

    @Test
    public void testMultiPattern() {
        MachineBuilder machineBuilder = new MachineBuilder();
        Assert.assertEquals(machineBuilder.add("[a]([b][c])[d]"), 0);
        Assert.assertEquals(machineBuilder.add("[a][b]([c][d])"), 1);
        Assert.assertEquals(machineBuilder.add("([a][b])([c][d])[e]"), 2);
        final Machine machine = machineBuilder.buildForMatch();
        List<SemToken> semToken = makeTokenList("abcd");
        final MultiMatcher multiMatcher = machine.match(semToken.iterator());
        {
            final TokenPatternMatchResult matchResult = multiMatcher.get(0);
            Assert.assertTrue(matchResult.matches());
            Assert.assertEquals(matchResult.groupCount(), 1);
            Assert.assertEquals(matchResult.start(1), 1);
            Assert.assertEquals(matchResult.end(1), 3);
        }
        {
            final TokenPatternMatchResult matchResult = multiMatcher.get(1);
            Assert.assertTrue(matchResult.matches());
            Assert.assertEquals(matchResult.groupCount(), 1);
            Assert.assertEquals(matchResult.start(1), 2);
            Assert.assertEquals(matchResult.end(1), 4);
        }
        {
            final TokenPatternMatchResult matchResult = multiMatcher.get(2);
            Assert.assertFalse(matchResult.matches());
            Assert.assertEquals(matchResult.groupCount(), 2);
            Assert.assertEquals(matchResult.start(1), -1);
            Assert.assertEquals(matchResult.end(1), -1);
        }
    }
}
