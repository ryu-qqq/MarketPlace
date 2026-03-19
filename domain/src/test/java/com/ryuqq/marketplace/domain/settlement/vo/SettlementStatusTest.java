package com.ryuqq.marketplace.domain.settlement.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementStatus 상태 전이 테스트")
class SettlementStatusTest {

    @Nested
    @DisplayName("canTransitionTo() - 허용 전이 검증")
    class AllowedTransitionTest {

        @Test
        @DisplayName("CALCULATING → CONFIRMED 전이 가능")
        void calculatingToConfirmed() {
            assertThat(SettlementStatus.CALCULATING.canTransitionTo(SettlementStatus.CONFIRMED))
                    .isTrue();
        }

        @Test
        @DisplayName("CONFIRMED → PAYOUT_REQUESTED 전이 가능")
        void confirmedToPayoutRequested() {
            assertThat(
                            SettlementStatus.CONFIRMED.canTransitionTo(
                                    SettlementStatus.PAYOUT_REQUESTED))
                    .isTrue();
        }

        @Test
        @DisplayName("PAYOUT_REQUESTED → COMPLETED 전이 가능")
        void payoutRequestedToCompleted() {
            assertThat(
                            SettlementStatus.PAYOUT_REQUESTED.canTransitionTo(
                                    SettlementStatus.COMPLETED))
                    .isTrue();
        }

        @Test
        @DisplayName("CALCULATING → HOLD 전이 가능")
        void calculatingToHold() {
            assertThat(SettlementStatus.CALCULATING.canTransitionTo(SettlementStatus.HOLD))
                    .isTrue();
        }

        @Test
        @DisplayName("CONFIRMED → HOLD 전이 가능")
        void confirmedToHold() {
            assertThat(SettlementStatus.CONFIRMED.canTransitionTo(SettlementStatus.HOLD)).isTrue();
        }

        @Test
        @DisplayName("HOLD → CALCULATING 전이 가능")
        void holdToCalculating() {
            assertThat(SettlementStatus.HOLD.canTransitionTo(SettlementStatus.CALCULATING))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("canTransitionTo() - 불허 전이 검증")
    class DisallowedTransitionTest {

        @Test
        @DisplayName("HOLD → COMPLETED 직접 전이 불가")
        void holdToCompleted() {
            assertThat(SettlementStatus.HOLD.canTransitionTo(SettlementStatus.COMPLETED)).isFalse();
        }

        @Test
        @DisplayName("COMPLETED는 terminal state")
        void completedIsTerminal() {
            assertThat(SettlementStatus.COMPLETED.canTransitionTo(SettlementStatus.CALCULATING))
                    .isFalse();
            assertThat(SettlementStatus.COMPLETED.canTransitionTo(SettlementStatus.CONFIRMED))
                    .isFalse();
            assertThat(SettlementStatus.COMPLETED.canTransitionTo(SettlementStatus.HOLD)).isFalse();
        }

        @Test
        @DisplayName("CALCULATING → COMPLETED 직접 전이 불가")
        void calculatingToCompletedDirectly() {
            assertThat(SettlementStatus.CALCULATING.canTransitionTo(SettlementStatus.COMPLETED))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("SettlementStatus는 5가지 값")
        void statusValues() {
            SettlementStatus[] values = SettlementStatus.values();
            assertThat(values)
                    .containsExactlyInAnyOrder(
                            SettlementStatus.CALCULATING,
                            SettlementStatus.CONFIRMED,
                            SettlementStatus.PAYOUT_REQUESTED,
                            SettlementStatus.COMPLETED,
                            SettlementStatus.HOLD);
        }
    }
}
