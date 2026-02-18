package com.ryuqq.marketplace.domain.cancel.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelStatus 상태 전이 테스트")
class CancelStatusTest {

    @Nested
    @DisplayName("canTransitionTo() - REQUESTED 상태에서 전이")
    class FromRequestedTest {

        @Test
        @DisplayName("REQUESTED에서 APPROVED로 전이할 수 있다")
        void requestedToApproved() {
            assertThat(CancelStatus.REQUESTED.canTransitionTo(CancelStatus.APPROVED)).isTrue();
        }

        @Test
        @DisplayName("REQUESTED에서 REJECTED로 전이할 수 있다")
        void requestedToRejected() {
            assertThat(CancelStatus.REQUESTED.canTransitionTo(CancelStatus.REJECTED)).isTrue();
        }

        @Test
        @DisplayName("REQUESTED에서 CANCELLED로 전이할 수 있다")
        void requestedToCancelled() {
            assertThat(CancelStatus.REQUESTED.canTransitionTo(CancelStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("REQUESTED에서 COMPLETED로 전이할 수 없다")
        void requestedToCompleted_Fail() {
            assertThat(CancelStatus.REQUESTED.canTransitionTo(CancelStatus.COMPLETED)).isFalse();
        }

        @Test
        @DisplayName("REQUESTED에서 REQUESTED로 전이할 수 없다")
        void requestedToRequested_Fail() {
            assertThat(CancelStatus.REQUESTED.canTransitionTo(CancelStatus.REQUESTED)).isFalse();
        }
    }

    @Nested
    @DisplayName("canTransitionTo() - APPROVED 상태에서 전이")
    class FromApprovedTest {

        @Test
        @DisplayName("APPROVED에서 COMPLETED로 전이할 수 있다")
        void approvedToCompleted() {
            assertThat(CancelStatus.APPROVED.canTransitionTo(CancelStatus.COMPLETED)).isTrue();
        }

        @Test
        @DisplayName("APPROVED에서 REQUESTED로 전이할 수 없다")
        void approvedToRequested_Fail() {
            assertThat(CancelStatus.APPROVED.canTransitionTo(CancelStatus.REQUESTED)).isFalse();
        }

        @Test
        @DisplayName("APPROVED에서 REJECTED로 전이할 수 없다")
        void approvedToRejected_Fail() {
            assertThat(CancelStatus.APPROVED.canTransitionTo(CancelStatus.REJECTED)).isFalse();
        }

        @Test
        @DisplayName("APPROVED에서 CANCELLED로 전이할 수 없다")
        void approvedToCancelled_Fail() {
            assertThat(CancelStatus.APPROVED.canTransitionTo(CancelStatus.CANCELLED)).isFalse();
        }

        @Test
        @DisplayName("APPROVED에서 APPROVED로 전이할 수 없다")
        void approvedToApproved_Fail() {
            assertThat(CancelStatus.APPROVED.canTransitionTo(CancelStatus.APPROVED)).isFalse();
        }
    }

    @Nested
    @DisplayName("canTransitionTo() - 종료 상태에서 전이 불가")
    class FromTerminalStatusTest {

        @Test
        @DisplayName("COMPLETED에서 어떤 상태로도 전이할 수 없다")
        void completedIsTerminal() {
            for (CancelStatus target : CancelStatus.values()) {
                assertThat(CancelStatus.COMPLETED.canTransitionTo(target))
                        .as("COMPLETED -> %s", target)
                        .isFalse();
            }
        }

        @Test
        @DisplayName("REJECTED에서 어떤 상태로도 전이할 수 없다")
        void rejectedIsTerminal() {
            for (CancelStatus target : CancelStatus.values()) {
                assertThat(CancelStatus.REJECTED.canTransitionTo(target))
                        .as("REJECTED -> %s", target)
                        .isFalse();
            }
        }

        @Test
        @DisplayName("CANCELLED에서 어떤 상태로도 전이할 수 없다")
        void cancelledIsTerminal() {
            for (CancelStatus target : CancelStatus.values()) {
                assertThat(CancelStatus.CANCELLED.canTransitionTo(target))
                        .as("CANCELLED -> %s", target)
                        .isFalse();
            }
        }
    }
}
