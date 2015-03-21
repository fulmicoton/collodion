package com.fulmicoton.common.loader;


import java.io.InputStream;
import java.util.Arrays;

public class ChainLoader implements Loader {

    private final Iterable<Loader> loaderChain;

    public ChainLoader(Iterable<Loader> loaderChain) {
        this.loaderChain = loaderChain;
    }

    @Override
    public InputStream open(String path) {
        for (final Loader loader: loaderChain) {
            final InputStream inputStream = loader.open(path);
            if (inputStream != null) {
                return inputStream;
            }
        }
        return null;
    }

    public static ChainLoader of(Loader... loaders) {
        return new ChainLoader(Arrays.asList(loaders));
    }
}
