package com.fraudshield.rules;

import com.fraudshield.dto.request.EvaluateTransactionRequest;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class TestData {

    private TestData() {
    }

    public static EvaluateTransactionRequest request() {
        return request(BigDecimal.valueOf(100));
    }

    public static EvaluateTransactionRequest request(BigDecimal amount) {
        return request(amount, ZonedDateTime.now(ZoneOffset.UTC));
    }

    public static EvaluateTransactionRequest request(BigDecimal amount, ZonedDateTime time) {
        return new EvaluateTransactionRequest(
                "tx-001", "user-1", amount, "USD", "merchant-1",
                "US", "device-abc", "192.168.1.1", "411111", time
        );
    }

    public static EvaluateTransactionRequest requestWithCountry(String country) {
        return new EvaluateTransactionRequest(
                "tx-001", "user-1", BigDecimal.valueOf(100), "USD", "merchant-1",
                country, "device-abc", "192.168.1.1", "411111", ZonedDateTime.now(ZoneOffset.UTC)
        );
    }

    public static EvaluateTransactionRequest requestWithDevice(String deviceId) {
        return new EvaluateTransactionRequest(
                "tx-001", "user-1", BigDecimal.valueOf(100), "USD", "merchant-1",
                "US", deviceId, "192.168.1.1", "411111", ZonedDateTime.now(ZoneOffset.UTC)
        );
    }

    public static FraudRule.RuleContext context() {
        return new FraudRule.RuleContext(0, "US", "device-abc", true, 0, false, 0.0);
    }

    public static FraudRule.RuleContext contextWithVelocity(int transactionCount) {
        return new FraudRule.RuleContext(transactionCount, "US", "device-abc", true, 0, false, 0.0);
    }

    public static FraudRule.RuleContext contextWithFraudCount(int count) {
        return new FraudRule.RuleContext(0, "US", "device-abc", true, count, false, 0.0);
    }

    public static FraudRule.RuleContext contextBlacklisted() {
        return new FraudRule.RuleContext(0, "US", "device-abc", true, 0, true, 0.0);
    }

    public static FraudRule.RuleContext contextWithBinRisk(double risk) {
        return new FraudRule.RuleContext(0, "US", "device-abc", true, 0, false, risk);
    }

    public static FraudRule.RuleContext contextWithLastCountry(String country) {
        return new FraudRule.RuleContext(0, country, "device-abc", true, 0, false, 0.0);
    }

    public static FraudRule.RuleContext contextDeviceUnseen() {
        return new FraudRule.RuleContext(0, "US", "device-abc", false, 0, false, 0.0);
    }
}
