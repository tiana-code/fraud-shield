package com.fraudshield.rules.impl;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AmountThresholdRule implements FraudRule {

    private static final BigDecimal HIGH_RISK_THRESHOLD = new BigDecimal("10000");
    private static final BigDecimal MEDIUM_RISK_THRESHOLD = new BigDecimal("3000");

    @Override
    public FraudRuleType type() {
        return FraudRuleType.AMOUNT_THRESHOLD;
    }

    @Override
    public RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context) {
        BigDecimal amount = request.amount();

        if (amount.compareTo(HIGH_RISK_THRESHOLD) > 0) {
            return RuleResult.triggered(type(), 0.8,
                    "Transaction amount %s exceeds high-risk threshold".formatted(amount.toPlainString()));
        }
        if (amount.compareTo(MEDIUM_RISK_THRESHOLD) > 0) {
            return RuleResult.triggered(type(), 0.4,
                    "Transaction amount %s exceeds medium-risk threshold".formatted(amount.toPlainString()));
        }
        return RuleResult.clean(type());
    }
}
