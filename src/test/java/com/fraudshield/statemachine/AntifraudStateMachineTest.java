package com.fraudshield.statemachine;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AntifraudStateMachineTest {

    private static final Instant NOW = Instant.parse("2026-04-10T12:00:00Z");

    private static TransitionContext ctx(String reasonCode, String description) {
        return TransitionContext.of(reasonCode, description, NOW);
    }

    @Test
    void initialState_isClean() {
        var stateMachine = new AntifraudStateMachine();
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.CLEAN);
    }

    @Test
    void customInitialState() {
        var stateMachine = new AntifraudStateMachine(FraudState.BLOCKED);
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.BLOCKED);
    }

    @Test
    void constructor_nullInitialState_throwsNPE() {
        assertThatThrownBy(() -> new AntifraudStateMachine(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void transition_cleanToSuspicious() {
        var stateMachine = new AntifraudStateMachine();
        StateTransition transition = stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("HIGH_SCORE", "High score"));
        assertThat(transition.fromState()).isEqualTo(FraudState.CLEAN);
        assertThat(transition.toState()).isEqualTo(FraudState.SUSPICIOUS);
        assertThat(transition.context().occurredAt()).isEqualTo(NOW);
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.SUSPICIOUS);
    }

    @Test
    void transition_suspiciousToChallenge() {
        var stateMachine = new AntifraudStateMachine();
        stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("SCORE_EXCEEDED", "Score exceeded"));
        stateMachine.transition(FraudEvent.CHALLENGE_ISSUED, ctx("CHALLENGE", "Sending challenge"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.CHALLENGE);
    }

    @Test
    void transition_suspiciousToUnderReview_viaManualReview() {
        var stateMachine = new AntifraudStateMachine();
        stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("SCORE_EXCEEDED", "Score exceeded"));
        stateMachine.transition(FraudEvent.MANUAL_REVIEW_REQUESTED, ctx("AGENT_REQUEST", "Agent requested"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.UNDER_REVIEW);
    }

    @Test
    void transition_suspiciousToUnderReview_viaAutoEscalate() {
        var stateMachine = new AntifraudStateMachine();
        stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("SCORE_EXCEEDED", "Score exceeded"));
        stateMachine.transition(FraudEvent.AUTO_ESCALATE, ctx("THRESHOLD_BREACH", "Threshold breached"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.UNDER_REVIEW);
    }

    @Test
    void transition_challengeToCleared() {
        var stateMachine = new AntifraudStateMachine();
        stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("ANOMALY", "Anomaly"));
        stateMachine.transition(FraudEvent.CHALLENGE_ISSUED, ctx("CHALLENGE", "Challenge"));
        stateMachine.transition(FraudEvent.CHALLENGE_PASSED, ctx("VERIFIED", "Verified"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.CLEARED);
    }

    @Test
    void transition_challengeToBlocked() {
        var stateMachine = new AntifraudStateMachine();
        stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("ANOMALY", "Anomaly"));
        stateMachine.transition(FraudEvent.CHALLENGE_ISSUED, ctx("CHALLENGE", "Challenge"));
        stateMachine.transition(FraudEvent.CHALLENGE_FAILED, ctx("FAILED", "Failed verification"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.BLOCKED);
    }

    @Test
    void transition_blockedToUnderReview() {
        var stateMachine = new AntifraudStateMachine(FraudState.BLOCKED);
        stateMachine.transition(FraudEvent.MANUAL_REVIEW_REQUESTED, ctx("APPEAL", "Appeal filed"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.UNDER_REVIEW);
    }

    @Test
    void transition_underReviewToCleared() {
        var stateMachine = new AntifraudStateMachine(FraudState.UNDER_REVIEW);
        stateMachine.transition(FraudEvent.REVIEW_CLEARED, ctx("ANALYST_CLEARED", "Analyst cleared"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.CLEARED);
    }

    @Test
    void transition_underReviewToBlocked() {
        var stateMachine = new AntifraudStateMachine(FraudState.UNDER_REVIEW);
        stateMachine.transition(FraudEvent.REVIEW_BLOCKED, ctx("FRAUD_CONFIRMED", "Analyst confirmed fraud"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.BLOCKED);
    }

    @Test
    void transition_clearedBackToSuspicious() {
        var stateMachine = new AntifraudStateMachine(FraudState.CLEARED);
        stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("NEW_ANOMALY", "New anomaly"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.SUSPICIOUS);
    }

    @Test
    void transition_clearedWhitelistApproved_staysCleared() {
        var stateMachine = new AntifraudStateMachine(FraudState.CLEARED);
        stateMachine.transition(FraudEvent.WHITELIST_APPROVED, ctx("WHITELIST", "Whitelisted"));
        assertThat(stateMachine.currentState()).isEqualTo(FraudState.CLEARED);
    }

    @Test
    void transition_invalid_throwsIllegalState() {
        var stateMachine = new AntifraudStateMachine();
        assertThatThrownBy(() -> stateMachine.transition(FraudEvent.CHALLENGE_ISSUED, ctx("INVALID", "Invalid")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid transition");
    }

    @Test
    void transition_nullEvent_throwsNPE() {
        var stateMachine = new AntifraudStateMachine();
        assertThatThrownBy(() -> stateMachine.transition(null, ctx("TEST", "Test")))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void transition_nullContext_throwsNPE() {
        var stateMachine = new AntifraudStateMachine();
        assertThatThrownBy(() -> stateMachine.transition(FraudEvent.ANOMALY_DETECTED, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void history_recordsAllTransitions() {
        var stateMachine = new AntifraudStateMachine();
        stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("STEP1", "Step 1"));
        stateMachine.transition(FraudEvent.CHALLENGE_ISSUED, ctx("STEP2", "Step 2"));

        List<StateTransition> history = stateMachine.history();
        assertThat(history).hasSize(2);
        assertThat(history.get(0).event()).isEqualTo(FraudEvent.ANOMALY_DETECTED);
        assertThat(history.get(1).event()).isEqualTo(FraudEvent.CHALLENGE_ISSUED);
    }

    @Test
    void history_returnsDefensiveCopy() {
        var stateMachine = new AntifraudStateMachine();
        stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("STEP1", "Step 1"));
        List<StateTransition> history = stateMachine.history();
        assertThatThrownBy(() -> history.add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void history_transitionContextPreservesTimestamp() {
        var stateMachine = new AntifraudStateMachine();
        StateTransition transition = stateMachine.transition(FraudEvent.ANOMALY_DETECTED, ctx("TEST", "Test"));
        assertThat(transition.context().occurredAt()).isEqualTo(NOW);
    }

    @Test
    void canTransition_validEvent_returnsTrue() {
        var stateMachine = new AntifraudStateMachine();
        assertThat(stateMachine.canTransition(FraudEvent.ANOMALY_DETECTED)).isTrue();
    }

    @Test
    void canTransition_invalidEvent_returnsFalse() {
        var stateMachine = new AntifraudStateMachine();
        assertThat(stateMachine.canTransition(FraudEvent.CHALLENGE_ISSUED)).isFalse();
    }

    @Test
    void singleSourceOfTruth_canTransitionMatchesTransition() {
        var stateMachine = new AntifraudStateMachine(FraudState.SUSPICIOUS);
        for (FraudEvent event : FraudEvent.values()) {
            boolean canTransition = stateMachine.canTransition(event);
            var probe = new AntifraudStateMachine(FraudState.SUSPICIOUS);
            try {
                probe.transition(event, ctx("PROBE", "Probe"));
                assertThat(canTransition)
                        .as("canTransition(%s) should be true when transition succeeds", event)
                        .isTrue();
            } catch (IllegalStateException e) {
                assertThat(canTransition)
                        .as("canTransition(%s) should be false when transition throws", event)
                        .isFalse();
            }
        }
    }
}
