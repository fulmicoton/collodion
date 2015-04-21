package com.fulmicoton.collodion.server;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CollodionServer extends Application<CollodionServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new CollodionServer().run(args);
    }


    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<CollodionServerConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));

    }

    @Override
    public void run(CollodionServerConfiguration configuration,
                    Environment environment) {
        final CorpusResource resource = new CorpusResource();
        environment.jersey().setUrlPattern("/api/*");
        environment.jersey().register(resource);
    }
}

