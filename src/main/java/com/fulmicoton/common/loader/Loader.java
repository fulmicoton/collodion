package com.fulmicoton.common.loader;

import com.fulmicoton.common.JSON;

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


    public <T> T readObject(final String path, final Class<T> klass) {
        final Reader content = this.read(path);
        if (path.endsWith(".yaml")) {
            return JSON.fromYAML(content, klass);
        }
        else {
            return JSON.GSON.fromJson(content, klass);
        }
    }

}
