package com.ryuqq.marketplace.domain.imageupload.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageUploadOutboxStatus 테스트")
class ImageUploadOutboxStatusTest {

    @Nested
    @DisplayName("canProcess() - 처리 가능 여부")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING은 처리 가능하다")
        void pendingCanProcess() {
            assertThat(ImageUploadOutboxStatus.PENDING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING은 처리 가능하다")
        void processingCanProcess() {
            assertThat(ImageUploadOutboxStatus.PROCESSING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED는 처리 불가하다")
        void completedCannotProcess() {
            assertThat(ImageUploadOutboxStatus.COMPLETED.canProcess()).isFalse();
        }

        @Test
        @DisplayName("FAILED는 처리 불가하다")
        void failedCannotProcess() {
            assertThat(ImageUploadOutboxStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() - 종료 상태 판별")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED는 종료 상태이다")
        void completedIsTerminal() {
            assertThat(ImageUploadOutboxStatus.COMPLETED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("FAILED는 종료 상태이다")
        void failedIsTerminal() {
            assertThat(ImageUploadOutboxStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("PENDING은 종료 상태가 아니다")
        void pendingIsNotTerminal() {
            assertThat(ImageUploadOutboxStatus.PENDING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("nextStatus() - 다음 상태 전이")
    class NextStatusTest {

        @Test
        @DisplayName("PENDING의 다음 상태는 PROCESSING이다")
        void pendingNextIsProcessing() {
            assertThat(ImageUploadOutboxStatus.PENDING.nextStatus())
                    .isEqualTo(ImageUploadOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("PROCESSING의 다음 상태는 COMPLETED이다")
        void processingNextIsCompleted() {
            assertThat(ImageUploadOutboxStatus.PROCESSING.nextStatus())
                    .isEqualTo(ImageUploadOutboxStatus.COMPLETED);
        }

        @Test
        @DisplayName("COMPLETED는 자기 자신을 반환한다")
        void completedNextIsSelf() {
            assertThat(ImageUploadOutboxStatus.COMPLETED.nextStatus())
                    .isEqualTo(ImageUploadOutboxStatus.COMPLETED);
        }

        @Test
        @DisplayName("FAILED는 자기 자신을 반환한다")
        void failedNextIsSelf() {
            assertThat(ImageUploadOutboxStatus.FAILED.nextStatus())
                    .isEqualTo(ImageUploadOutboxStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("상태 판별 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("각 상태별 판별 메서드가 올바르게 동작한다")
        void statusCheckMethods() {
            assertThat(ImageUploadOutboxStatus.PENDING.isPending()).isTrue();
            assertThat(ImageUploadOutboxStatus.PROCESSING.isProcessing()).isTrue();
            assertThat(ImageUploadOutboxStatus.COMPLETED.isCompleted()).isTrue();
            assertThat(ImageUploadOutboxStatus.FAILED.isFailed()).isTrue();
        }
    }
}
