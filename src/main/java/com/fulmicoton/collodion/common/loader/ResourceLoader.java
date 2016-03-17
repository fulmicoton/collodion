package com.fulmicoton.collodion.common.loader;

import java.io.InputStream;

public abstract class ResourceLoader extends Loader {

    static class FromClassResourceLoader extends ResourceLoader {
        private final Class<?> klass;
        FromClassResourceLoader(final Class<?> klass) {
            this.klass = klass;
        }

        @Override
        public InputStream open(String path) {
            return this.klass.getResourceAsStream(path);
        }
    }

    static class FromClassLoaderResourceLoader extends ResourceLoader {
        private final ClassLoader classLoader;
        FromClassLoaderResourceLoader(final ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        public InputStream open(String path) {
            return this.classLoader.getResourceAsStream(path);
        }
    }

    public static ResourceLoader SYSTEM_CLASSLOADER = new FromClassLoaderResourceLoader(ClassLoader.getSystemClassLoader());

    public static ResourceLoader fromClass(Class<?> klass) {
        return new FromClassResourceLoader(klass);
    }
}
