package com.fulmicoton.collodion.tokenizer;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

public class GoAgainReaderTest {

    @Test
    public void testSimple() throws IOException {
        final String msg = "coucou aaaa";
        final StringReader simpleReader = new StringReader(msg);
        final GoAgainReader goAgainReader = new GoAgainReader(new StringReader(msg));
        final char[] testBufferA = new char[10000];
        final char[] testBufferB = new char[10000];
        final char[] testBufferC = new char[10000];
        {
            int lengthA = simpleReader.read(testBufferA, 0, 1);
            int lengthB = goAgainReader.read(testBufferB, 0, 1);
            Assert.assertEquals(lengthA, lengthB);
            Assert.assertArrayEquals(testBufferA, testBufferB);
        }
        {
            goAgainReader.reread(testBufferC, 0, 1);
            Assert.assertArrayEquals(testBufferA, testBufferC);
        }
        {
            int lengthA = simpleReader.read(testBufferA, 1, 1);
            int lengthB = goAgainReader.read(testBufferB, 1, 1);
            Assert.assertEquals(lengthA, lengthB);
            Assert.assertArrayEquals(testBufferA, testBufferB);
        }
        {
            goAgainReader.reread(testBufferC, 0, 2);
            Assert.assertArrayEquals(testBufferA, testBufferC);
            goAgainReader.advance(1);
            goAgainReader.reread(testBufferC, 0, 1);
            Assert.assertEquals(testBufferC[0], testBufferC[1]);
            goAgainReader.advance(1);
        }
        {
            int lengthA = simpleReader.read(testBufferA, 0, 2);
            int lengthB = goAgainReader.read(testBufferB, 0, 2);
            Assert.assertEquals(lengthA, lengthB);
            Assert.assertArrayEquals(testBufferA, testBufferB);
            goAgainReader.reread(testBufferC, 0, 2);
            Assert.assertArrayEquals(testBufferA, testBufferC);
        }
        {
            goAgainReader.reread(testBufferC, 0, 2);
            Assert.assertArrayEquals(testBufferA, testBufferC);
        }
    }

}
