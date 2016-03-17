package com.fulmicoton.collodion.common.loader;

import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class Loader {


    public static final Loader DEFAULT_LOADER = ChainLoader.of(
            DirectoryLoader.forRoot(new File("/")),
            DirectoryLoader.forRoot(new File(".")),
            ResourceLoader.fromClass(CollodionAnalyzer.class),
            ResourceLoader.SYSTEM_CLASSLOADER
    );

    /**
     * Given an address returns an input stream
     * @param path - it can be filesystem path,
     *               a relative path,
     *               a resource path,
     *               depending on the implementation.
     * @return null if the address does not lead anywhere
     *              else an input stream
     */
    public abstract InputStream open(final String path) throws IOException;

    public BufferedReader read(final String path) throws IOException {
        final InputStream input = this.open(path);
        if (input == null) {
            throw new FileNotFoundException("Path " + path + ". with resource loader : " + this.toString());
        }
        final Reader reader = new InputStreamReader(input);
        return new BufferedReader(reader);
    }


    public <T> T readObject(final String path, final Class<T> klass) throws IOException {
        final Reader content = this.read(path);
        if (content == null) {
            throw new FileNotFoundException("Resource with path " + path + "was not found.");
        }
        if (path.endsWith(".yaml")) {
            return JSON.fromYAML(content, klass);
        }
        else {
            return JSON.fromJson(content, klass);
        }
    }

}
