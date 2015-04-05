package com.fulmicoton.common.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DirectoryLoader extends Loader {

    private final File root;

    private DirectoryLoader(File root) {
        this.root = root;
    }

    public static DirectoryLoader forRoot(File root) {
        return new DirectoryLoader(root);
    }

    @Override
    public InputStream open(String path) {
        final File resolvedFile = new File(this.root, path);
        if (resolvedFile.exists()) {
            try {
                return new FileInputStream(resolvedFile);
            }
            catch (FileNotFoundException e) {
                throw new IllegalStateException("Somebody removed the file after we checked it exists.", e);
            }

        }
        else {
            return null;
        }
    }
}
