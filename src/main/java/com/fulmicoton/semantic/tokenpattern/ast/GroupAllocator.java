package com.fulmicoton.semantic.tokenpattern.ast;

public class GroupAllocator {

    int nbGroup = 0;

    public int allocateGroup() {
        return nbGroup++;
    }

    public int getNbGroups() {
        return nbGroup;
    }

}
