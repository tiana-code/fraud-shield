package com.fraudshield.rules.impl;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.springframework.stereotype.Component;

@Component
public class BlacklistRule implements FraudRule {

    @Override
    public FraudRuleType type() {
        return FraudRuleType.BLACKLIST;
    }

    @Override
    public RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context) {
        if (context.isBlacklisted()) {
            return RuleResult.triggered(type(), 1.0, "Entity is on the blacklist");
        }
        return RuleResult.clean(type());
    }
}
