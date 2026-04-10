package com.fraudshield.bloom;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BloomFilterTest {

    @Test
    void addAndMightContain_returnsTrue() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        filter.add("user-123");
        assertThat(filter.mightContain("user-123")).isTrue();
    }

    @Test
    void mightContain_absentElement_returnsFalse() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        assertThat(filter.mightContain("never-added")).isFalse();
    }

    @Test
    void multipleElements_allDetected() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        filter.add("alpha");
        filter.add("beta");
        filter.add("gamma");
        assertThat(filter.mightContain("alpha")).isTrue();
        assertThat(filter.mightContain("beta")).isTrue();
        assertThat(filter.mightContain("gamma")).isTrue();
    }

    @Test
    void expectedFpp_emptyFilter_returnsZero() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        assertThat(filter.expectedFpp()).isZero();
    }

    @Test
    void expectedFpp_afterInsertions_isPositive() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        for (int i = 0; i < 100; i++) {
            filter.add("element-" + i);
        }
        assertThat(filter.expectedFpp()).isGreaterThan(0.0);
    }

    @Test
    void falsePositiveRate_underTarget() {
        int capacity = 10_000;
        double targetFpp = 0.05;
        BloomFilter filter = new BloomFilter(capacity, targetFpp);

        for (int i = 0; i < capacity; i++) {
            filter.add("present-" + i);
        }

        int falsePositives = 0;
        int testCount = 10_000;
        for (int i = 0; i < testCount; i++) {
            if (filter.mightContain("absent-" + i)) {
                falsePositives++;
            }
        }
        double observedFpp = (double) falsePositives / testCount;
        assertThat(observedFpp).isLessThan(targetFpp * 2);
    }

    @Test
    void insertionCount_tracksInsertions() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        assertThat(filter.insertionCount()).isZero();
        filter.add("one");
        filter.add("two");
        assertThat(filter.insertionCount()).isEqualTo(2);
    }

    @Test
    void add_nullValue_throwsNPE() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        assertThatThrownBy(() -> filter.add(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void mightContain_nullValue_throwsNPE() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        assertThatThrownBy(() -> filter.mightContain(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructor_invalidExpectedInsertions_throws() {
        assertThatThrownBy(() -> new BloomFilter(0, 0.01))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new BloomFilter(-1, 0.01))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_invalidFpp_throws() {
        assertThatThrownBy(() -> new BloomFilter(1000, 0.0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new BloomFilter(1000, 1.0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new BloomFilter(1000, -0.1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bitSize_isPositive() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        assertThat(filter.bitSize()).isPositive();
    }

    @Test
    void numHashFunctions_isAtLeastOne() {
        BloomFilter filter = new BloomFilter(1000, 0.01);
        assertThat(filter.numHashFunctions()).isGreaterThanOrEqualTo(1);
    }
}
