package com.fraudshield.rules.impl;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BlacklistRuleTest {

    private final BlacklistRule rule = new BlacklistRule();

    @Test
    void evaluate_blacklisted_returnsTriggered() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.contextBlacklisted());
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(1.0);
    }

    @Test
    void evaluate_notBlacklisted_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.request(), TestData.context());
        assertThat(result.triggered()).isFalse();
        assertThat(result.score()).isZero();
    }
}
