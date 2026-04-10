package com.fraudshield.scoring;

import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RiskDecisionPolicyTest {

    private final RiskDecisionPolicy policy = createPolicy(0.4, 0.7, 0.9);

    @Test
    void evaluate_belowSuspicious_returnsAllow() {
        assertThat(policy.evaluate(score(0.39))).isEqualTo(RiskDecision.ALLOW);
    }

    @Test
    void evaluate_atSuspicious_returnsReview() {
        assertThat(policy.evaluate(score(0.4))).isEqualTo(RiskDecision.REVIEW);
    }

    @Test
    void evaluate_atHighRisk_returnsChallenge() {
        assertThat(policy.evaluate(score(0.7))).isEqualTo(RiskDecision.CHALLENGE);
    }

    @Test
    void evaluate_atBlock_returnsBlock() {
        assertThat(policy.evaluate(score(0.9)))
                .isEqualTo(RiskDecision.BLOCK);
    }

    @Test
    void evaluate_zeroScore_returnsAllow() {
        assertThat(policy.evaluate(score(0.0)))
                .isEqualTo(RiskDecision.ALLOW);
    }

    @Test
    void evaluate_terminalRule_alwaysBlocks() {
        var blacklist = FraudRule.RuleResult.triggered(FraudRuleType.BLACKLIST, 1.0, "Blacklisted");
        FraudScore lowScore = new FraudScore(0.1, List.of(blacklist));
        assertThat(policy.evaluate(lowScore)).isEqualTo(RiskDecision.BLOCK);
    }

    @Test
    void evaluate_terminalRuleNotTriggered_usesThresholds() {
        var clean = FraudRule.RuleResult.clean(FraudRuleType.BLACKLIST);
        FraudScore lowScore = new FraudScore(0.1, List.of(clean));
        assertThat(policy.evaluate(lowScore)).isEqualTo(RiskDecision.ALLOW);
    }

    @Test
    void evaluate_customThresholds() {
        RiskDecisionPolicy custom = createPolicy(0.3, 0.5, 0.8);
        assertThat(custom.evaluate(score(0.3))).isEqualTo(RiskDecision.REVIEW);
        assertThat(custom.evaluate(score(0.5))).isEqualTo(RiskDecision.CHALLENGE);
        assertThat(custom.evaluate(score(0.8))).isEqualTo(RiskDecision.BLOCK);
    }

    private static FraudScore score(double value) {
        return new FraudScore(value, List.of());
    }

    private static RiskDecisionPolicy createPolicy(double suspicious, double highRisk, double block) {
        ScoringConfig config = new ScoringConfig();
        ScoringConfig.DecisionThresholds thresholds = new ScoringConfig.DecisionThresholds();
        thresholds.setSuspicious(suspicious);
        thresholds.setHighRisk(highRisk);
        thresholds.setBlock(block);
        config.setThresholds(thresholds);
        return new RiskDecisionPolicy(config);
    }
}
