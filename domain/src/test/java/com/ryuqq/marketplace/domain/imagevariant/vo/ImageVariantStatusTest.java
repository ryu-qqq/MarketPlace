package com.ryuqq.marketplace.domain.imagevariant.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageVariantStatus 테스트")
class ImageVariantStatusTest {

    @Nested
    @DisplayName("canProcess() - 처리 가능 여부")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING만 처리 가능하다")
        void onlyPendingCanProcess() {
            assertThat(ImageVariantStatus.PENDING.canProcess()).isTrue();
            assertThat(ImageVariantStatus.PROCESSING.canProcess()).isFalse();
            assertThat(ImageVariantStatus.COMPLETED.canProcess()).isFalse();
            assertThat(ImageVariantStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() - 종료 상태 판별")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED와 FAILED만 종료 상태이다")
        void terminalStatuses() {
            assertThat(ImageVariantStatus.COMPLETED.isTerminal()).isTrue();
            assertThat(ImageVariantStatus.FAILED.isTerminal()).isTrue();
            assertThat(ImageVariantStatus.PENDING.isTerminal()).isFalse();
            assertThat(ImageVariantStatus.PROCESSING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("nextOnSuccess() - 성공 시 다음 상태")
    class NextOnSuccessTest {

        @Test
        @DisplayName("PENDING의 다음 상태는 PROCESSING이다")
        void pendingNextIsProcessing() {
            assertThat(ImageVariantStatus.PENDING.nextOnSuccess())
                    .isEqualTo(ImageVariantStatus.PROCESSING);
        }

        @Test
        @DisplayName("PROCESSING의 다음 상태는 COMPLETED이다")
        void processingNextIsCompleted() {
            assertThat(ImageVariantStatus.PROCESSING.nextOnSuccess())
                    .isEqualTo(ImageVariantStatus.COMPLETED);
        }

        @Test
        @DisplayName("COMPLETED에서는 예외가 발생한다")
        void completedThrowsException() {
            assertThatThrownBy(() -> ImageVariantStatus.COMPLETED.nextOnSuccess())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("종료 상태에서 전환 불가");
        }

        @Test
        @DisplayName("FAILED에서는 예외가 발생한다")
        void failedThrowsException() {
            assertThatThrownBy(() -> ImageVariantStatus.FAILED.nextOnSuccess())
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
            assertThat(ImageVariantStatus.PENDING.isPending()).isTrue();
            assertThat(ImageVariantStatus.PROCESSING.isProcessing()).isTrue();
            assertThat(ImageVariantStatus.COMPLETED.isCompleted()).isTrue();
            assertThat(ImageVariantStatus.FAILED.isFailed()).isTrue();
        }
    }
}
