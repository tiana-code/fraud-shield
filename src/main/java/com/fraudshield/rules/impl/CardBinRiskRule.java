package com.fraudshield.rules.impl;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.springframework.stereotype.Component;

@Component
public class CardBinRiskRule implements FraudRule {

    private static final double HIGH_RISK_BIN_THRESHOLD = 0.7;
    private static final double MEDIUM_RISK_BIN_THRESHOLD = 0.4;

    @Override
    public FraudRuleType type() {
        return FraudRuleType.CARD_BIN_RISK;
    }

    @Override
    public RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context) {
        double binRisk = context.cardBinRiskScore();

        if (binRisk >= HIGH_RISK_BIN_THRESHOLD) {
            return RuleResult.triggered(type(), binRisk,
                    "Card BIN has high fraud history: risk=%.2f".formatted(binRisk));
        }
        if (binRisk >= MEDIUM_RISK_BIN_THRESHOLD) {
            return RuleResult.triggered(type(), binRisk,
                    "Card BIN has elevated fraud history: risk=%.2f".formatted(binRisk));
        }
        return RuleResult.clean(type());
    }
}
