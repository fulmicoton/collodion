package com.fulmicoton.collodion.processors.sequencematcher;

import gnu.trove.TIntCollection;
import gnu.trove.impl.hash.TIntIntHash;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AhoCorasick {

    private TIntList nodeFallbacks = new TIntArrayList();
    private List<TIntIntHashMap> nodeBranches = new ArrayList<>();
    private List<TIntSet> nodeTerminals = new ArrayList<>();

    private static final TIntSet EMPTY_TERMINALS = new TIntHashSet();
    private static final TIntIntHashMap EMPTY_BRANCHES = newBranchMap();


    private static TIntIntHashMap newBranchMap() {
        return new TIntIntHashMap(3, 2, -1, -1);
    }

    AhoCorasick() {
        //this.root = new Node();
        this.newNode();
    }

    public int getRoot() {
        return 0;
    }

    public int newNode() {
        int nodeId = this.nodeFallbacks.size();
        this.nodeFallbacks.add(-1);
        nodeBranches.add(EMPTY_BRANCHES);
        nodeTerminals.add(EMPTY_TERMINALS);
        return nodeId;
    }

    public void addTerminal(final int nodeId, final int terminal) {
        TIntSet terminals = this.nodeTerminals.get(nodeId);
        if (terminals.isEmpty()) {
            terminals = new TIntHashSet();
            this.nodeTerminals.set(nodeId, terminals);
        }
        terminals.add(terminal);
    }

    public void addTerminals(final int nodeId, final TIntCollection terminalsToAdd) {
        TIntSet terminals = this.nodeTerminals.get(nodeId);
        if (terminals.isEmpty()) {
            terminals = new TIntHashSet();
            this.nodeTerminals.set(nodeId, terminals);
        }
        terminals.addAll(terminalsToAdd);
    }

    public void addBranch(final int fromNodeId, final int tokenId, final int toNodeId) {
        TIntIntHashMap branches = this.nodeBranches.get(fromNodeId);
        if (branches.isEmpty()) {
            branches = newBranchMap();
            this.nodeBranches.set(fromNodeId, branches);
        }
        branches.put(tokenId, toNodeId);
    }
//
//    public static class Node {
//        private Node fallback = null;
//        public final TIntSet terminals = new TIntHashSet();
//        final TIntObjectHashMap<Node> branches = new TIntObjectHashMap<>();
//
//        private void addTerminal(final int terminal) {
//            this.terminals.add(terminal);
//        }
//
//        private Node getOrInsert(final int termId) {
//            final Node result = branches.get(termId);
//            if (result != null) {
//                return result;
//            }
//            else {
//                final Node newNode = new Node();
//                this.branches.put(termId, newNode);
//                return newNode;
//            }
//        }
//
//        public Node goTo(final int token) {
//            final Node node = this.branches.get(token);
//            if (node == null) {
//                if (this.fallback == this) {
//                    return this;
//                }
//                else {
//                    return this.fallback.goTo(token);
//                }
//            }
//            else {
//                return node;
//            }
//        }
//    }

    public TIntSet getTerminals(int nodeId) {
        return this.nodeTerminals.get(nodeId);
    }

    private int getOrInsert(final int nodeId, final int termId) {
        final int result = this.nodeBranches.get(nodeId).get(termId);
        if (result != -1) {
            return result;
        }
        else {
            final int newNodeId = this.newNode();
            this.addBranch(nodeId, termId, newNodeId);
            return newNodeId;
        }
    }

    public void insert(final int[] sequence, final int output) {
        if (sequence.length == 0) {
            throw new IllegalArgumentException("Does not accept empty sequence");
        }
        int nodeId = getRoot();
        for (final int termId: sequence) {
            nodeId = this.getOrInsert(nodeId, termId);
        }
        this.addTerminal(nodeId, output);
    }

    public int goTo(final int from, final int token) {
        final int nodeDest = this.nodeBranches.get(from).get(token); //branches.get(token);
        if (nodeDest == -1) {
            final int fallback = this.nodeFallbacks.get(from);
            if (fallback == from) {
                return from;
            }
            else {
                return goTo(fallback, token);
            }
        }
        else {
            return nodeDest;
        }
    }

    public void finalize() {
        final int root = this.getRoot();
        this.nodeFallbacks.set(root, root);

        final Queue<Integer> queue = new LinkedList<>();
        {
            final int[] childrenNodes = this.nodeBranches.get(this.getRoot()).values();
            for (final int childNode: childrenNodes) {
                this.nodeFallbacks.set(childNode, root);
                queue.add(childNode);
            }
        }
        while (!queue.isEmpty()) {
            final int nodeId = queue.poll();
            final TIntIntIterator childrenIt = nodeBranches.get(nodeId).iterator();
            while (childrenIt.hasNext()) {
                childrenIt.advance();
                final int k = childrenIt.key();
                final int child = childrenIt.value();
                queue.add(child);
                int v = this.nodeFallbacks.get(nodeId);
                while (!this.nodeBranches.get(v).containsKey(k) && (v != this.getRoot())) {
                    v = this.nodeFallbacks.get(v);
                }
                this.nodeFallbacks.set(child, goTo(v, k));
                final int childFallback = this.nodeFallbacks.get(child);
                final TIntSet childFallbackTerminals = this.nodeTerminals.get(childFallback);
                this.addTerminals(child, childFallbackTerminals);
            }
        }

    }

}
