package com.fraudshield.dto.response;

import com.fraudshield.scoring.FraudAssessment;
import com.fraudshield.scoring.RiskDecision;

import java.util.List;

public record FraudEvaluationResponse(
        String transactionId,
        double fraudScore,
        RiskDecision decision,
        List<String> reasons,
        List<RuleResultDto> ruleResults
) {
    public static FraudEvaluationResponse from(FraudAssessment assessment) {
        List<RuleResultDto> ruleResults = assessment.fraudScore().ruleResults().stream()
                .map(r -> new RuleResultDto(r.ruleType().name(), r.score(), r.reason(), r.triggered()))
                .toList();

        return new FraudEvaluationResponse(
                assessment.transactionId(),
                assessment.fraudScore().score(),
                assessment.decision(),
                assessment.fraudScore().reasons(),
                ruleResults);
    }

    public record RuleResultDto(
            String ruleType,
            double score,
            String reason,
            boolean triggered
    ) {
    }
}
