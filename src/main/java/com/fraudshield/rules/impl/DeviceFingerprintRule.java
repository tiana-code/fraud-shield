package com.fraudshield.rules.impl;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleType;
import org.springframework.stereotype.Component;

@Component
public class DeviceFingerprintRule implements FraudRule {

    @Override
    public FraudRuleType type() {
        return FraudRuleType.DEVICE_FINGERPRINT;
    }

    @Override
    public RuleResult evaluate(EvaluateTransactionRequest request, RuleContext context) {
        if (request.deviceId() == null) {
            return RuleResult.triggered(type(), 0.4, "No device fingerprint present");
        }
        if (!context.deviceSeen()) {
            return RuleResult.triggered(type(), 0.6, "Unrecognized device: %s".formatted(request.deviceId()));
        }
        return RuleResult.clean(type());
    }
}
