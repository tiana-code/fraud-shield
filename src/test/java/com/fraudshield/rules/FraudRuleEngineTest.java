package com.fraudshield.rules;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.scoring.FraudScore;
import com.fraudshield.scoring.ScoringConfig;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FraudRuleEngineTest {

    @Test
    void evaluate_singleTriggeredRule_returnsCorrectScore() {
        FraudRule alwaysTrigger = stubRule(FraudRuleType.AMOUNT_THRESHOLD, 0.8, true);
        ScoringConfig config = configWithWeights(Map.of(FraudRuleType.AMOUNT_THRESHOLD, 2.0));

        FraudRuleEngine engine = new FraudRuleEngine(List.of(alwaysTrigger), config);
        FraudScore score = engine.evaluate(TestData.request(), TestData.context());

        assertThat(score.score()).isEqualTo(0.8);
    }

    @Test
    void evaluate_noTriggeredRules_returnsZero() {
        FraudRule clean = stubRule(FraudRuleType.VELOCITY, 0.0, false);
        ScoringConfig config = configWithWeights(Map.of(FraudRuleType.VELOCITY, 1.0));

        FraudRuleEngine engine = new FraudRuleEngine(List.of(clean), config);
        FraudScore score = engine.evaluate(TestData.request(), TestData.context());

        assertThat(score.score()).isZero();
    }

    @Test
    void evaluate_blacklistOnly_returnsFullScore() {
        FraudRule blacklist = stubRule(FraudRuleType.BLACKLIST, 1.0, true);
        FraudRule clean1 = stubRule(FraudRuleType.VELOCITY, 0.0, false);
        FraudRule clean2 = stubRule(FraudRuleType.GEO_ANOMALY, 0.0, false);
        ScoringConfig config = configWithWeights(Map.of(
                FraudRuleType.BLACKLIST, 3.0,
                FraudRuleType.VELOCITY, 2.0,
                FraudRuleType.GEO_ANOMALY, 1.5));

        FraudRuleEngine engine = new FraudRuleEngine(List.of(blacklist, clean1, clean2), config);
        FraudScore score = engine.evaluate(TestData.request(), TestData.context());

        assertThat(score.score()).isEqualTo(1.0);
    }

    @Test
    void evaluate_multipleTriggered_aggregatesCorrectly() {
        FraudRule rule1 = stubRule(FraudRuleType.BLACKLIST, 0.8, true);
        FraudRule rule2 = stubRule(FraudRuleType.VELOCITY, 0.6, true);
        FraudRule rule3 = stubRule(FraudRuleType.GEO_ANOMALY, 0.0, false);
        ScoringConfig config = configWithWeights(Map.of(
                FraudRuleType.BLACKLIST, 2.0,
                FraudRuleType.VELOCITY, 1.0,
                FraudRuleType.GEO_ANOMALY, 1.0));

        FraudRuleEngine engine = new FraudRuleEngine(List.of(rule1, rule2, rule3), config);
        FraudScore score = engine.evaluate(TestData.request(), TestData.context());

        double expected = (0.8 * 2.0 + 0.6 * 1.0) / (2.0 + 1.0);
        assertThat(score.score()).isCloseTo(expected, org.assertj.core.data.Offset.offset(0.001));
    }

    @Test
    void evaluate_returnsTriggeredReasons() {
        FraudRule triggered = stubRule(FraudRuleType.BLACKLIST, 0.5, true);
        FraudRule clean = stubRule(FraudRuleType.VELOCITY, 0.0, false);
        ScoringConfig config = configWithWeights(Map.of(
                FraudRuleType.BLACKLIST, 1.0,
                FraudRuleType.VELOCITY, 1.0));

        FraudRuleEngine engine = new FraudRuleEngine(List.of(triggered, clean), config);
        FraudScore score = engine.evaluate(TestData.request(), TestData.context());

        assertThat(score.reasons()).hasSize(1);
        assertThat(score.reasons().getFirst()).contains("Triggered");
    }

    private static FraudRule stubRule(FraudRuleType ruleType, double score, boolean triggered) {
        return new FraudRule() {
            @Override
            public FraudRuleType type() {
                return ruleType;
            }

            @Override
            public RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context) {
                return triggered
                        ? RuleResult.triggered(ruleType, score, "Triggered: " + ruleType)
                        : RuleResult.clean(ruleType);
            }
        };
    }

    private static ScoringConfig configWithWeights(Map<FraudRuleType, Double> weights) {
        ScoringConfig config = new ScoringConfig();
        config.setDefaultWeight(1.0);
        config.setRuleWeights(new EnumMap<>(weights));
        return config;
    }
}
