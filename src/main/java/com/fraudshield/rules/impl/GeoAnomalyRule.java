package com.fraudshield.rules.impl;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.springframework.stereotype.Component;

@Component
public class GeoAnomalyRule implements FraudRule {

    @Override
    public FraudRuleType type() {
        return FraudRuleType.GEO_ANOMALY;
    }

    @Override
    public RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context) {
        String lastCountry = context.lastKnownCountry();
        String currentCountry = request.country();

        if (lastCountry == null || currentCountry == null) {
            return RuleResult.clean(type());
        }
        if (!lastCountry.equalsIgnoreCase(currentCountry)) {
            return RuleResult.triggered(type(), 0.75,
                    "Country changed from %s to %s".formatted(lastCountry, currentCountry));
        }
        return RuleResult.clean(type());
    }
}
