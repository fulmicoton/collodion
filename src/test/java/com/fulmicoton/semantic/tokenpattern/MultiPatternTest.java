package com.fulmicoton.semantic.tokenpattern;

import com.fulmicoton.semantic.tokenpattern.nfa.Machine;
import com.fulmicoton.semantic.tokenpattern.nfa.MachineBuilder;
import com.fulmicoton.semantic.tokenpattern.nfa.Matcher;
import com.fulmicoton.semantic.tokenpattern.nfa.MultiMatcher;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

import static com.fulmicoton.semantic.tokenpattern.TokenPatternTest.makeTokenList;

public class MultiPatternTest {

    @Test
    public void testMultiPattern() {
        MachineBuilder machineBuilder = new MachineBuilder();
        Assert.assertEquals(machineBuilder.add("[a]([b][c])[d]"), 0);
        Assert.assertEquals(machineBuilder.add("[a][b]([c][d])"), 1);
        Assert.assertEquals(machineBuilder.add("([a][b])([c][d])[e]"), 2);
        final Machine machine = machineBuilder.build();
        List<SemToken> semToken = makeTokenList("abcd");
        final MultiMatcher multiMatcher = machine.match(semToken.iterator());
        {
            final Matcher matcher = multiMatcher.get(0);
            Assert.assertTrue(matcher.matches());
            Assert.assertEquals(matcher.groupCount(), 1);
            Assert.assertEquals(matcher.start(1), 1);
            Assert.assertEquals(matcher.end(1), 3);
        }
        {
            final Matcher matcher = multiMatcher.get(1);
            Assert.assertTrue(matcher.matches());
            Assert.assertEquals(matcher.groupCount(), 1);
            Assert.assertEquals(matcher.start(1), 2);
            Assert.assertEquals(matcher.end(1), 4);
        }
        {
            final Matcher matcher = multiMatcher.get(2);
            Assert.assertFalse(matcher.matches());
            Assert.assertEquals(matcher.groupCount(), 2);
            Assert.assertEquals(matcher.start(1), -1);
            Assert.assertEquals(matcher.end(1), -1);
        }
    }
}
