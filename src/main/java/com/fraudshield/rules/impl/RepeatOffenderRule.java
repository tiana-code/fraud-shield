package com.fraudshield.rules.impl;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.springframework.stereotype.Component;

@Component
public class RepeatOffenderRule implements FraudRule {

    private static final int HIGH_RISK_THRESHOLD = 3;
    private static final int MEDIUM_RISK_THRESHOLD = 1;

    @Override
    public FraudRuleType type() {
        return FraudRuleType.REPEAT_OFFENDER;
    }

    @Override
    public RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context) {
        int count = context.previousFraudCount();

        if (count >= HIGH_RISK_THRESHOLD) {
            return RuleResult.triggered(type(), 0.95,
                    "Entity has %d prior fraud incidents".formatted(count));
        }
        if (count >= MEDIUM_RISK_THRESHOLD) {
            return RuleResult.triggered(type(), 0.7,
                    "Entity has %d prior fraud incident".formatted(count));
        }
        return RuleResult.clean(type());
    }
}
