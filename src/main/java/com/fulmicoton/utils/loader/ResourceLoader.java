package com.fulmicoton.utils.loader;

import java.io.InputStream;

public class ResourceLoader implements Loader {

    private final Class<?> klass;

    public ResourceLoader(Class<?> klass) {
        this.klass = klass;
    }

    public static ResourceLoader fromClass(Class<?> klass) {
        return new ResourceLoader(klass);
    }

    @Override
    public InputStream open(String path) {
        return klass.getResourceAsStream(path);
    }
}
