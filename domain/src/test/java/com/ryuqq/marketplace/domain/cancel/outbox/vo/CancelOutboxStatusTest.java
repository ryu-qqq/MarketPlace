package com.ryuqq.marketplace.domain.cancel.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelOutboxStatus 단위 테스트")
class CancelOutboxStatusTest {

    @Nested
    @DisplayName("isPending() 테스트")
    class IsPendingTest {

        @Test
        @DisplayName("PENDING 상태이면 isPending()이 true이다")
        void pendingStatusIsPending() {
            assertThat(CancelOutboxStatus.PENDING.isPending()).isTrue();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태이면 isPending()이 false이다")
        void nonPendingStatusIsNotPending() {
            assertThat(CancelOutboxStatus.PROCESSING.isPending()).isFalse();
            assertThat(CancelOutboxStatus.COMPLETED.isPending()).isFalse();
            assertThat(CancelOutboxStatus.FAILED.isPending()).isFalse();
        }
    }

    @Nested
    @DisplayName("isProcessing() 테스트")
    class IsProcessingTest {

        @Test
        @DisplayName("PROCESSING 상태이면 isProcessing()이 true이다")
        void processingStatusIsProcessing() {
            assertThat(CancelOutboxStatus.PROCESSING.isProcessing()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태이면 isProcessing()이 false이다")
        void nonProcessingStatusIsNotProcessing() {
            assertThat(CancelOutboxStatus.PENDING.isProcessing()).isFalse();
            assertThat(CancelOutboxStatus.COMPLETED.isProcessing()).isFalse();
            assertThat(CancelOutboxStatus.FAILED.isProcessing()).isFalse();
        }
    }

    @Nested
    @DisplayName("isCompleted() 테스트")
    class IsCompletedTest {

        @Test
        @DisplayName("COMPLETED 상태이면 isCompleted()가 true이다")
        void completedStatusIsCompleted() {
            assertThat(CancelOutboxStatus.COMPLETED.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED가 아닌 상태이면 isCompleted()가 false이다")
        void nonCompletedStatusIsNotCompleted() {
            assertThat(CancelOutboxStatus.PENDING.isCompleted()).isFalse();
            assertThat(CancelOutboxStatus.PROCESSING.isCompleted()).isFalse();
            assertThat(CancelOutboxStatus.FAILED.isCompleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("isFailed() 테스트")
    class IsFailedTest {

        @Test
        @DisplayName("FAILED 상태이면 isFailed()가 true이다")
        void failedStatusIsFailed() {
            assertThat(CancelOutboxStatus.FAILED.isFailed()).isTrue();
        }

        @Test
        @DisplayName("FAILED가 아닌 상태이면 isFailed()가 false이다")
        void nonFailedStatusIsNotFailed() {
            assertThat(CancelOutboxStatus.PENDING.isFailed()).isFalse();
            assertThat(CancelOutboxStatus.PROCESSING.isFailed()).isFalse();
            assertThat(CancelOutboxStatus.COMPLETED.isFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("canProcess() 테스트")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING 상태이면 canProcess()가 true이다")
        void pendingStatusCanProcess() {
            assertThat(CancelOutboxStatus.PENDING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태이면 canProcess()가 false이다")
        void nonPendingStatusCannotProcess() {
            assertThat(CancelOutboxStatus.PROCESSING.canProcess()).isFalse();
            assertThat(CancelOutboxStatus.COMPLETED.canProcess()).isFalse();
            assertThat(CancelOutboxStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() 테스트")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED는 종료 상태이다")
        void completedIsTerminal() {
            assertThat(CancelOutboxStatus.COMPLETED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("FAILED는 종료 상태이다")
        void failedIsTerminal() {
            assertThat(CancelOutboxStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("PENDING은 종료 상태가 아니다")
        void pendingIsNotTerminal() {
            assertThat(CancelOutboxStatus.PENDING.isTerminal()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING은 종료 상태가 아니다")
        void processingIsNotTerminal() {
            assertThat(CancelOutboxStatus.PROCESSING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("PENDING의 description은 대기이다")
        void pendingDescription() {
            assertThat(CancelOutboxStatus.PENDING.description()).isEqualTo("대기");
        }

        @Test
        @DisplayName("PROCESSING의 description은 처리중이다")
        void processingDescription() {
            assertThat(CancelOutboxStatus.PROCESSING.description()).isEqualTo("처리중");
        }

        @Test
        @DisplayName("COMPLETED의 description은 완료이다")
        void completedDescription() {
            assertThat(CancelOutboxStatus.COMPLETED.description()).isEqualTo("완료");
        }

        @Test
        @DisplayName("FAILED의 description은 실패이다")
        void failedDescription() {
            assertThat(CancelOutboxStatus.FAILED.description()).isEqualTo("실패");
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("CancelOutboxStatus는 4가지 값이다")
        void outboxStatusValues() {
            assertThat(CancelOutboxStatus.values())
                    .containsExactlyInAnyOrder(
                            CancelOutboxStatus.PENDING,
                            CancelOutboxStatus.PROCESSING,
                            CancelOutboxStatus.COMPLETED,
                            CancelOutboxStatus.FAILED);
        }
    }
}
