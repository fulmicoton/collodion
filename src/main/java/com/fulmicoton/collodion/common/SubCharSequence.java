package com.fulmicoton.collodion.common;

public class SubCharSequence implements CharSequence {

    final CharSequence underlying;
    final int start;
    final int length;

    public SubCharSequence(CharSequence underlying, int start, int end) {
        this.underlying = underlying;
        this.start = start;
        this.length = end - start;
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    public char charAt(int index) {
        return this.underlying.charAt(index - start);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new SubCharSequence(this.underlying, start + this.start, end + this.start);
    }
}
