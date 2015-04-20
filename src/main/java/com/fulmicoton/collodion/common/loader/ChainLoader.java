package com.fulmicoton.collodion.common.loader;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ChainLoader extends Loader {

    private final Iterable<Loader> loaderChain;

    public ChainLoader(Iterable<Loader> loaderChain) {
        this.loaderChain = loaderChain;
    }

    @Override
    public InputStream open(String path) throws IOException {
        for (final Loader loader: loaderChain) {
            final InputStream inputStream = loader.open(path);
            if (inputStream != null) {
                return inputStream;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        final Iterable<String> parts = Iterables.transform(this.loaderChain, new Function<Loader, String>() {

            @Nullable
            @Override
            public String apply(Loader loader) {
                return loader.toString();
            }
        });
        final String subLoaderString = Joiner.on(",").join(parts);
        return "ChainLoader(" + subLoaderString + ")";
    }

    public static ChainLoader of(Loader... loaders) {
        return new ChainLoader(Arrays.asList(loaders));
    }
}
