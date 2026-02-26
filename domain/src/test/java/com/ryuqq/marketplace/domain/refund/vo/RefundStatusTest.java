package com.ryuqq.marketplace.domain.refund.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundStatus 상태 전이 테스트")
class RefundStatusTest {

    @Nested
    @DisplayName("REQUESTED 상태 전이 규칙")
    class FromRequestedTest {

        @Test
        @DisplayName("REQUESTED에서 COLLECTING으로 전이할 수 있다")
        void requestedToCollecting() {
            assertThat(RefundStatus.REQUESTED.canTransitionTo(RefundStatus.COLLECTING)).isTrue();
        }

        @Test
        @DisplayName("REQUESTED에서 REJECTED로 전이할 수 있다")
        void requestedToRejected() {
            assertThat(RefundStatus.REQUESTED.canTransitionTo(RefundStatus.REJECTED)).isTrue();
        }

        @Test
        @DisplayName("REQUESTED에서 CANCELLED로 전이할 수 있다")
        void requestedToCancelled() {
            assertThat(RefundStatus.REQUESTED.canTransitionTo(RefundStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("REQUESTED에서 COLLECTED로 전이할 수 없다")
        void requestedToCollected_NotAllowed() {
            assertThat(RefundStatus.REQUESTED.canTransitionTo(RefundStatus.COLLECTED)).isFalse();
        }

        @Test
        @DisplayName("REQUESTED에서 COMPLETED로 전이할 수 없다")
        void requestedToCompleted_NotAllowed() {
            assertThat(RefundStatus.REQUESTED.canTransitionTo(RefundStatus.COMPLETED)).isFalse();
        }

        @Test
        @DisplayName("REQUESTED에서 REQUESTED로 전이할 수 없다")
        void requestedToRequested_NotAllowed() {
            assertThat(RefundStatus.REQUESTED.canTransitionTo(RefundStatus.REQUESTED)).isFalse();
        }
    }

    @Nested
    @DisplayName("COLLECTING 상태 전이 규칙")
    class FromCollectingTest {

        @Test
        @DisplayName("COLLECTING에서 COLLECTED로 전이할 수 있다")
        void collectingToCollected() {
            assertThat(RefundStatus.COLLECTING.canTransitionTo(RefundStatus.COLLECTED)).isTrue();
        }

        @Test
        @DisplayName("COLLECTING에서 REJECTED로 전이할 수 있다")
        void collectingToRejected() {
            assertThat(RefundStatus.COLLECTING.canTransitionTo(RefundStatus.REJECTED)).isTrue();
        }

        @Test
        @DisplayName("COLLECTING에서 CANCELLED로 전이할 수 있다")
        void collectingToCancelled() {
            assertThat(RefundStatus.COLLECTING.canTransitionTo(RefundStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("COLLECTING에서 REQUESTED로 전이할 수 없다")
        void collectingToRequested_NotAllowed() {
            assertThat(RefundStatus.COLLECTING.canTransitionTo(RefundStatus.REQUESTED)).isFalse();
        }

        @Test
        @DisplayName("COLLECTING에서 COMPLETED로 전이할 수 없다")
        void collectingToCompleted_NotAllowed() {
            assertThat(RefundStatus.COLLECTING.canTransitionTo(RefundStatus.COMPLETED)).isFalse();
        }
    }

    @Nested
    @DisplayName("COLLECTED 상태 전이 규칙")
    class FromCollectedTest {

        @Test
        @DisplayName("COLLECTED에서 COMPLETED로 전이할 수 있다")
        void collectedToCompleted() {
            assertThat(RefundStatus.COLLECTED.canTransitionTo(RefundStatus.COMPLETED)).isTrue();
        }

        @Test
        @DisplayName("COLLECTED에서 REJECTED로 전이할 수 있다")
        void collectedToRejected() {
            assertThat(RefundStatus.COLLECTED.canTransitionTo(RefundStatus.REJECTED)).isTrue();
        }

        @Test
        @DisplayName("COLLECTED에서 CANCELLED로 전이할 수 없다")
        void collectedToCancelled_NotAllowed() {
            assertThat(RefundStatus.COLLECTED.canTransitionTo(RefundStatus.CANCELLED)).isFalse();
        }

        @Test
        @DisplayName("COLLECTED에서 COLLECTING으로 전이할 수 없다")
        void collectedToCollecting_NotAllowed() {
            assertThat(RefundStatus.COLLECTED.canTransitionTo(RefundStatus.COLLECTING)).isFalse();
        }
    }

    @Nested
    @DisplayName("종료 상태 전이 규칙")
    class FromTerminalStatusTest {

        @Test
        @DisplayName("COMPLETED에서 어떤 상태로도 전이할 수 없다")
        void completedToAny_NotAllowed() {
            for (RefundStatus target : RefundStatus.values()) {
                assertThat(RefundStatus.COMPLETED.canTransitionTo(target)).isFalse();
            }
        }

        @Test
        @DisplayName("REJECTED에서 어떤 상태로도 전이할 수 없다")
        void rejectedToAny_NotAllowed() {
            for (RefundStatus target : RefundStatus.values()) {
                assertThat(RefundStatus.REJECTED.canTransitionTo(target)).isFalse();
            }
        }

        @Test
        @DisplayName("CANCELLED에서 어떤 상태로도 전이할 수 없다")
        void cancelledToAny_NotAllowed() {
            for (RefundStatus target : RefundStatus.values()) {
                assertThat(RefundStatus.CANCELLED.canTransitionTo(target)).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("6개의 상태 값이 존재한다")
        void allStatusesExist() {
            assertThat(RefundStatus.values()).hasSize(6);
        }

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allStatusValuesPresent() {
            assertThat(RefundStatus.values())
                    .containsExactly(
                            RefundStatus.REQUESTED,
                            RefundStatus.COLLECTING,
                            RefundStatus.COLLECTED,
                            RefundStatus.COMPLETED,
                            RefundStatus.REJECTED,
                            RefundStatus.CANCELLED);
        }
    }
}
