package com.fraudshield.scoring;

import java.util.Objects;

public record FraudAssessment(
        String transactionId,
        FraudScore fraudScore,
        RiskDecision decision
) {
    public FraudAssessment {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(fraudScore, "fraudScore must not be null");
        Objects.requireNonNull(decision, "decision must not be null");
    }
}
