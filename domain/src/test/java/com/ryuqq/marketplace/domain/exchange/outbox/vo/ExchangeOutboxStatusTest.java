package com.ryuqq.marketplace.domain.exchange.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeOutboxStatus 단위 테스트")
class ExchangeOutboxStatusTest {

    @Nested
    @DisplayName("상태 판별 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("PENDING 상태는 isPending()이 true이다")
        void pendingIsPending() {
            assertThat(ExchangeOutboxStatus.PENDING.isPending()).isTrue();
            assertThat(ExchangeOutboxStatus.PENDING.isProcessing()).isFalse();
            assertThat(ExchangeOutboxStatus.PENDING.isCompleted()).isFalse();
            assertThat(ExchangeOutboxStatus.PENDING.isFailed()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING 상태는 isProcessing()이 true이다")
        void processingIsProcessing() {
            assertThat(ExchangeOutboxStatus.PROCESSING.isProcessing()).isTrue();
            assertThat(ExchangeOutboxStatus.PROCESSING.isPending()).isFalse();
            assertThat(ExchangeOutboxStatus.PROCESSING.isCompleted()).isFalse();
            assertThat(ExchangeOutboxStatus.PROCESSING.isFailed()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태는 isCompleted()가 true이다")
        void completedIsCompleted() {
            assertThat(ExchangeOutboxStatus.COMPLETED.isCompleted()).isTrue();
            assertThat(ExchangeOutboxStatus.COMPLETED.isPending()).isFalse();
            assertThat(ExchangeOutboxStatus.COMPLETED.isProcessing()).isFalse();
            assertThat(ExchangeOutboxStatus.COMPLETED.isFailed()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태는 isFailed()가 true이다")
        void failedIsFailed() {
            assertThat(ExchangeOutboxStatus.FAILED.isFailed()).isTrue();
            assertThat(ExchangeOutboxStatus.FAILED.isPending()).isFalse();
            assertThat(ExchangeOutboxStatus.FAILED.isProcessing()).isFalse();
            assertThat(ExchangeOutboxStatus.FAILED.isCompleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("canProcess() 테스트")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING 상태만 canProcess()가 true이다")
        void onlyPendingCanProcess() {
            assertThat(ExchangeOutboxStatus.PENDING.canProcess()).isTrue();
            assertThat(ExchangeOutboxStatus.PROCESSING.canProcess()).isFalse();
            assertThat(ExchangeOutboxStatus.COMPLETED.canProcess()).isFalse();
            assertThat(ExchangeOutboxStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() 테스트")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED와 FAILED는 isTerminal()이 true이다")
        void completedAndFailedAreTerminal() {
            assertThat(ExchangeOutboxStatus.COMPLETED.isTerminal()).isTrue();
            assertThat(ExchangeOutboxStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("PENDING과 PROCESSING은 isTerminal()이 false이다")
        void pendingAndProcessingAreNotTerminal() {
            assertThat(ExchangeOutboxStatus.PENDING.isTerminal()).isFalse();
            assertThat(ExchangeOutboxStatus.PROCESSING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("각 상태는 한글 설명을 가진다")
        void eachStatusHasKoreanDescription() {
            assertThat(ExchangeOutboxStatus.PENDING.description()).isEqualTo("대기");
            assertThat(ExchangeOutboxStatus.PROCESSING.description()).isEqualTo("처리중");
            assertThat(ExchangeOutboxStatus.COMPLETED.description()).isEqualTo("완료");
            assertThat(ExchangeOutboxStatus.FAILED.description()).isEqualTo("실패");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("4개의 상태가 정의되어 있다")
        void hasFourStatuses() {
            assertThat(ExchangeOutboxStatus.values()).hasSize(4);
        }

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allStatusesExist() {
            assertThat(ExchangeOutboxStatus.values())
                    .containsExactly(
                            ExchangeOutboxStatus.PENDING,
                            ExchangeOutboxStatus.PROCESSING,
                            ExchangeOutboxStatus.COMPLETED,
                            ExchangeOutboxStatus.FAILED);
        }
    }
}
