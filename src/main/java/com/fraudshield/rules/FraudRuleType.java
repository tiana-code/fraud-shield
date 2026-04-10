package com.fraudshield.rules;

public enum FraudRuleType {
    BLACKLIST(true),
    REPEAT_OFFENDER(false),
    VELOCITY(false),
    GEO_ANOMALY(false),
    CARD_BIN_RISK(false),
    AMOUNT_THRESHOLD(false),
    DEVICE_FINGERPRINT(false),
    TIME_OF_DAY(false);

    private final boolean terminal;

    FraudRuleType(boolean terminal) {
        this.terminal = terminal;
    }

    public boolean isTerminal() {
        return terminal;
    }
}
