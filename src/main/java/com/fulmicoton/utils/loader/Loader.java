package com.fulmicoton.utils.loader;

import java.io.InputStream;

public interface Loader {

    public InputStream open(final String path);

}
