package com.fulmicoton.collodion.processors.sequencematcher;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.junit.Assert;
import org.junit.Test;

public class AhoCorasickTest {


    @Test
    public void testAhoCorasick() {
        final AhoCorasick ahoCorasick = new AhoCorasick();
        ahoCorasick.insert(Ints.toArray(ImmutableList.of(1, 2, 3)), 1);
        ahoCorasick.insert(Ints.toArray(ImmutableList.of(1, 2, 4)), 2);
        ahoCorasick.insert(Ints.toArray(ImmutableList.of(1, 2, 3, 5)), 3);
        ahoCorasick.insert(Ints.toArray(ImmutableList.of(2, 3, 7)), 4);
        ahoCorasick.insert(Ints.toArray(ImmutableList.of(2, 3)), 6);


        AhoCorasick.Node node = ahoCorasick.getRoot();
        ahoCorasick.goTo(node, 5);
        Assert.assertEquals("", node.terminals, ImmutableList.of());
        ahoCorasick.goTo(node, 1);
        Assert.assertEquals("", node.terminals, ImmutableList.of());
        ahoCorasick.goTo(node, 2);
        Assert.assertEquals("", node.terminals, ImmutableList.of());
        ahoCorasick.goTo(node, 3);
        Assert.assertEquals("", node.terminals, ImmutableList.of(1, 6));
        ahoCorasick.goTo(node, 7);
        Assert.assertEquals("", node.terminals, ImmutableList.of(4));
        ahoCorasick.goTo(node, 2);
        Assert.assertEquals("", node.terminals, ImmutableList.of());
        ahoCorasick.goTo(node, 3);
        Assert.assertEquals("", node.terminals, ImmutableList.of(6));
        ahoCorasick.goTo(node, 4);
        Assert.assertEquals("", node.terminals, ImmutableList.of(2));
        ahoCorasick.goTo(node, 5);
        Assert.assertEquals("", node.terminals, ImmutableList.of());

    }

}
