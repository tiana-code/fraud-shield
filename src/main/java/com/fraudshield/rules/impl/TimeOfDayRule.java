package com.fraudshield.rules.impl;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneOffset;

@Component
public class TimeOfDayRule implements FraudRule {

    private static final LocalTime SUSPICIOUS_START = LocalTime.of(1, 0);
    private static final LocalTime SUSPICIOUS_END = LocalTime.of(5, 0);

    @Override
    public FraudRuleType type() {
        return FraudRuleType.TIME_OF_DAY;
    }

    @Override
    public RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context) {
        LocalTime txTime = request.transactionTime()
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalTime();

        if (!txTime.isBefore(SUSPICIOUS_START) && txTime.isBefore(SUSPICIOUS_END)) {
            return RuleResult.triggered(type(), 0.45,
                    "Transaction at suspicious hour: %s UTC".formatted(txTime));
        }
        return RuleResult.clean(type());
    }
}
