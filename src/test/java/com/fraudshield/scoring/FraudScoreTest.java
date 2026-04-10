package com.fraudshield.scoring;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FraudScoreTest {

    @Test
    void validScore_inRange() {
        FraudScore score = new FraudScore(0.5, List.of());
        assertThat(score.score()).isEqualTo(0.5);
    }

    @Test
    void zeroScore_isValid() {
        assertThat(new FraudScore(0.0, List.of()).score()).isZero();
    }

    @Test
    void maxScore_isValid() {
        assertThat(new FraudScore(1.0, List.of()).score()).isEqualTo(1.0);
    }

    @Test
    void negativeScore_throws() {
        assertThatThrownBy(() -> new FraudScore(-0.1, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[0.0, 1.0]");
    }

    @Test
    void scoreAboveOne_throws() {
        assertThatThrownBy(() -> new FraudScore(1.1, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanScore_throws() {
        assertThatThrownBy(() -> new FraudScore(Double.NaN, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void infiniteScore_throws() {
        assertThatThrownBy(() -> new FraudScore(Double.POSITIVE_INFINITY, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullRuleResults_throws() {
        assertThatThrownBy(() -> new FraudScore(0.5, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void reasons_derivedFromTriggeredResults() {
        var triggered = FraudRule.RuleResult.triggered(FraudRuleType.BLACKLIST, 1.0, "On blacklist");
        var clean = FraudRule.RuleResult.clean(FraudRuleType.VELOCITY);
        FraudScore score = new FraudScore(0.8, List.of(triggered, clean));

        assertThat(score.reasons()).containsExactly("On blacklist");
    }

    @Test
    void hasTerminalTrigger_blacklistTriggered_returnsTrue() {
        var triggered = FraudRule.RuleResult.triggered(FraudRuleType.BLACKLIST, 1.0, "Blacklisted");
        FraudScore score = new FraudScore(1.0, List.of(triggered));

        assertThat(score.hasTerminalTrigger()).isTrue();
    }

    @Test
    void hasTerminalTrigger_noTerminalRule_returnsFalse() {
        var triggered = FraudRule.RuleResult.triggered(FraudRuleType.VELOCITY, 0.9, "High velocity");
        FraudScore score = new FraudScore(0.9, List.of(triggered));

        assertThat(score.hasTerminalTrigger()).isFalse();
    }

    @Test
    void ruleResults_isDefensiveCopy() {
        var result = FraudRule.RuleResult.clean(FraudRuleType.VELOCITY);
        var mutableList = new ArrayList<>(List.of(result));
        FraudScore score = new FraudScore(0.0, mutableList);

        mutableList.clear();
        assertThat(score.ruleResults()).hasSize(1);
    }
}
