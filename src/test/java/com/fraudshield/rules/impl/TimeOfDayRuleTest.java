package com.fraudshield.rules.impl;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.TestData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TimeOfDayRuleTest {

    private final TimeOfDayRule rule = new TimeOfDayRule();

    @Test
    void evaluate_withinSuspiciousWindow_returnsTriggered() {
        ZonedDateTime time = ZonedDateTime.of(LocalDate.now(), LocalTime.of(2, 30), ZoneOffset.UTC);
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(100), time), TestData.context());
        assertThat(result.triggered()).isTrue();
        assertThat(result.score()).isEqualTo(0.45);
    }

    @Test
    void evaluate_atExactStart_returnsTriggered() {
        ZonedDateTime time = ZonedDateTime.of(LocalDate.now(), LocalTime.of(1, 0), ZoneOffset.UTC);
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(100), time), TestData.context());
        assertThat(result.triggered()).isTrue();
    }

    @Test
    void evaluate_atExactEnd_returnsClean() {
        ZonedDateTime time = ZonedDateTime.of(LocalDate.now(), LocalTime.of(5, 0), ZoneOffset.UTC);
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(100), time), TestData.context());
        assertThat(result.triggered()).isFalse();
    }

    @Test
    void evaluate_outsideWindow_returnsClean() {
        ZonedDateTime time = ZonedDateTime.of(LocalDate.now(), LocalTime.of(12, 0), ZoneOffset.UTC);
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(100), time), TestData.context());
        assertThat(result.triggered()).isFalse();
    }

    @Test
    void evaluate_justBeforeStart_returnsClean() {
        ZonedDateTime time = ZonedDateTime.of(LocalDate.now(), LocalTime.of(0, 59), ZoneOffset.UTC);
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(100), time), TestData.context());
        assertThat(result.triggered()).isFalse();
    }

    @Test
    void evaluate_nonUtcTimezone_convertsCorrectly() {
        ZonedDateTime time = ZonedDateTime.of(LocalDate.now(), LocalTime.of(5, 30), ZoneOffset.ofHours(3));
        FraudRule.RuleResult result = rule.evaluate(TestData.request(BigDecimal.valueOf(100), time), TestData.context());
        assertThat(result.triggered()).isTrue();
    }
}
