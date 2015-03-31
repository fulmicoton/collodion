package com.fulmicoton.semantic.tokenpattern.ast;


class CountParam {

    final int minCount;
    final int maxCount;

    CountParam(int minCount, int maxCount) throws ParsingError {
        if (maxCount < minCount) {
            throw new ParsingError(String.format("Max boundary cannot be below the min boundary (%i < %i)", maxCount, minCount));
        }
        this.minCount = minCount;
        this.maxCount = maxCount;
    }
}
