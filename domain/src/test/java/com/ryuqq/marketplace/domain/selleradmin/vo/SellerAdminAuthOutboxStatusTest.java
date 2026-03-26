package com.ryuqq.marketplace.domain.selleradmin.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdminAuthOutboxStatus 단위 테스트")
class SellerAdminAuthOutboxStatusTest {

    @Nested
    @DisplayName("상태 판별 메서드")
    class StatusCheckTest {

        @Test
        @DisplayName("PENDING 상태는 isPending()이 true다")
        void pendingIsPending() {
            assertThat(SellerAdminAuthOutboxStatus.PENDING.isPending()).isTrue();
            assertThat(SellerAdminAuthOutboxStatus.PROCESSING.isPending()).isFalse();
            assertThat(SellerAdminAuthOutboxStatus.COMPLETED.isPending()).isFalse();
            assertThat(SellerAdminAuthOutboxStatus.FAILED.isPending()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING 상태는 isProcessing()이 true다")
        void processingIsProcessing() {
            assertThat(SellerAdminAuthOutboxStatus.PROCESSING.isProcessing()).isTrue();
            assertThat(SellerAdminAuthOutboxStatus.PENDING.isProcessing()).isFalse();
            assertThat(SellerAdminAuthOutboxStatus.COMPLETED.isProcessing()).isFalse();
            assertThat(SellerAdminAuthOutboxStatus.FAILED.isProcessing()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태는 isCompleted()가 true다")
        void completedIsCompleted() {
            assertThat(SellerAdminAuthOutboxStatus.COMPLETED.isCompleted()).isTrue();
            assertThat(SellerAdminAuthOutboxStatus.PENDING.isCompleted()).isFalse();
            assertThat(SellerAdminAuthOutboxStatus.PROCESSING.isCompleted()).isFalse();
            assertThat(SellerAdminAuthOutboxStatus.FAILED.isCompleted()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태는 isFailed()가 true다")
        void failedIsFailed() {
            assertThat(SellerAdminAuthOutboxStatus.FAILED.isFailed()).isTrue();
            assertThat(SellerAdminAuthOutboxStatus.PENDING.isFailed()).isFalse();
            assertThat(SellerAdminAuthOutboxStatus.PROCESSING.isFailed()).isFalse();
            assertThat(SellerAdminAuthOutboxStatus.COMPLETED.isFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("canProcess() - 처리 가능 여부")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING 상태는 처리 가능하다")
        void pendingCanProcess() {
            assertThat(SellerAdminAuthOutboxStatus.PENDING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태는 처리 가능하다")
        void processingCanProcess() {
            assertThat(SellerAdminAuthOutboxStatus.PROCESSING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED 상태는 처리 불가하다")
        void completedCannotProcess() {
            assertThat(SellerAdminAuthOutboxStatus.COMPLETED.canProcess()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태는 처리 불가하다")
        void failedCannotProcess() {
            assertThat(SellerAdminAuthOutboxStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() - 종료 상태 여부")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED는 종료 상태다")
        void completedIsTerminal() {
            assertThat(SellerAdminAuthOutboxStatus.COMPLETED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("FAILED는 종료 상태다")
        void failedIsTerminal() {
            assertThat(SellerAdminAuthOutboxStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("PENDING은 종료 상태가 아니다")
        void pendingIsNotTerminal() {
            assertThat(SellerAdminAuthOutboxStatus.PENDING.isTerminal()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING은 종료 상태가 아니다")
        void processingIsNotTerminal() {
            assertThat(SellerAdminAuthOutboxStatus.PROCESSING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("nextStatus() - 다음 상태 전이")
    class NextStatusTest {

        @Test
        @DisplayName("PENDING의 다음 상태는 PROCESSING이다")
        void pendingNextIsProcessing() {
            assertThat(SellerAdminAuthOutboxStatus.PENDING.nextStatus())
                    .isEqualTo(SellerAdminAuthOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("PROCESSING의 다음 상태는 COMPLETED이다")
        void processingNextIsCompleted() {
            assertThat(SellerAdminAuthOutboxStatus.PROCESSING.nextStatus())
                    .isEqualTo(SellerAdminAuthOutboxStatus.COMPLETED);
        }

        @Test
        @DisplayName("COMPLETED의 다음 상태는 자기 자신이다")
        void completedNextIsSelf() {
            assertThat(SellerAdminAuthOutboxStatus.COMPLETED.nextStatus())
                    .isEqualTo(SellerAdminAuthOutboxStatus.COMPLETED);
        }

        @Test
        @DisplayName("FAILED의 다음 상태는 자기 자신이다")
        void failedNextIsSelf() {
            assertThat(SellerAdminAuthOutboxStatus.FAILED.nextStatus())
                    .isEqualTo(SellerAdminAuthOutboxStatus.FAILED);
        }
    }

    @Test
    @DisplayName("SellerAdminAuthOutboxStatus 값이 4개 존재한다")
    void hasExpectedValues() {
        assertThat(SellerAdminAuthOutboxStatus.values()).hasSize(4);
    }
}
