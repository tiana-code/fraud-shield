package com.fraudshield.pattern;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AhoCorasickMatcherTest {

    @Test
    void findAll_singlePattern_findsMatch() {
        var matcher = new AhoCorasickMatcher(List.of("casino"));
        List<AhoCorasickMatcher.MatchResult> results = matcher.findAll("visit the casino now");
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().pattern()).isEqualTo("casino");
    }

    @Test
    void findAll_multiplePatterns_findsAll() {
        var matcher = new AhoCorasickMatcher(List.of("casino", "darkweb", "mixer"));
        List<AhoCorasickMatcher.MatchResult> results = matcher.findAll("casino and darkweb and mixer");
        assertThat(results).hasSize(3)
                .extracting(AhoCorasickMatcher.MatchResult::pattern)
                .containsExactlyInAnyOrder("casino", "darkweb", "mixer");
    }

    @Test
    void findAll_overlappingPatterns() {
        var matcher = new AhoCorasickMatcher(List.of("he", "she", "his", "hers"));
        List<AhoCorasickMatcher.MatchResult> results = matcher.findAll("shers");
        assertThat(results).extracting(AhoCorasickMatcher.MatchResult::pattern)
                .contains("she", "he", "hers");
    }

    @Test
    void findAll_noMatch_returnsEmpty() {
        var matcher = new AhoCorasickMatcher(List.of("casino"));
        assertThat(matcher.findAll("legitimate transaction")).isEmpty();
    }

    @Test
    void containsAny_withMatch_returnsTrue() {
        var matcher = new AhoCorasickMatcher(List.of("fraud", "scam"));
        assertThat(matcher.containsAny("this is a scam")).isTrue();
    }

    @Test
    void containsAny_noMatch_returnsFalse() {
        var matcher = new AhoCorasickMatcher(List.of("fraud", "scam"));
        assertThat(matcher.containsAny("legitimate business")).isFalse();
    }

    @Test
    void addPattern_afterBuild_throwsIllegalState() {
        var matcher = new AhoCorasickMatcher(List.of("test"));
        assertThatThrownBy(() -> matcher.addPattern("new"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void findAll_beforeBuild_throwsIllegalState() {
        var matcher = new AhoCorasickMatcher();
        matcher.addPattern("test");
        assertThatThrownBy(() -> matcher.findAll("test"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void containsAny_beforeBuild_throwsIllegalState() {
        var matcher = new AhoCorasickMatcher();
        matcher.addPattern("test");
        assertThatThrownBy(() -> matcher.containsAny("test"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void constructor_withPatterns_buildsAutomatically() {
        var matcher = new AhoCorasickMatcher(List.of("auto"));
        assertThat(matcher.containsAny("auto-built")).isTrue();
    }

    @Test
    void findAll_nullText_throwsNPE() {
        var matcher = new AhoCorasickMatcher(List.of("test"));
        assertThatThrownBy(() -> matcher.findAll(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void containsAny_nullText_throwsNPE() {
        var matcher = new AhoCorasickMatcher(List.of("test"));
        assertThatThrownBy(() -> matcher.containsAny(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addPattern_nullPattern_throwsNPE() {
        var matcher = new AhoCorasickMatcher();
        assertThatThrownBy(() -> matcher.addPattern(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addPattern_emptyPattern_isIgnored() {
        var matcher = new AhoCorasickMatcher();
        matcher.addPattern("");
        matcher.build();
        assertThat(matcher.findAll("anything")).isEmpty();
    }

    @Test
    void constructor_nullPatterns_throwsNPE() {
        assertThatThrownBy(() -> new AhoCorasickMatcher(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void findAll_matchResult_hasCorrectPositions() {
        var matcher = new AhoCorasickMatcher(List.of("abc"));
        List<AhoCorasickMatcher.MatchResult> results = matcher.findAll("xabcx");
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().startIndex()).isEqualTo(1);
        assertThat(results.getFirst().endIndex()).isEqualTo(3);
    }
}
