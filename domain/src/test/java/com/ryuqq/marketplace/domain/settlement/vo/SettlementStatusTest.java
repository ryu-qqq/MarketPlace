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
        @DisplayName("PENDING에서 COMPLETED로 전이할 수 있다")
        void pendingToCompleted() {
            assertThat(SettlementStatus.PENDING.canTransitionTo(SettlementStatus.COMPLETED))
                    .isTrue();
        }

        @Test
        @DisplayName("PENDING에서 HOLD로 전이할 수 있다")
        void pendingToHold() {
            assertThat(SettlementStatus.PENDING.canTransitionTo(SettlementStatus.HOLD)).isTrue();
        }

        @Test
        @DisplayName("HOLD에서 PENDING으로 전이할 수 있다")
        void holdToPending() {
            assertThat(SettlementStatus.HOLD.canTransitionTo(SettlementStatus.PENDING)).isTrue();
        }
    }

    @Nested
    @DisplayName("canTransitionTo() - 불허 전이 검증")
    class DisallowedTransitionTest {

        @Test
        @DisplayName("PENDING에서 PENDING으로 전이할 수 없다")
        void pendingToPending() {
            assertThat(SettlementStatus.PENDING.canTransitionTo(SettlementStatus.PENDING))
                    .isFalse();
        }

        @Test
        @DisplayName("HOLD에서 COMPLETED로 직접 전이할 수 없다")
        void holdToCompleted() {
            assertThat(SettlementStatus.HOLD.canTransitionTo(SettlementStatus.COMPLETED)).isFalse();
        }

        @Test
        @DisplayName("HOLD에서 HOLD로 전이할 수 없다")
        void holdToHold() {
            assertThat(SettlementStatus.HOLD.canTransitionTo(SettlementStatus.HOLD)).isFalse();
        }

        @Test
        @DisplayName("COMPLETED에서 PENDING으로 전이할 수 없다")
        void completedToPending() {
            assertThat(SettlementStatus.COMPLETED.canTransitionTo(SettlementStatus.PENDING))
                    .isFalse();
        }

        @Test
        @DisplayName("COMPLETED에서 HOLD로 전이할 수 없다")
        void completedToHold() {
            assertThat(SettlementStatus.COMPLETED.canTransitionTo(SettlementStatus.HOLD)).isFalse();
        }

        @Test
        @DisplayName("COMPLETED에서 COMPLETED로 전이할 수 없다")
        void completedToCompleted() {
            assertThat(SettlementStatus.COMPLETED.canTransitionTo(SettlementStatus.COMPLETED))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("SettlementStatus는 PENDING, HOLD, COMPLETED 세 가지 값을 가진다")
        void statusValues() {
            SettlementStatus[] values = SettlementStatus.values();
            assertThat(values)
                    .containsExactlyInAnyOrder(
                            SettlementStatus.PENDING,
                            SettlementStatus.HOLD,
                            SettlementStatus.COMPLETED);
        }
    }
}
