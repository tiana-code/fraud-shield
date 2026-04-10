package com.fraudshield.bloom;

import java.util.BitSet;
import java.util.Objects;

/**
 * Memory: ~1.44 * expectedInsertions * log2(1/fpp) bits
 * Lookup: O(k) where k = number of hash functions
 * Not thread-safe. External synchronization required for concurrent access.
 */
public class BloomFilter {

    private final BitSet bits;
    private final int numHashFunctions;
    private final int bitSize;
    private long insertions;

    public BloomFilter(long expectedInsertions, double falsePositiveProbability) {
        if (expectedInsertions <= 0) {
            throw new IllegalArgumentException("expectedInsertions must be positive");
        }
        if (falsePositiveProbability <= 0 || falsePositiveProbability >= 1) {
            throw new IllegalArgumentException("falsePositiveProbability must be in (0, 1)");
        }

        this.bitSize = optimalBitSize(expectedInsertions, falsePositiveProbability);
        this.numHashFunctions = optimalNumHashFunctions(expectedInsertions, bitSize);
        this.bits = new BitSet(bitSize);
        this.insertions = 0;
    }

    public void add(String value) {
        Objects.requireNonNull(value, "value must not be null");
        int[] hashes = MurmurHash3.generateHashes(value, numHashFunctions);
        for (int hash : hashes) {
            bits.set((hash & Integer.MAX_VALUE) % bitSize);
        }
        insertions++;
    }

    public boolean mightContain(String value) {
        Objects.requireNonNull(value, "value must not be null");
        int[] hashes = MurmurHash3.generateHashes(value, numHashFunctions);
        for (int hash : hashes) {
            if (!bits.get((hash & Integer.MAX_VALUE) % bitSize)) {
                return false;
            }
        }
        return true;
    }

    // (1 - e^(-k*n/m))^k
    public double expectedFpp() {
        if (insertions == 0) return 0.0;
        double exponent = -(double) numHashFunctions * insertions / bitSize;
        double base = 1.0 - Math.exp(exponent);
        return Math.pow(base, numHashFunctions);
    }

    public long insertionCount() {
        return insertions;
    }

    public int bitSize() {
        return bitSize;
    }

    public int numHashFunctions() {
        return numHashFunctions;
    }

    // m = -n * ln(p) / (ln(2))^2
    private static int optimalBitSize(long expectedInsertions, double fpp) {
        long bits = (long) Math.ceil(-expectedInsertions * Math.log(fpp) / (Math.log(2) * Math.log(2)));
        if (bits > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Required bit size %d exceeds maximum %d. Reduce expectedInsertions or increase falsePositiveProbability."
                            .formatted(bits, Integer.MAX_VALUE));
        }
        return (int) bits;
    }

    // k = (m/n) * ln(2)
    private static int optimalNumHashFunctions(long expectedInsertions, long bitSize) {
        return Math.max(1, (int) Math.round((double) bitSize / expectedInsertions * Math.log(2)));
    }
}
