package com.fulmicoton.processors.tokenpattern.parsing;


import com.fulmicoton.multiregexp.Token;

import java.util.List;

public interface Emitter<T, V> {

    public V emit(final List<V> childrenEmission, final List<Token<T>> tokens);

}
