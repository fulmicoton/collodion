package com.fulmicoton.collodion.tokenizer;

import java.io.IOException;
import java.io.Reader;

public class GoAgainReader extends Reader {

    /**
     * Wraps a reader in a way that makes it possible to
     * read thing twice.
     *
     * It is used by the SolilessTokenizer.
     */

    char[] staybackBuffer = new char[1000];
    int start = 0;
    int length = 0;
    final Reader reader;
    boolean finished = false;

    public GoAgainReader(final Reader reader) {
        this.reader = reader;
    }

    public void reread(char[] buff, int off, int len) {
        assert len <= length;

        int maxSimpleReread = this.staybackBuffer.length - this.start;
        if (maxSimpleReread > len) {
            System.arraycopy(this.staybackBuffer, this.start, buff, off, len);
        }
        else {
            System.arraycopy(this.staybackBuffer, this.start, buff, off, maxSimpleReread);
            System.arraycopy(this.staybackBuffer, 0, buff, off + maxSimpleReread, len - maxSimpleReread);
        }
    }

    public void advance(int len) {
        this.length -= len;
        this.start += len;
        if (this.start >= staybackBuffer.length) {
            this.start -= staybackBuffer.length;
        }
    }

    private void resize(int newSize) {
        char[] newBuffer = new char[newSize];
        this.reread(newBuffer, 0, this.length);
        this.start = 0;
        this.staybackBuffer = newBuffer;
    }

    private int cur() {
        int cur = this.start + this.length;
        if (cur >= this.staybackBuffer.length) {
            return cur - this.staybackBuffer.length;
        }
        else {
            return cur;
        }
    }

    private int simpleRead(char[] cbuf, int off, int len) throws IOException {
        assert len <= cbuf.length - off;
        assert this.staybackBuffer.length >= len;
        final int newChunkStart = cur();
        final int readLength = this.reader.read(this.staybackBuffer, newChunkStart, len);
        if (readLength < len) {
            this.finished = true;
        }
        if (readLength == -1) {
            return -1;
        }
        this.length += readLength;
        System.arraycopy(this.staybackBuffer, newChunkStart, cbuf, off, readLength);
        return readLength;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (this.finished) {
            return -1;
        }
        if (this.length + len > this.staybackBuffer.length) {
            this.resize(len + this.length);
        }
        int cur = this.cur();
        int maxSimpleReadLength = this.staybackBuffer.length - cur;
        if (len > maxSimpleReadLength) {
            final int firstLength = this.simpleRead(cbuf, off, maxSimpleReadLength);
            if (firstLength < 0) {
                return -1;
            }
            if (firstLength < maxSimpleReadLength) {
                return firstLength;
            }
            else {
                final int secondLength = this.simpleRead(cbuf, off + firstLength, len - firstLength);
                if (secondLength < 0) {
                    return firstLength;
                }
                else {
                    return firstLength + secondLength;
                }
            }
        }
        else {
            return this.simpleRead(cbuf, off, len);
        }
    }

    @Override
    public void close() throws IOException {

    }

}
