package com.ryuqq.marketplace.domain.refund.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundOutboxStatus 단위 테스트")
class RefundOutboxStatusTest {

    @Nested
    @DisplayName("상태 판별 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("PENDING 상태는 isPending()이 true이다")
        void pendingIsPending() {
            assertThat(RefundOutboxStatus.PENDING.isPending()).isTrue();
            assertThat(RefundOutboxStatus.PENDING.isProcessing()).isFalse();
            assertThat(RefundOutboxStatus.PENDING.isCompleted()).isFalse();
            assertThat(RefundOutboxStatus.PENDING.isFailed()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING 상태는 isProcessing()이 true이다")
        void processingIsProcessing() {
            assertThat(RefundOutboxStatus.PROCESSING.isProcessing()).isTrue();
            assertThat(RefundOutboxStatus.PROCESSING.isPending()).isFalse();
            assertThat(RefundOutboxStatus.PROCESSING.isCompleted()).isFalse();
            assertThat(RefundOutboxStatus.PROCESSING.isFailed()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태는 isCompleted()가 true이다")
        void completedIsCompleted() {
            assertThat(RefundOutboxStatus.COMPLETED.isCompleted()).isTrue();
            assertThat(RefundOutboxStatus.COMPLETED.isPending()).isFalse();
            assertThat(RefundOutboxStatus.COMPLETED.isProcessing()).isFalse();
            assertThat(RefundOutboxStatus.COMPLETED.isFailed()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태는 isFailed()가 true이다")
        void failedIsFailed() {
            assertThat(RefundOutboxStatus.FAILED.isFailed()).isTrue();
            assertThat(RefundOutboxStatus.FAILED.isPending()).isFalse();
            assertThat(RefundOutboxStatus.FAILED.isProcessing()).isFalse();
            assertThat(RefundOutboxStatus.FAILED.isCompleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("canProcess() 테스트")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING 상태만 canProcess()가 true이다")
        void onlyPendingCanProcess() {
            assertThat(RefundOutboxStatus.PENDING.canProcess()).isTrue();
            assertThat(RefundOutboxStatus.PROCESSING.canProcess()).isFalse();
            assertThat(RefundOutboxStatus.COMPLETED.canProcess()).isFalse();
            assertThat(RefundOutboxStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() 테스트")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED와 FAILED는 isTerminal()이 true이다")
        void completedAndFailedAreTerminal() {
            assertThat(RefundOutboxStatus.COMPLETED.isTerminal()).isTrue();
            assertThat(RefundOutboxStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("PENDING과 PROCESSING은 isTerminal()이 false이다")
        void pendingAndProcessingAreNotTerminal() {
            assertThat(RefundOutboxStatus.PENDING.isTerminal()).isFalse();
            assertThat(RefundOutboxStatus.PROCESSING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("각 상태는 한글 설명을 가진다")
        void eachStatusHasKoreanDescription() {
            assertThat(RefundOutboxStatus.PENDING.description()).isEqualTo("대기");
            assertThat(RefundOutboxStatus.PROCESSING.description()).isEqualTo("처리중");
            assertThat(RefundOutboxStatus.COMPLETED.description()).isEqualTo("완료");
            assertThat(RefundOutboxStatus.FAILED.description()).isEqualTo("실패");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("4개의 상태가 정의되어 있다")
        void hasFourStatuses() {
            assertThat(RefundOutboxStatus.values()).hasSize(4);
        }

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allStatusesExist() {
            assertThat(RefundOutboxStatus.values())
                    .containsExactly(
                            RefundOutboxStatus.PENDING,
                            RefundOutboxStatus.PROCESSING,
                            RefundOutboxStatus.COMPLETED,
                            RefundOutboxStatus.FAILED);
        }
    }
}
