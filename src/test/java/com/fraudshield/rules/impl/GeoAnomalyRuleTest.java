package com.fraudshield.rules.impl;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeoAnomalyRuleTest {

    private final GeoAnomalyRule rule = new GeoAnomalyRule();

    @Test
    void evaluate_countryChanged_returnsTriggered() {
        FraudRule.RuleResult result = rule.evaluate(TestData.requestWithCountry("RU"), TestData.contextWithLastCountry("US"));
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.75);
    }

    @Test
    void evaluate_sameCountry_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.requestWithCountry("US"), TestData.contextWithLastCountry("US"));
        assertThat(result.triggered()).isFalse();
    }

    @Test
    void evaluate_sameCountryCaseInsensitive_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.requestWithCountry("us"), TestData.contextWithLastCountry("US"));
        assertThat(result.triggered()).isFalse();
    }

    @Test
    void evaluate_nullLastCountry_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.requestWithCountry("US"), TestData.contextWithLastCountry(null));
        assertThat(result.triggered()).isFalse();
    }

    @Test
    void evaluate_nullCurrentCountry_returnsClean() {
        FraudRule.RuleResult result = rule.evaluate(TestData.requestWithCountry(null), TestData.contextWithLastCountry("US"));
        assertThat(result.triggered()).isFalse();
    }
}
