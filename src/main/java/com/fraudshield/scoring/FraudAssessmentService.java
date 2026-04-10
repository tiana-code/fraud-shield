package com.fraudshield.scoring;

import com.fraudshield.dto.request.EvaluateTransactionRequest;
import com.fraudshield.rules.FraudRule;
import com.fraudshield.rules.FraudRuleEngine;
import org.springframework.stereotype.Service;

@Service
public class FraudAssessmentService {

    private final FraudRuleEngine ruleEngine;
    private final RiskDecisionPolicy decisionPolicy;

    public FraudAssessmentService(FraudRuleEngine ruleEngine, RiskDecisionPolicy decisionPolicy) {
        this.ruleEngine = ruleEngine;
        this.decisionPolicy = decisionPolicy;
    }

    public FraudAssessment assess(EvaluateTransactionRequest request) {
        FraudRule.RuleContext context = buildContext(request);
        FraudScore score = ruleEngine.evaluate(request, context);
        RiskDecision decision = decisionPolicy.evaluate(score);
        return new FraudAssessment(request.transactionId(), score, decision);
    }

    private FraudRule.RuleContext buildContext(EvaluateTransactionRequest request) {
        return new FraudRule.RuleContext(0, null, request.deviceId(), false, 0, false, 0.0);
    }
}
