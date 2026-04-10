package com.fraudshield.scoring;

import com.fraudshield.rules.FraudRule;

import java.util.List;
import java.util.Objects;

public record FraudScore(
        double score,
        List<FraudRule.RuleResult> ruleResults
) {
    public FraudScore {
        if (Double.isNaN(score) || Double.isInfinite(score)) {
            throw new IllegalArgumentException("score must be a finite number");
        }
        if (score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException("score must be in [0.0, 1.0], got: " + score);
        }
        Objects.requireNonNull(ruleResults, "ruleResults must not be null");
        ruleResults = List.copyOf(ruleResults);
    }

    public List<String> reasons() {
        return ruleResults.stream()
                .filter(FraudRule.RuleResult::triggered)
                .map(FraudRule.RuleResult::reason)
                .toList();
    }

    public boolean hasTerminalTrigger() {
        return ruleResults.stream()
                .anyMatch(r -> r.triggered() && r.ruleType().isTerminal());
    }
}
