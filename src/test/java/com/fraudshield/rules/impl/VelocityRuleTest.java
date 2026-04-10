package com.fraudshield.rules.impl;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VelocityRuleTest {

    private final VelocityRule rule = new VelocityRule();

    @Test
    void evaluate_highVelocity_returnsHighScore() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithVelocity(15));
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.9);
    }

    @Test
    void evaluate_mediumVelocity_returnsMediumScore() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithVelocity(7));
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.5);
    }

    @Test
    void evaluate_normalVelocity_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithVelocity(2));
        assertThat(result.triggered()).isFalse();
        assertThat(result.score()).isZero();
    }

    @Test
    void evaluate_exactHighThreshold_returnsHighScore() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithVelocity(10));
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.9);
    }
}
