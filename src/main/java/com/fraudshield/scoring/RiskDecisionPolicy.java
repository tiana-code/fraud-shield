package com.fraudshield.scoring;

import org.springframework.stereotype.Component;

@Component
public class RiskDecisionPolicy {

    private final ScoringConfig scoringConfig;

    public RiskDecisionPolicy(ScoringConfig scoringConfig) {
        this.scoringConfig = scoringConfig;
    }

    public RiskDecision evaluate(FraudScore fraudScore) {
        if (fraudScore.hasTerminalTrigger()) {
            return RiskDecision.BLOCK;
        }

        double score = fraudScore.score();
        ScoringConfig.DecisionThresholds thresholds = scoringConfig.thresholds();

        if (score >= thresholds.block()) {
            return RiskDecision.BLOCK;
        }
        if (score >= thresholds.highRisk()) {
            return RiskDecision.CHALLENGE;
        }
        if (score >= thresholds.suspicious()) {
            return RiskDecision.REVIEW;
        }
        return RiskDecision.ALLOW;
    }
}
