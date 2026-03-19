package com.ryuqq.marketplace.domain.shipment.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentOutboxStatus 단위 테스트")
class ShipmentOutboxStatusTest {

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("PENDING의 설명은 대기이다")
        void pendingDescription() {
            assertThat(ShipmentOutboxStatus.PENDING.description()).isEqualTo("대기");
        }

        @Test
        @DisplayName("PROCESSING의 설명은 처리중이다")
        void processingDescription() {
            assertThat(ShipmentOutboxStatus.PROCESSING.description()).isEqualTo("처리중");
        }

        @Test
        @DisplayName("COMPLETED의 설명은 완료이다")
        void completedDescription() {
            assertThat(ShipmentOutboxStatus.COMPLETED.description()).isEqualTo("완료");
        }

        @Test
        @DisplayName("FAILED의 설명은 실패이다")
        void failedDescription() {
            assertThat(ShipmentOutboxStatus.FAILED.description()).isEqualTo("실패");
        }
    }

    @Nested
    @DisplayName("isPending() 테스트")
    class IsPendingTest {

        @Test
        @DisplayName("PENDING 상태는 isPending()이 true이다")
        void pendingStatusIsPending() {
            assertThat(ShipmentOutboxStatus.PENDING.isPending()).isTrue();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태는 isPending()이 false이다")
        void nonPendingStatusIsNotPending() {
            assertThat(ShipmentOutboxStatus.PROCESSING.isPending()).isFalse();
            assertThat(ShipmentOutboxStatus.COMPLETED.isPending()).isFalse();
            assertThat(ShipmentOutboxStatus.FAILED.isPending()).isFalse();
        }
    }

    @Nested
    @DisplayName("isProcessing() 테스트")
    class IsProcessingTest {

        @Test
        @DisplayName("PROCESSING 상태는 isProcessing()이 true이다")
        void processingStatusIsProcessing() {
            assertThat(ShipmentOutboxStatus.PROCESSING.isProcessing()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태는 isProcessing()이 false이다")
        void nonProcessingStatusIsNotProcessing() {
            assertThat(ShipmentOutboxStatus.PENDING.isProcessing()).isFalse();
            assertThat(ShipmentOutboxStatus.COMPLETED.isProcessing()).isFalse();
            assertThat(ShipmentOutboxStatus.FAILED.isProcessing()).isFalse();
        }
    }

    @Nested
    @DisplayName("isCompleted() 테스트")
    class IsCompletedTest {

        @Test
        @DisplayName("COMPLETED 상태는 isCompleted()이 true이다")
        void completedStatusIsCompleted() {
            assertThat(ShipmentOutboxStatus.COMPLETED.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED가 아닌 상태는 isCompleted()가 false이다")
        void nonCompletedStatusIsNotCompleted() {
            assertThat(ShipmentOutboxStatus.PENDING.isCompleted()).isFalse();
            assertThat(ShipmentOutboxStatus.PROCESSING.isCompleted()).isFalse();
            assertThat(ShipmentOutboxStatus.FAILED.isCompleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("isFailed() 테스트")
    class IsFailedTest {

        @Test
        @DisplayName("FAILED 상태는 isFailed()가 true이다")
        void failedStatusIsFailed() {
            assertThat(ShipmentOutboxStatus.FAILED.isFailed()).isTrue();
        }

        @Test
        @DisplayName("FAILED가 아닌 상태는 isFailed()가 false이다")
        void nonFailedStatusIsNotFailed() {
            assertThat(ShipmentOutboxStatus.PENDING.isFailed()).isFalse();
            assertThat(ShipmentOutboxStatus.PROCESSING.isFailed()).isFalse();
            assertThat(ShipmentOutboxStatus.COMPLETED.isFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("canProcess() 테스트")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING 상태만 canProcess()가 true이다")
        void pendingStatusCanProcess() {
            assertThat(ShipmentOutboxStatus.PENDING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태는 canProcess()가 false이다")
        void nonPendingStatusCannotProcess() {
            assertThat(ShipmentOutboxStatus.PROCESSING.canProcess()).isFalse();
            assertThat(ShipmentOutboxStatus.COMPLETED.canProcess()).isFalse();
            assertThat(ShipmentOutboxStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() 테스트")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED는 isTerminal()이 true이다")
        void completedIsTerminal() {
            assertThat(ShipmentOutboxStatus.COMPLETED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("FAILED는 isTerminal()이 true이다")
        void failedIsTerminal() {
            assertThat(ShipmentOutboxStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("진행 중 상태는 isTerminal()이 false이다")
        void nonTerminalStatusIsNotTerminal() {
            assertThat(ShipmentOutboxStatus.PENDING.isTerminal()).isFalse();
            assertThat(ShipmentOutboxStatus.PROCESSING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("ShipmentOutboxStatus는 4가지 값이다")
        void statusValues() {
            assertThat(ShipmentOutboxStatus.values())
                    .containsExactlyInAnyOrder(
                            ShipmentOutboxStatus.PENDING,
                            ShipmentOutboxStatus.PROCESSING,
                            ShipmentOutboxStatus.COMPLETED,
                            ShipmentOutboxStatus.FAILED);
        }
    }
}
