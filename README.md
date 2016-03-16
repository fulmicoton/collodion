# Collodion
## A text-processing tool-chain on top of Lucene



Collodion aims at making a building a NLP tool-chain on top of Lucene TokenStreams.

## DISCLAIMER

Collodion is a work in progress. API may still change.
Please contact me if you want to start using it, so that we can discuss your
needs.


# Why?


Lucene is free, opensource, and enjoys a great community that cares about
performance and language coverage. Other solutions (CoreNLP, OpenNLP, LingPipe) usually
lack either speed, language support, or are commercial.


# Building it

Just run

    mvn install

# What is it really?

Lucene's analyzers are great but require to be built programmatically.
Collodion mostly expose a CollodionAnalyzer, which extends Lucene's Analyzer
but can be build from a configuration file. (JSON or Yaml)


Yaml:

  processors:
    - type: "stem"

    - type: "vocabulary"
      path: "vocabulary.json"

    - type: "tokenpattern"
      path: "pattern"
      maxLength: 100



JSON:

    {
      "processors": [
        {
            "type": "stem"
        },
        {
           "type": "vocabulary",
           "path": "vocabulary.json"
        },
        {
          "type": "tokenpattern",
          "path": "pattern",
          "maxLength": 100
        }
      ]
    }


Using the analyzer is then as simple as :

    // Only this part is specific to collodion
    CollodionAnalyzer analyzer = CollodionAnalyzer.fromPath("my-pipeline.yaml");

    // The rest is the same as any Lucene's analyzer
    final TokenStream tokenStream = collodionAnalyzer.tokenStream("My text.");
    final VocabularyAttribute vocabularyAnnotation = tokenStream.getAttribute(VocabularyAttribute.class);
    while (tokenStream.next()) {
      System.out.println(vocabularyAnnotation);
    }

## Resource path?

By default Collodion will resolve the paths you give it using
your current working directory, and possibly try loading resources relatively to
the `com.fulmicoton.collodion` package.

You can define your own resource loader and use it to load your resources.

    final ChainLoader chainLoader = ChainLoader.of(
      DirectoryLoader.forRoot("/var/resources/"),
      DirectoryLoader.forRoot("/home/pmasurel"),
      ResourceLoader.fromClass(MyClass.class)
    );

    final CollodionAnalyzer analyzer = CollodionAnalyzer.fromPath(chainLoader, "my-pipeline.yaml");
Your loader will then be used to load all of the resources required when building
token filters.

Only the resourceloader and the directory loader are used as building blocks for the
moment, but creating your own is as simple as extending the loader class and implementing
a single method:

    public class HttpLoader extends Loader {

        /**
         * Given an address returns an input stream
         * @param path - it can be filesystem path,
         *               a relative path,
         *               a resource path,
         *               depending on the implementation.
         * @return null if the address does not lead anywhere
         *              else an input stream
         */
        @Override
        public InputStream open(final String path) {
          // ... do stuff ...
        }  
    }


# TokenFilters

Very few TokenFilters are available for the moment but adding a new
one is quite easy.

## Stemmer

Build on top of lucene's porter stemmer, the stemmer add the stem in a
StemAttribute.


## NumberParser

Merge tokens that describe numbers and attach the value as a NumberAttribute.


## Vocabulary

Matches a dictionary and add an annotation attribute with the list of annotations
that match.

Matching can be done on the original form or the stem form, and can be done in
an exact fashion (using Lucene's transducer) or regular expression (using MultiRegexp,
  which compiles all regular expression in one giant DFA).


## TokenPattern

Similar to its homonym in CoreNLP, TokenPattern makes it possible to define
regular expression pattern on tokens.

It currently supports groups, named groups but only works on vocabulary annotations.

It annotates the matching phrase, and then the groups individually.

## Debug

Adding the debug token filter to your pipeline will output all of your tokens to stdout.
