package com.fraudshield.rules.impl;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CardBinRiskRuleTest {

    private final CardBinRiskRule rule = new CardBinRiskRule();

    @Test
    void evaluate_highRiskBin_returnsTriggered() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithBinRisk(0.85));
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.85);
    }

    @Test
    void evaluate_mediumRiskBin_returnsTriggered() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithBinRisk(0.5));
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.5);
    }

    @Test
    void evaluate_lowRiskBin_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextWithBinRisk(0.1));
        assertThat(result.triggered()).isFalse();
        assertThat(result.score()).isZero();
    }
}
