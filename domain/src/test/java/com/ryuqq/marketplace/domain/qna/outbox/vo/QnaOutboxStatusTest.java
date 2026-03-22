package com.ryuqq.marketplace.domain.qna.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaOutboxStatus 단위 테스트")
class QnaOutboxStatusTest {

    @Nested
    @DisplayName("상태 판별 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("PENDING 상태는 isPending()이 true이다")
        void pendingIsPending() {
            assertThat(QnaOutboxStatus.PENDING.isPending()).isTrue();
            assertThat(QnaOutboxStatus.PENDING.isProcessing()).isFalse();
            assertThat(QnaOutboxStatus.PENDING.isCompleted()).isFalse();
            assertThat(QnaOutboxStatus.PENDING.isFailed()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING 상태는 isProcessing()이 true이다")
        void processingIsProcessing() {
            assertThat(QnaOutboxStatus.PROCESSING.isProcessing()).isTrue();
            assertThat(QnaOutboxStatus.PROCESSING.isPending()).isFalse();
            assertThat(QnaOutboxStatus.PROCESSING.isCompleted()).isFalse();
            assertThat(QnaOutboxStatus.PROCESSING.isFailed()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태는 isCompleted()가 true이다")
        void completedIsCompleted() {
            assertThat(QnaOutboxStatus.COMPLETED.isCompleted()).isTrue();
            assertThat(QnaOutboxStatus.COMPLETED.isPending()).isFalse();
            assertThat(QnaOutboxStatus.COMPLETED.isProcessing()).isFalse();
            assertThat(QnaOutboxStatus.COMPLETED.isFailed()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태는 isFailed()가 true이다")
        void failedIsFailed() {
            assertThat(QnaOutboxStatus.FAILED.isFailed()).isTrue();
            assertThat(QnaOutboxStatus.FAILED.isPending()).isFalse();
            assertThat(QnaOutboxStatus.FAILED.isProcessing()).isFalse();
            assertThat(QnaOutboxStatus.FAILED.isCompleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("canProcess() 테스트")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING 상태만 canProcess()가 true이다")
        void onlyPendingCanProcess() {
            assertThat(QnaOutboxStatus.PENDING.canProcess()).isTrue();
            assertThat(QnaOutboxStatus.PROCESSING.canProcess()).isFalse();
            assertThat(QnaOutboxStatus.COMPLETED.canProcess()).isFalse();
            assertThat(QnaOutboxStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() 테스트")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED와 FAILED는 isTerminal()이 true이다")
        void completedAndFailedAreTerminal() {
            assertThat(QnaOutboxStatus.COMPLETED.isTerminal()).isTrue();
            assertThat(QnaOutboxStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("PENDING과 PROCESSING은 isTerminal()이 false이다")
        void pendingAndProcessingAreNotTerminal() {
            assertThat(QnaOutboxStatus.PENDING.isTerminal()).isFalse();
            assertThat(QnaOutboxStatus.PROCESSING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("각 상태는 한글 설명을 가진다")
        void eachStatusHasKoreanDescription() {
            assertThat(QnaOutboxStatus.PENDING.description()).isEqualTo("대기");
            assertThat(QnaOutboxStatus.PROCESSING.description()).isEqualTo("처리중");
            assertThat(QnaOutboxStatus.COMPLETED.description()).isEqualTo("완료");
            assertThat(QnaOutboxStatus.FAILED.description()).isEqualTo("실패");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("4개의 상태가 정의되어 있다")
        void hasFourStatuses() {
            assertThat(QnaOutboxStatus.values()).hasSize(4);
        }

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allStatusesExist() {
            assertThat(QnaOutboxStatus.values())
                    .containsExactly(
                            QnaOutboxStatus.PENDING,
                            QnaOutboxStatus.PROCESSING,
                            QnaOutboxStatus.COMPLETED,
                            QnaOutboxStatus.FAILED);
        }
    }
}
