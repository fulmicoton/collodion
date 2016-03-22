package com.fulmicoton.collodion.processors.sequencematcher;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import gnu.trove.set.TIntSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class AhoCorasickTest {


    public int[] toSortedArray(final TIntSet ints) {
        final int[] sortedArr = ints.toArray();
        Arrays.sort(sortedArr);
        return sortedArr;
    }

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
        Assert.assertArrayEquals("", toSortedArray(node.terminals), new int[]{});
        node = ahoCorasick.goTo(node, 1);
        Assert.assertArrayEquals("", toSortedArray(node.terminals), new int[]{});
        node = ahoCorasick.goTo(node, 2);
        Assert.assertArrayEquals("", toSortedArray(node.terminals), new int[]{});
        node = ahoCorasick.goTo(node, 3);
        Assert.assertArrayEquals("", toSortedArray(node.terminals), new int[]{1, 6});
        node = ahoCorasick.goTo(node, 7);
        Assert.assertArrayEquals("", toSortedArray(node.terminals), new int[]{4});
        node = ahoCorasick.goTo(node, 2);
        Assert.assertArrayEquals("", toSortedArray(node.terminals), new int[]{});
        node = ahoCorasick.goTo(node, 3);
        Assert.assertArrayEquals("", toSortedArray(node.terminals), new int[]{6});
        node = ahoCorasick.goTo(node, 4);
        Assert.assertArrayEquals("", toSortedArray(node.terminals), new int[]{});
        node = ahoCorasick.goTo(node, 5);
        Assert.assertArrayEquals("", toSortedArray(node.terminals), new int[]{});

    }

}
