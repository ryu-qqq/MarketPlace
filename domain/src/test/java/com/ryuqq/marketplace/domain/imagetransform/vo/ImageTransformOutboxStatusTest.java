package com.ryuqq.marketplace.domain.imagetransform.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageTransformOutboxStatus 테스트")
class ImageTransformOutboxStatusTest {

    @Nested
    @DisplayName("canProcess() - 처리 가능 여부")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING은 처리 가능하다")
        void pendingCanProcess() {
            assertThat(ImageTransformOutboxStatus.PENDING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING은 처리 가능하다")
        void processingCanProcess() {
            assertThat(ImageTransformOutboxStatus.PROCESSING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED는 처리 불가하다")
        void completedCannotProcess() {
            assertThat(ImageTransformOutboxStatus.COMPLETED.canProcess()).isFalse();
        }

        @Test
        @DisplayName("FAILED는 처리 불가하다")
        void failedCannotProcess() {
            assertThat(ImageTransformOutboxStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() - 종료 상태 판별")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED와 FAILED만 종료 상태이다")
        void terminalStatuses() {
            assertThat(ImageTransformOutboxStatus.COMPLETED.isTerminal()).isTrue();
            assertThat(ImageTransformOutboxStatus.FAILED.isTerminal()).isTrue();
            assertThat(ImageTransformOutboxStatus.PENDING.isTerminal()).isFalse();
            assertThat(ImageTransformOutboxStatus.PROCESSING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("nextOnSuccess() - 성공 시 다음 상태")
    class NextOnSuccessTest {

        @Test
        @DisplayName("PENDING의 다음 상태는 PROCESSING이다")
        void pendingNextIsProcessing() {
            assertThat(ImageTransformOutboxStatus.PENDING.nextOnSuccess())
                    .isEqualTo(ImageTransformOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("PROCESSING의 다음 상태는 COMPLETED이다")
        void processingNextIsCompleted() {
            assertThat(ImageTransformOutboxStatus.PROCESSING.nextOnSuccess())
                    .isEqualTo(ImageTransformOutboxStatus.COMPLETED);
        }

        @Test
        @DisplayName("COMPLETED에서는 예외가 발생한다")
        void completedThrowsException() {
            assertThatThrownBy(() -> ImageTransformOutboxStatus.COMPLETED.nextOnSuccess())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("종료 상태에서 전환 불가");
        }

        @Test
        @DisplayName("FAILED에서는 예외가 발생한다")
        void failedThrowsException() {
            assertThatThrownBy(() -> ImageTransformOutboxStatus.FAILED.nextOnSuccess())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("종료 상태에서 전환 불가");
        }
    }

    @Nested
    @DisplayName("상태 판별 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("각 상태별 판별 메서드가 올바르게 동작한다")
        void statusCheckMethods() {
            assertThat(ImageTransformOutboxStatus.PENDING.isPending()).isTrue();
            assertThat(ImageTransformOutboxStatus.PROCESSING.isProcessing()).isTrue();
            assertThat(ImageTransformOutboxStatus.COMPLETED.isCompleted()).isTrue();
            assertThat(ImageTransformOutboxStatus.FAILED.isFailed()).isTrue();
        }
    }
}
