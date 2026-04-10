package com.fraudshield.rules.impl;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.springframework.stereotype.Component;

@Component
public class VelocityRule implements FraudRule {

    private static final int HIGH_VELOCITY_THRESHOLD = 10;
    private static final int MEDIUM_VELOCITY_THRESHOLD = 5;

    @Override
    public FraudRuleType type() {
        return FraudRuleType.VELOCITY;
    }

    @Override
    public RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context) {
        int count = context.recentTransactionCount();

        if (count >= HIGH_VELOCITY_THRESHOLD) {
            return RuleResult.triggered(type(), 0.9,
                    "High transaction velocity: %d transactions in window".formatted(count));
        }
        if (count >= MEDIUM_VELOCITY_THRESHOLD) {
            return RuleResult.triggered(type(), 0.5,
                    "Elevated transaction velocity: %d transactions in window".formatted(count));
        }
        return RuleResult.clean(type());
    }
}
