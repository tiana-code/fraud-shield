package com.fraudshield.scoring;

import com.fraudshield.rules.FraudRuleType;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@Component
@Validated
@ConfigurationProperties(prefix = "fraud-shield.scoring")
public class ScoringConfig {

    @Positive
    private double defaultWeight = 1.0;

    @NotNull
    private Map<FraudRuleType, Double> ruleWeights = new EnumMap<>(Map.of(
            FraudRuleType.BLACKLIST, 3.0,
            FraudRuleType.REPEAT_OFFENDER, 2.5,
            FraudRuleType.VELOCITY, 2.0,
            FraudRuleType.GEO_ANOMALY, 1.5,
            FraudRuleType.CARD_BIN_RISK, 1.5,
            FraudRuleType.AMOUNT_THRESHOLD, 1.2,
            FraudRuleType.DEVICE_FINGERPRINT, 1.0,
            FraudRuleType.TIME_OF_DAY, 0.8
    ));

    @NotNull
    private DecisionThresholds thresholds = new DecisionThresholds();

    @PostConstruct
    void validateThresholdOrdering() {
        if (thresholds.suspicious >= thresholds.highRisk) {
            throw new IllegalStateException(
                    "suspicious threshold (%.2f) must be less than highRisk (%.2f)"
                            .formatted(thresholds.suspicious, thresholds.highRisk));
        }
        if (thresholds.highRisk >= thresholds.block) {
            throw new IllegalStateException(
                    "highRisk threshold (%.2f) must be less than block (%.2f)"
                            .formatted(thresholds.highRisk, thresholds.block));
        }
    }

    public double defaultWeight() {
        return defaultWeight;
    }

    public Map<FraudRuleType, Double> ruleWeights() {
        return Collections.unmodifiableMap(ruleWeights);
    }

    public DecisionThresholds thresholds() {
        return thresholds;
    }

    public void setDefaultWeight(double defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    public void setRuleWeights(Map<FraudRuleType, Double> ruleWeights) {
        this.ruleWeights = new EnumMap<>(ruleWeights);
    }

    public void setThresholds(DecisionThresholds thresholds) {
        this.thresholds = thresholds;
    }

    public static class DecisionThresholds {

        @DecimalMin("0.0")
        @DecimalMax("1.0")
        private double suspicious = 0.4;

        @DecimalMin("0.0")
        @DecimalMax("1.0")
        private double highRisk = 0.7;

        @DecimalMin("0.0")
        @DecimalMax("1.0")
        private double block = 0.9;

        public double suspicious() {
            return suspicious;
        }

        public double highRisk() {
            return highRisk;
        }

        public double block() {
            return block;
        }

        public void setSuspicious(double suspicious) {
            this.suspicious = suspicious;
        }

        public void setHighRisk(double highRisk) {
            this.highRisk = highRisk;
        }

        public void setBlock(double block) {
            this.block = block;
        }
    }
}
