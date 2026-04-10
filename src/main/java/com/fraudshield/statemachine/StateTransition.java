package com.fraudshield.statemachine;

import java.util.Objects;

public record StateTransition(
        FraudState fromState,
        FraudEvent event,
        FraudState toState,
        TransitionContext context
) {
    public StateTransition {
        Objects.requireNonNull(fromState, "fromState must not be null");
        Objects.requireNonNull(event, "event must not be null");
        Objects.requireNonNull(toState, "toState must not be null");
        Objects.requireNonNull(context, "context must not be null");
    }
}
