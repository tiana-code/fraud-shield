package com.fraudshield.bloom;

public final class MurmurHash3 {

    private static final int C1 = 0xcc9e2d51;
    private static final int C2 = 0x1b873593;
    private static final int SEED = 0xe17a1465;

    private MurmurHash3() {
    }

    public static int hash32(byte[] data, int seed) {
        int h1 = seed;
        int length = data.length;
        int blocks = length >> 2;

        for (int i = 0; i < blocks; i++) {
            int k1 = getInt(data, i << 2);
            k1 *= C1;
            k1 = Integer.rotateLeft(k1, 15);
            k1 *= C2;
            h1 ^= k1;
            h1 = Integer.rotateLeft(h1, 13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        int tail = blocks << 2;
        int k1 = 0;
        int remaining = length & 3;
        if (remaining >= 3) k1 ^= (data[tail + 2] & 0xff) << 16;
        if (remaining >= 2) k1 ^= (data[tail + 1] & 0xff) << 8;
        if (remaining >= 1) {
            k1 ^= data[tail] & 0xff;
            k1 *= C1;
            k1 = Integer.rotateLeft(k1, 15);
            k1 *= C2;
            h1 ^= k1;
        }

        h1 ^= length;
        return fmix32(h1);
    }

    public static long[] hash128(byte[] data) {
        int length = data.length;
        long h1 = SEED;
        long h2 = SEED;
        long c1 = 0x87c37b91114253d5L;
        long c2 = 0x4cf5ad432745937fL;

        int blocks = length >> 4;
        for (int i = 0; i < blocks; i++) {
            long k1 = getLong(data, i * 16);
            long k2 = getLong(data, i * 16 + 8);

            k1 *= c1;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= c2;
            h1 ^= k1;

            h1 = Long.rotateLeft(h1, 27);
            h1 += h2;
            h1 = h1 * 5 + 0x52dce729L;

            k2 *= c2;
            k2 = Long.rotateLeft(k2, 33);
            k2 *= c1;
            h2 ^= k2;

            h2 = Long.rotateLeft(h2, 31);
            h2 += h1;
            h2 = h2 * 5 + 0x38495ab5L;
        }

        int tail = blocks * 16;
        long k1 = 0;
        long k2 = 0;
        int rem = length & 15;

        if (rem >= 15) k2 ^= ((long) (data[tail + 14] & 0xff)) << 48;
        if (rem >= 14) k2 ^= ((long) (data[tail + 13] & 0xff)) << 40;
        if (rem >= 13) k2 ^= ((long) (data[tail + 12] & 0xff)) << 32;
        if (rem >= 12) k2 ^= ((long) (data[tail + 11] & 0xff)) << 24;
        if (rem >= 11) k2 ^= ((long) (data[tail + 10] & 0xff)) << 16;
        if (rem >= 10) k2 ^= ((long) (data[tail + 9] & 0xff)) << 8;
        if (rem >= 9) k2 ^= (long) (data[tail + 8] & 0xff);

        if (rem >= 9) {
            k2 *= c2;
            k2 = Long.rotateLeft(k2, 33);
            k2 *= c1;
            h2 ^= k2;
        }

        if (rem >= 8) k1 ^= ((long) (data[tail + 7] & 0xff)) << 56;
        if (rem >= 7) k1 ^= ((long) (data[tail + 6] & 0xff)) << 48;
        if (rem >= 6) k1 ^= ((long) (data[tail + 5] & 0xff)) << 40;
        if (rem >= 5) k1 ^= ((long) (data[tail + 4] & 0xff)) << 32;
        if (rem >= 4) k1 ^= ((long) (data[tail + 3] & 0xff)) << 24;
        if (rem >= 3) k1 ^= ((long) (data[tail + 2] & 0xff)) << 16;
        if (rem >= 2) k1 ^= ((long) (data[tail + 1] & 0xff)) << 8;
        if (rem >= 1) k1 ^= (long) (data[tail] & 0xff);

        if (rem >= 1) {
            k1 *= c1;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= c2;
            h1 ^= k1;
        }

        h1 ^= length;
        h2 ^= length;

        h1 += h2;
        h2 += h1;

        h1 = fmix64(h1);
        h2 = fmix64(h2);

        h1 += h2;
        h2 += h1;

        return new long[]{h1, h2};
    }

    private static int fmix32(int h) {
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return h;
    }

    private static long fmix64(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;
        return k;
    }

    private static int getInt(byte[] data, int offset) {
        return (data[offset] & 0xff)
                | ((data[offset + 1] & 0xff) << 8)
                | ((data[offset + 2] & 0xff) << 16)
                | ((data[offset + 3] & 0xff) << 24);
    }

    private static long getLong(byte[] data, int offset) {
        return (data[offset] & 0xffL)
                | ((data[offset + 1] & 0xffL) << 8)
                | ((data[offset + 2] & 0xffL) << 16)
                | ((data[offset + 3] & 0xffL) << 24)
                | ((data[offset + 4] & 0xffL) << 32)
                | ((data[offset + 5] & 0xffL) << 40)
                | ((data[offset + 6] & 0xffL) << 48)
                | ((data[offset + 7] & 0xffL) << 56);
    }

    public static int[] generateHashes(String value, int numHashes) {
        byte[] bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        long[] h128 = hash128(bytes);
        long h1 = h128[0];
        long h2 = h128[1];

        int[] hashes = new int[numHashes];
        for (int i = 0; i < numHashes; i++) {
            hashes[i] = (int) ((h1 + (long) i * h2) & Long.MAX_VALUE);
        }
        return hashes;
    }
}
