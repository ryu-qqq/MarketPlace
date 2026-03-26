package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AnalysisStatus 단위 테스트")
class AnalysisStatusTest {

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("AnalysisStatus는 6가지 값을 가진다")
        void analysisStatusValues() {
            AnalysisStatus[] values = AnalysisStatus.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            AnalysisStatus.PENDING,
                            AnalysisStatus.ORCHESTRATING,
                            AnalysisStatus.ANALYZING,
                            AnalysisStatus.AGGREGATING,
                            AnalysisStatus.COMPLETED,
                            AnalysisStatus.FAILED);
        }

        @Test
        @DisplayName("각 상태의 description이 올바르다")
        void descriptionIsCorrect() {
            assertThat(AnalysisStatus.PENDING.description()).isEqualTo("대기");
            assertThat(AnalysisStatus.ORCHESTRATING.description()).isEqualTo("오케스트레이션중");
            assertThat(AnalysisStatus.ANALYZING.description()).isEqualTo("분석중");
            assertThat(AnalysisStatus.AGGREGATING.description()).isEqualTo("집계중");
            assertThat(AnalysisStatus.COMPLETED.description()).isEqualTo("완료");
            assertThat(AnalysisStatus.FAILED.description()).isEqualTo("실패");
        }
    }

    @Nested
    @DisplayName("isPending() - 대기 상태 확인")
    class IsPendingTest {

        @Test
        @DisplayName("PENDING 상태는 isPending이 true이다")
        void pendingIsPending() {
            assertThat(AnalysisStatus.PENDING.isPending()).isTrue();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태는 isPending이 false이다")
        void otherStatusIsNotPending() {
            assertThat(AnalysisStatus.ANALYZING.isPending()).isFalse();
            assertThat(AnalysisStatus.COMPLETED.isPending()).isFalse();
            assertThat(AnalysisStatus.FAILED.isPending()).isFalse();
        }
    }

    @Nested
    @DisplayName("isAnalyzing() - 분석 중 상태 확인")
    class IsAnalyzingTest {

        @Test
        @DisplayName("ANALYZING 상태는 isAnalyzing이 true이다")
        void analyzingIsAnalyzing() {
            assertThat(AnalysisStatus.ANALYZING.isAnalyzing()).isTrue();
        }

        @Test
        @DisplayName("ANALYZING이 아닌 상태는 isAnalyzing이 false이다")
        void otherStatusIsNotAnalyzing() {
            assertThat(AnalysisStatus.PENDING.isAnalyzing()).isFalse();
            assertThat(AnalysisStatus.COMPLETED.isAnalyzing()).isFalse();
        }
    }

    @Nested
    @DisplayName("isCompleted() - 완료 상태 확인")
    class IsCompletedTest {

        @Test
        @DisplayName("COMPLETED 상태는 isCompleted가 true이다")
        void completedIsCompleted() {
            assertThat(AnalysisStatus.COMPLETED.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED가 아닌 상태는 isCompleted가 false이다")
        void otherStatusIsNotCompleted() {
            assertThat(AnalysisStatus.PENDING.isCompleted()).isFalse();
            assertThat(AnalysisStatus.FAILED.isCompleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("isFailed() - 실패 상태 확인")
    class IsFailedTest {

        @Test
        @DisplayName("FAILED 상태는 isFailed가 true이다")
        void failedIsFailed() {
            assertThat(AnalysisStatus.FAILED.isFailed()).isTrue();
        }

        @Test
        @DisplayName("FAILED가 아닌 상태는 isFailed가 false이다")
        void otherStatusIsNotFailed() {
            assertThat(AnalysisStatus.PENDING.isFailed()).isFalse();
            assertThat(AnalysisStatus.COMPLETED.isFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() - 종료 상태 확인")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED와 FAILED는 종료 상태이다")
        void completedAndFailedAreTerminal() {
            assertThat(AnalysisStatus.COMPLETED.isTerminal()).isTrue();
            assertThat(AnalysisStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("진행 중 상태는 종료 상태가 아니다")
        void inProgressStatusIsNotTerminal() {
            assertThat(AnalysisStatus.PENDING.isTerminal()).isFalse();
            assertThat(AnalysisStatus.ORCHESTRATING.isTerminal()).isFalse();
            assertThat(AnalysisStatus.ANALYZING.isTerminal()).isFalse();
            assertThat(AnalysisStatus.AGGREGATING.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("isInProgress() - 진행 중 상태 확인")
    class IsInProgressTest {

        @Test
        @DisplayName("ORCHESTRATING, ANALYZING, AGGREGATING은 진행 중 상태이다")
        void orchestratingAnalyzingAggregatingAreInProgress() {
            assertThat(AnalysisStatus.ORCHESTRATING.isInProgress()).isTrue();
            assertThat(AnalysisStatus.ANALYZING.isInProgress()).isTrue();
            assertThat(AnalysisStatus.AGGREGATING.isInProgress()).isTrue();
        }

        @Test
        @DisplayName("PENDING, COMPLETED, FAILED는 진행 중 상태가 아니다")
        void pendingCompletedFailedAreNotInProgress() {
            assertThat(AnalysisStatus.PENDING.isInProgress()).isFalse();
            assertThat(AnalysisStatus.COMPLETED.isInProgress()).isFalse();
            assertThat(AnalysisStatus.FAILED.isInProgress()).isFalse();
        }
    }
}
