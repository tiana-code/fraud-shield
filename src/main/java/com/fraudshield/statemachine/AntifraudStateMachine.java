package com.fraudshield.statemachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AntifraudStateMachine {

    private static final Map<FraudState, Map<FraudEvent, FraudState>> TRANSITIONS = Map.of(
            FraudState.CLEAN, Map.of(
                    FraudEvent.ANOMALY_DETECTED, FraudState.SUSPICIOUS
            ),
            FraudState.SUSPICIOUS, Map.of(
                    FraudEvent.CHALLENGE_ISSUED, FraudState.CHALLENGE,
                    FraudEvent.AUTO_ESCALATE, FraudState.UNDER_REVIEW,
                    FraudEvent.MANUAL_REVIEW_REQUESTED, FraudState.UNDER_REVIEW
            ),
            FraudState.CHALLENGE, Map.of(
                    FraudEvent.CHALLENGE_PASSED, FraudState.CLEARED,
                    FraudEvent.CHALLENGE_FAILED, FraudState.BLOCKED,
                    FraudEvent.MANUAL_REVIEW_REQUESTED, FraudState.UNDER_REVIEW
            ),
            FraudState.BLOCKED, Map.of(
                    FraudEvent.MANUAL_REVIEW_REQUESTED, FraudState.UNDER_REVIEW
            ),
            FraudState.UNDER_REVIEW, Map.of(
                    FraudEvent.REVIEW_CLEARED, FraudState.CLEARED,
                    FraudEvent.REVIEW_BLOCKED, FraudState.BLOCKED
            ),
            FraudState.CLEARED, Map.of(
                    FraudEvent.ANOMALY_DETECTED, FraudState.SUSPICIOUS,
                    FraudEvent.WHITELIST_APPROVED, FraudState.CLEARED
            )
    );

    private FraudState currentState;
    private final List<StateTransition> history;

    public AntifraudStateMachine() {
        this(FraudState.CLEAN);
    }

    public AntifraudStateMachine(FraudState initialState) {
        Objects.requireNonNull(initialState, "initialState must not be null");
        this.currentState = initialState;
        this.history = new ArrayList<>();
    }

    public StateTransition transition(FraudEvent event, TransitionContext context) {
        Objects.requireNonNull(event, "event must not be null");
        Objects.requireNonNull(context, "context must not be null");

        Map<FraudEvent, FraudState> allowedFromCurrent = TRANSITIONS.getOrDefault(currentState, Map.of());
        FraudState nextState = allowedFromCurrent.get(event);

        if (nextState == null) {
            throw new IllegalStateException(
                    "Invalid transition: %s -[%s]-> ?. Allowed events in state %s: %s"
                            .formatted(currentState, event, currentState, allowedFromCurrent.keySet())
            );
        }

        StateTransition transition = new StateTransition(currentState, event, nextState, context);
        history.add(transition);
        currentState = nextState;
        return transition;
    }

    public FraudState currentState() {
        return currentState;
    }

    public List<StateTransition> history() {
        return List.copyOf(history);
    }

    public boolean canTransition(FraudEvent event) {
        return TRANSITIONS.getOrDefault(currentState, Map.of()).containsKey(event);
    }
}
