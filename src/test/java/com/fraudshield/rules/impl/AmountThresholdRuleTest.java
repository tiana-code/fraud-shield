package com.fraudshield.rules.impl;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.TestData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AmountThresholdRuleTest {

    private final AmountThresholdRule rule = new AmountThresholdRule();

    @Test
    void evaluate_aboveHighThreshold_returnsHighScore() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(15000)), TestData.context());
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.8);
    }

    @Test
    void evaluate_aboveMediumThreshold_returnsMediumScore() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(5000)), TestData.context());
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.4);
    }

    @Test
    void evaluate_belowThreshold_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(100)), TestData.context());
        assertThat(result.triggered()).isFalse();
        assertThat(result.score()).isZero();
    }

    @Test
    void evaluate_exactHighThreshold_returnsMedium() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(10000)), TestData.context());
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.4);
    }

    @Test
    void type_returnsAmountThreshold() {
        assertThat(rule.type()).isEqualTo(com.fraudshield.rules.FraudRuleType.AMOUNT_THRESHOLD);
    }
}
