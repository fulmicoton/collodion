package com.fulmicoton.collodion.processors.sequencematcher;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;


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

        public final TIntList terminals = new TIntArrayList();
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


        private void insertOrConnect(final int[] sequence,
                                    final int sequenceId,
                                    final Node[] canonicalSequence,
                                    final int canonicalSequenceId,
                                    final int output) {
            if (sequenceId == sequence.length) {
                this.addTerminal(output);
            }
            else {
                final int termId = sequence[sequenceId];
                final Node nextNode = branches.get(termId);
                if (nextNode == null) {
                    this.branches.put(termId, canonicalSequence[canonicalSequenceId]);
                }
                else {
                    nextNode.insertOrConnect(sequence, sequenceId + 1, canonicalSequence, canonicalSequenceId + 1, output);
                }
            }
        }

        private Node get(final int token, final Node defaultResult) {
            final Node node = this.branches.get(token);
            if (node == null) {
                return defaultResult;
            }
            else {
                return node;
            }
        }
    }

    public Node goTo(final Node from, final int token) {
        return from.get(token, this.root);
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
        for (final Node child: this.root.children()) {
            child.insertOrConnect(sequence, 0, nodeSequence, 0, output);
        }
    }

}
