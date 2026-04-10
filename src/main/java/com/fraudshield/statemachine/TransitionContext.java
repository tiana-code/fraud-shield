package com.fraudshield.statemachine;

import java.time.Instant;
import java.util.Objects;

public record TransitionContext(
        String reasonCode,
        String description,
        Instant occurredAt
) {
    public TransitionContext {
        Objects.requireNonNull(reasonCode, "reasonCode must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (reasonCode.isBlank()) {
            throw new IllegalArgumentException("reasonCode must not be blank");
        }
    }

    public static TransitionContext of(String reasonCode, String description, Instant occurredAt) {
        return new TransitionContext(reasonCode, description, occurredAt);
    }
}
