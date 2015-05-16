package com.fulmicoton.collodion.server;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;

public class CollodionServer extends Application<CollodionServerConfiguration> {

    public static class Config {
        @Option(name="--project", usage="Set your project directory")
        public String projectDirectory;
    }

    public static void main(String[] args) throws Exception {
        final Config config = new Config();
        final ParserProperties parserProperties = ParserProperties
                .defaults()
                .withUsageWidth(80);

        final CmdLineParser parser = new CmdLineParser(config, parserProperties);
        try {
            final String[] remainingArgs = Arrays.asList(args)
                    .subList(1, args.length)
                    .toArray(new String[0]);
            parser.parseArgument(remainingArgs);
            CollodionApplication.setProjectPath(new File(config.projectDirectory));
            new CollodionServer().run(new String[]{args[0]});
        } catch(CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }
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
        final CorpusResource corpusResource = new CorpusResource();
        final AnalyzerResource analyzerResource = new AnalyzerResource();

        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");


        environment.jersey().setUrlPattern("/api/*");
        environment.jersey().register(corpusResource);
        environment.jersey().register(analyzerResource);
    }
}

