package com.ryuqq.marketplace.domain.legacyconversion.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyConversionOutboxStatus VO 테스트")
class LegacyConversionOutboxStatusTest {

    @Nested
    @DisplayName("상태 판별 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("PENDING 상태에서 isPending()은 true를 반환한다")
        void isPendingReturnsTrueForPending() {
            assertThat(LegacyConversionOutboxStatus.PENDING.isPending()).isTrue();
            assertThat(LegacyConversionOutboxStatus.PROCESSING.isPending()).isFalse();
            assertThat(LegacyConversionOutboxStatus.COMPLETED.isPending()).isFalse();
            assertThat(LegacyConversionOutboxStatus.FAILED.isPending()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING 상태에서 isProcessing()은 true를 반환한다")
        void isProcessingReturnsTrueForProcessing() {
            assertThat(LegacyConversionOutboxStatus.PROCESSING.isProcessing()).isTrue();
            assertThat(LegacyConversionOutboxStatus.PENDING.isProcessing()).isFalse();
            assertThat(LegacyConversionOutboxStatus.COMPLETED.isProcessing()).isFalse();
            assertThat(LegacyConversionOutboxStatus.FAILED.isProcessing()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태에서 isCompleted()는 true를 반환한다")
        void isCompletedReturnsTrueForCompleted() {
            assertThat(LegacyConversionOutboxStatus.COMPLETED.isCompleted()).isTrue();
            assertThat(LegacyConversionOutboxStatus.PENDING.isCompleted()).isFalse();
            assertThat(LegacyConversionOutboxStatus.PROCESSING.isCompleted()).isFalse();
            assertThat(LegacyConversionOutboxStatus.FAILED.isCompleted()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태에서 isFailed()는 true를 반환한다")
        void isFailedReturnsTrueForFailed() {
            assertThat(LegacyConversionOutboxStatus.FAILED.isFailed()).isTrue();
            assertThat(LegacyConversionOutboxStatus.PENDING.isFailed()).isFalse();
            assertThat(LegacyConversionOutboxStatus.PROCESSING.isFailed()).isFalse();
            assertThat(LegacyConversionOutboxStatus.COMPLETED.isFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("canProcess() - 처리 가능 여부 테스트")
    class CanProcessTest {

        @Test
        @DisplayName("PENDING 상태는 처리 가능하다")
        void pendingCanProcess() {
            assertThat(LegacyConversionOutboxStatus.PENDING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태는 처리 가능하다")
        void processingCanProcess() {
            assertThat(LegacyConversionOutboxStatus.PROCESSING.canProcess()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED 상태는 처리 불가능하다")
        void completedCanNotProcess() {
            assertThat(LegacyConversionOutboxStatus.COMPLETED.canProcess()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태는 처리 불가능하다")
        void failedCanNotProcess() {
            assertThat(LegacyConversionOutboxStatus.FAILED.canProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() - 종료 상태 여부 테스트")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED는 종료 상태이다")
        void completedIsTerminal() {
            assertThat(LegacyConversionOutboxStatus.COMPLETED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("FAILED는 종료 상태이다")
        void failedIsTerminal() {
            assertThat(LegacyConversionOutboxStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("PENDING은 종료 상태가 아니다")
        void pendingIsNotTerminal() {
            assertThat(LegacyConversionOutboxStatus.PENDING.isTerminal()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING은 종료 상태가 아니다")
        void processingIsNotTerminal() {
            assertThat(LegacyConversionOutboxStatus.PROCESSING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("description() - 설명 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("각 상태의 설명이 올바르다")
        void descriptionForEachStatus() {
            assertThat(LegacyConversionOutboxStatus.PENDING.description()).isEqualTo("대기");
            assertThat(LegacyConversionOutboxStatus.PROCESSING.description()).isEqualTo("처리중");
            assertThat(LegacyConversionOutboxStatus.COMPLETED.description()).isEqualTo("완료");
            assertThat(LegacyConversionOutboxStatus.FAILED.description()).isEqualTo("실패");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allStatusValuesExist() {
            assertThat(LegacyConversionOutboxStatus.values())
                    .containsExactly(
                            LegacyConversionOutboxStatus.PENDING,
                            LegacyConversionOutboxStatus.PROCESSING,
                            LegacyConversionOutboxStatus.COMPLETED,
                            LegacyConversionOutboxStatus.FAILED);
        }
    }
}
