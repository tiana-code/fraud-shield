package com.fraudshield.statemachine;

public enum FraudState {
    CLEAN,
    SUSPICIOUS,
    CHALLENGE,
    BLOCKED,
    UNDER_REVIEW,
    CLEARED
}
