package com.fraudshield.pattern;

import java.util.*;

/**
 * Build: O(sum of pattern lengths)
 * Search: O(n + m + z) where n=text length, m=total pattern length, z=number of matches
 */
public class AhoCorasickMatcher {

    private final TrieNode root;
    private boolean built;

    public AhoCorasickMatcher() {
        this.root = new TrieNode();
        this.root.failureLink = this.root;
        this.built = false;
    }

    public AhoCorasickMatcher(Collection<String> patterns) {
        this();
        Objects.requireNonNull(patterns, "patterns must not be null");
        for (String pattern : patterns) {
            addPattern(pattern);
        }
        build();
    }

    public void addPattern(String pattern) {
        if (built) {
            throw new IllegalStateException("Cannot add patterns after build(). Create a new instance.");
        }
        Objects.requireNonNull(pattern, "pattern must not be null");
        if (pattern.isEmpty()) {
            return;
        }
        TrieNode current = root;
        for (char c : pattern.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.outputPattern = pattern;
    }

    public void build() {
        Queue<TrieNode> queue = new ArrayDeque<>();

        for (TrieNode child : root.children.values()) {
            child.failureLink = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            TrieNode current = queue.poll();

            for (Map.Entry<Character, TrieNode> entry : current.children.entrySet()) {
                char c = entry.getKey();
                TrieNode child = entry.getValue();

                TrieNode failure = current.failureLink;
                while (failure != root && !failure.children.containsKey(c)) {
                    failure = failure.failureLink;
                }
                child.failureLink = failure.children.getOrDefault(c, root);
                if (child.failureLink == child) {
                    child.failureLink = root;
                }

                queue.add(child);
            }
        }

        built = true;
    }

    public List<MatchResult> findAll(String text) {
        Objects.requireNonNull(text, "text must not be null");
        ensureBuilt();
        List<MatchResult> results = new ArrayList<>();

        traverse(text, (position, node) -> {
            String pattern = node.outputPattern;
            int start = position - pattern.length() + 1;
            results.add(new MatchResult(pattern, start, position));
            return false;
        });

        return results;
    }

    public boolean containsAny(String text) {
        Objects.requireNonNull(text, "text must not be null");
        ensureBuilt();
        return traverse(text, (position, node) -> true);
    }

    private void ensureBuilt() {
        if (!built) {
            throw new IllegalStateException("Call build() before searching.");
        }
    }

    private boolean traverse(String text, TerminalHandler handler) {
        TrieNode current = root;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            while (current != root && !current.children.containsKey(c)) {
                current = current.failureLink;
            }
            current = current.children.getOrDefault(c, root);

            TrieNode check = current;
            while (check != root) {
                if (check.isTerminal() && handler.onMatch(i, check)) {
                    return true;
                }
                check = check.failureLink;
            }
        }
        return false;
    }

    @FunctionalInterface
    private interface TerminalHandler {
        boolean onMatch(int position, TrieNode node);
    }

    public record MatchResult(String pattern, int startIndex, int endIndex) {
    }
}
