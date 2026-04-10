package com.fraudshield.rules;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.scoring.FraudScore;
import com.fraudshield.scoring.ScoringConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FraudRuleEngine {

    private final List<FraudRule> rules;
    private final ScoringConfig scoringConfig;

    public FraudRuleEngine(List<FraudRule> rules, ScoringConfig scoringConfig) {
        this.rules = rules;
        this.scoringConfig = scoringConfig;
    }

    public FraudScore evaluate(EvaluateTransactionRequest request, FraudRule.RuleContext context) {
        Map<FraudRuleType, Double> weights = scoringConfig.ruleWeights();
        List<FraudRule.RuleResult> results = new ArrayList<>(rules.size());
        double weightedSum = 0.0;
        double totalWeight = 0.0;

        for (FraudRule rule : rules) {
            FraudRule.RuleResult result = rule.evaluate(request, context);
            results.add(result);
            double weight = weights.getOrDefault(result.ruleType(), scoringConfig.defaultWeight());
            weightedSum += result.score() * weight;
            if (result.triggered()) {
                totalWeight += weight;
            }
        }

        double compositeScore = totalWeight == 0.0 ? 0.0 : Math.min(1.0, weightedSum / totalWeight);
        return new FraudScore(compositeScore, results);
    }
}
