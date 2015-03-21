package com.fulmicoton.corpus;

import com.fulmicoton.semantic.Token;

import java.util.ArrayList;
import java.util.List;

public class Fragment {

    public List<Token> tokens;
    public int focusId;

    public Fragment() {
        this.tokens = new ArrayList<Token>();
    }

    public Fragment(List<Token> tokens, int focusId) {
        this();
        this.tokens.addAll(tokens);
        this.focusId = focusId;
    }

}
