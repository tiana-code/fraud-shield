package com.fraudshield.rules.impl;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepeatOffenderRuleTest {

    private final RepeatOffenderRule rule = new RepeatOffenderRule();

    @Test
    void evaluate_highRiskCount_returnsHighScore() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithFraudCount(5));
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.95);
    }

    @Test
    void evaluate_mediumRiskCount_returnsMediumScore() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithFraudCount(1));
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.7);
    }

    @Test
    void evaluate_noHistory_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithFraudCount(0));
        assertThat(result.triggered()).isFalse();
        assertThat(result.score()).isZero();
    }

    @Test
    void evaluate_exactHighThreshold_returnsHighScore() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithFraudCount(3));
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.95);
    }
}
