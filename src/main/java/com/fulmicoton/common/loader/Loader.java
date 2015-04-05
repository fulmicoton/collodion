package com.fulmicoton.common.loader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class Loader {

    /**
     * Given an address returns an input stream
     * @param path - it can be filesystem path,
     *               a relative path,
     *               a resource path,
     *               depending on the implementation.
     * @return null if the address does not lead anywhere
     *              else an input stream
     */
    public abstract InputStream open(final String path);

    public BufferedReader read(final String path) {
        final InputStream input = this.open(path);
        if (input == null) {
            return null;
        }
        final Reader reader = new InputStreamReader(input);
        return new BufferedReader(reader);
    }

}
