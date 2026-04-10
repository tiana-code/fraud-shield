package com.fraudshield.bloom;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class MurmurHash3Test {

    @Test
    void hash32_emptyInput_returnsConsistentHash() {
        byte[] empty = new byte[0];
        int result = MurmurHash3.hash32(empty, 0);
        assertThat(result).isEqualTo(MurmurHash3.hash32(empty, 0));
    }

    @Test
    void hash32_sameInputSameSeed_isConsistent() {
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        int first = MurmurHash3.hash32(data, 42);
        int second = MurmurHash3.hash32(data, 42);
        assertThat(first).isEqualTo(second);
    }

    @Test
    void hash32_differentSeeds_produceDifferentHashes() {
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        int hash1 = MurmurHash3.hash32(data, 0);
        int hash2 = MurmurHash3.hash32(data, 1);
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void hash32_singleByteTail() {
        byte[] data = "a".getBytes(StandardCharsets.UTF_8);
        assertThat(data.length & 3).isEqualTo(1);
        int result = MurmurHash3.hash32(data, 0);
        assertThat(result).isEqualTo(MurmurHash3.hash32(data, 0));
    }

    @Test
    void hash32_twoByteTail() {
        byte[] data = "ab".getBytes(StandardCharsets.UTF_8);
        assertThat(data.length & 3).isEqualTo(2);
        int result = MurmurHash3.hash32(data, 0);
        assertThat(result).isEqualTo(MurmurHash3.hash32(data, 0));
    }

    @Test
    void hash32_threeByteTail() {
        byte[] data = "abc".getBytes(StandardCharsets.UTF_8);
        assertThat(data.length & 3).isEqualTo(3);
        int result = MurmurHash3.hash32(data, 0);
        assertThat(result).isEqualTo(MurmurHash3.hash32(data, 0));
    }

    @Test
    void hash32_alignedInput_noTail() {
        byte[] data = "abcd".getBytes(StandardCharsets.UTF_8);
        assertThat(data.length & 3).isZero();
        int result = MurmurHash3.hash32(data, 0);
        assertThat(result).isEqualTo(MurmurHash3.hash32(data, 0));
    }

    @Test
    void hash32_differentInputs_differentHashes() {
        int hash1 = MurmurHash3.hash32("hello".getBytes(StandardCharsets.UTF_8), 0);
        int hash2 = MurmurHash3.hash32("world".getBytes(StandardCharsets.UTF_8), 0);
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void hash128_returnsNonTrivialValues() {
        byte[] data = "test".getBytes(StandardCharsets.UTF_8);
        long[] result = MurmurHash3.hash128(data);
        assertThat(result).hasSize(2);
        assertThat(result[0] | result[1]).isNotZero();
    }

    @Test
    void hash128_isConsistent() {
        byte[] data = "fraud-shield".getBytes(StandardCharsets.UTF_8);
        long[] first = MurmurHash3.hash128(data);
        long[] second = MurmurHash3.hash128(data);
        assertThat(first).isEqualTo(second);
    }

    @Test
    void generateHashes_producesRequestedCount() {
        int[] hashes = MurmurHash3.generateHashes("test-value", 7);
        assertThat(hashes).hasSize(7);
    }

    @Test
    void generateHashes_isConsistent() {
        int[] first = MurmurHash3.generateHashes("test-value", 5);
        int[] second = MurmurHash3.generateHashes("test-value", 5);
        assertThat(first).isEqualTo(second);
    }

    @Test
    void generateHashes_differentInputs_differentResults() {
        int[] hashes1 = MurmurHash3.generateHashes("user-1", 5);
        int[] hashes2 = MurmurHash3.generateHashes("user-2", 5);
        assertThat(hashes1).isNotEqualTo(hashes2);
    }
}
