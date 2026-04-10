package com.fraudshield.pattern;

import java.util.HashMap;
import java.util.Map;

class TrieNode {

    final Map<Character, TrieNode> children = new HashMap<>();
    TrieNode failureLink;
    String outputPattern;

    TrieNode() {
        this.failureLink = null;
        this.outputPattern = null;
    }

    boolean isTerminal() {
        return outputPattern != null;
    }
}
