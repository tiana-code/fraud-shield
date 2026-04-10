package com.fraudshield.rules.impl;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceFingerprintRuleTest {

    private final DeviceFingerprintRule rule = new DeviceFingerprintRule();

    @Test
    void evaluate_noDeviceId_returnsTriggered() {
        FraudRule.RuleResult result = rule.evaluate(TestData.requestWithDevice(null), TestData.context());
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.4);
    }

    @Test
    void evaluate_unseenDevice_returnsTriggered() {
        FraudRule.RuleResult result = rule.evaluate(TestData.requestWithDevice("new-device"), TestData.contextDeviceUnseen());
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.6);
    }

    @Test
    void evaluate_knownDevice_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.requestWithDevice("device-abc"), TestData.context());
        assertThat(result.triggered()).isFalse();
        assertThat(result.score()).isZero();
    }
}
