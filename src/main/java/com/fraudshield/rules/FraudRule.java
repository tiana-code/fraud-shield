package com.fraudshield.rules;

import com.fraudshield.dto.request.EvaluateTransactionRequest;

public interface FraudRule {

    FraudRuleType type();

    RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context);

    record RuleResult(FraudRuleType ruleType, double score, String reason, boolean triggered) {

        public static RuleResult clean(FraudRuleType ruleType) {
            return new RuleResult(ruleType, 0.0, "No anomaly detected", false);
        }

        public static RuleResult triggered(FraudRuleType ruleType, double score, String reason) {
            return new RuleResult(ruleType, score, reason, true);
        }
    }

    record RuleContext(
            int recentTransactionCount,
            String lastKnownCountry,
            String deviceId,
            boolean deviceSeen,
            int previousFraudCount,
            boolean isBlacklisted,
            double cardBinRiskScore
    ) {
    }
}
