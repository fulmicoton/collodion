package com.fulmicoton.processors.debug;

import com.fulmicoton.common.loader.Loader;
import com.fulmicoton.processors.ProcessorBuilder;
import com.google.common.collect.ImmutableSet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.util.Attribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DebugFilter extends TokenFilter {

    final Set<Class<? extends Attribute>> IGNORE_ATTRIBUTES = ImmutableSet.<Class<? extends Attribute>>builder()
            .add(TermToBytesRefAttribute.class)
            .build();
    final List<AttrTypePair> attributes = new ArrayList<>();
    final String name;

    protected DebugFilter(final TokenStream input, final String name) {
        super(input);
        this.name = name;
        final Set<Class<? extends Attribute>> attrSet = new HashSet<>(IGNORE_ATTRIBUTES);
        final Iterator<Class<? extends Attribute>> attributeClassIt = input.getAttributeClassesIterator();
        while (attributeClassIt.hasNext()) {
            final Class<? extends Attribute> attrClass = attributeClassIt.next();
            if (attrSet.add(attrClass)) {
                final Attribute attr = input.getAttribute(attrClass);
                this.attributes.add(new AttrTypePair(attrClass, attr));
            }
        }
    }


    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken())
            return false;
        System.out.println("--------");
        if (this.name != null) {
            System.out.println("= " + this.name + " =");
        }
        for (AttrTypePair attrType: this.attributes) {
            System.out.println("  " + attrType.toString());
        }
        return true;
    }

    public static class Builder implements ProcessorBuilder<DebugFilter> {

        private String name;

        @Override
        public void init(final Loader loader) throws IOException {
        }

        @Override
        public DebugFilter createFilter(TokenStream prev) throws IOException {
            return new DebugFilter(prev, name);
        }
    }
}