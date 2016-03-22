package com.fulmicoton.collodion.processors.sequencematcher;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.LinkedList;
import java.util.Queue;

public class AhoCorasick {

    private static final Node[] EMPTY_NODE_ARRAY = new Node[0];

    private final Node root;

    AhoCorasick() {
        this.root = new Node();
    }

    public Node getRoot() {
        return this.root;
    }

    public static class Node {
        private Node fallback = null;
        private int i;
        public final TIntSet terminals = new TIntHashSet();
        final TIntObjectHashMap<Node> branches = new TIntObjectHashMap<>();

        private void addTerminal(final int terminal) {
            this.terminals.add(terminal);
        }

        private Node getOrInsert(final int termId) {
            final Node result = branches.get(termId);
            if (result != null) {
                return result;
            }
            else {
                final Node newNode = new Node();
                this.branches.put(termId, newNode);
                return newNode;
            }
        }

        private Node[] children() {
            return this.branches.values(EMPTY_NODE_ARRAY);
        }

        public Node goTo(final int token) {
            final Node node = this.branches.get(token);
            if (node == null) {
                if (this.fallback == this) {
                    return this;
                }
                else {
                    return this.fallback.goTo(token);
                }
            }
            else {
                return node;
            }
        }
    }

    public void insert(final int[] sequence, final int output) {
        if (sequence.length == 0) {
            throw new IllegalArgumentException("Does not accept empty sequence");
        }
        Node node = this.root;
        final Node[] nodeSequence = new Node[sequence.length];
        for (int i=0; i < sequence.length; i++) {
            final int termId = sequence[i];
            node = node.getOrInsert(termId);
            nodeSequence[i] = node;
        }
        node.addTerminal(output);
    }

    public void finalize() {
        this.root.fallback = this.root;
        final Queue<Node> queue = new LinkedList<>();
        {
            for (final Node node : this.root.branches.values(new Node[0])) {
                node.fallback = this.root;
                queue.add(node);
            }
        }
        while (!queue.isEmpty()) {
            final Node node = queue.poll();
            final TIntObjectIterator<Node> it = node.branches.iterator() ;
            while (it.hasNext()) {
                it.advance();
                final int k = it.key();
                final Node child = it.value();
                queue.add(child);
                Node v = node.fallback;
                while (!v.branches.containsKey(k) && v!= this.root) {
                    v = v.fallback;
                }
                child.fallback = v.goTo(k);
                child.terminals.addAll(child.fallback.terminals);
            }
        }

    }

}
