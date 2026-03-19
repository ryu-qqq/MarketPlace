package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("IntelligenceOutboxStatus 단위 테스트")
class IntelligenceOutboxStatusTest {

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("IntelligenceOutboxStatus는 4가지 값을 가진다")
        void outboxStatusValues() {
            IntelligenceOutboxStatus[] values = IntelligenceOutboxStatus.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            IntelligenceOutboxStatus.PENDING,
                            IntelligenceOutboxStatus.SENT,
                            IntelligenceOutboxStatus.COMPLETED,
                            IntelligenceOutboxStatus.FAILED);
        }

        @Test
        @DisplayName("각 상태의 description이 올바르다")
        void descriptionIsCorrect() {
            assertThat(IntelligenceOutboxStatus.PENDING.description()).isEqualTo("대기");
            assertThat(IntelligenceOutboxStatus.SENT.description()).isEqualTo("발행완료");
            assertThat(IntelligenceOutboxStatus.COMPLETED.description()).isEqualTo("완료");
            assertThat(IntelligenceOutboxStatus.FAILED.description()).isEqualTo("실패");
        }
    }

    @Nested
    @DisplayName("상태 확인 메서드")
    class StatusCheckTest {

        @Test
        @DisplayName("PENDING 상태는 isPending이 true이다")
        void pendingIsPending() {
            assertThat(IntelligenceOutboxStatus.PENDING.isPending()).isTrue();
            assertThat(IntelligenceOutboxStatus.SENT.isPending()).isFalse();
        }

        @Test
        @DisplayName("SENT 상태는 isSent가 true이다")
        void sentIsSent() {
            assertThat(IntelligenceOutboxStatus.SENT.isSent()).isTrue();
            assertThat(IntelligenceOutboxStatus.PENDING.isSent()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태는 isCompleted가 true이다")
        void completedIsCompleted() {
            assertThat(IntelligenceOutboxStatus.COMPLETED.isCompleted()).isTrue();
            assertThat(IntelligenceOutboxStatus.PENDING.isCompleted()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태는 isFailed가 true이다")
        void failedIsFailed() {
            assertThat(IntelligenceOutboxStatus.FAILED.isFailed()).isTrue();
            assertThat(IntelligenceOutboxStatus.PENDING.isFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("canSend() - 발행 가능 여부")
    class CanSendTest {

        @Test
        @DisplayName("PENDING 상태만 발행 가능하다")
        void onlyPendingCanSend() {
            assertThat(IntelligenceOutboxStatus.PENDING.canSend()).isTrue();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태는 발행 불가하다")
        void nonPendingCannotSend() {
            assertThat(IntelligenceOutboxStatus.SENT.canSend()).isFalse();
            assertThat(IntelligenceOutboxStatus.COMPLETED.canSend()).isFalse();
            assertThat(IntelligenceOutboxStatus.FAILED.canSend()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() - 종료 상태 확인")
    class IsTerminalTest {

        @Test
        @DisplayName("COMPLETED와 FAILED는 종료 상태이다")
        void completedAndFailedAreTerminal() {
            assertThat(IntelligenceOutboxStatus.COMPLETED.isTerminal()).isTrue();
            assertThat(IntelligenceOutboxStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("PENDING과 SENT는 종료 상태가 아니다")
        void pendingAndSentAreNotTerminal() {
            assertThat(IntelligenceOutboxStatus.PENDING.isTerminal()).isFalse();
            assertThat(IntelligenceOutboxStatus.SENT.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("isInProgress() - 진행 중 상태 확인")
    class IsInProgressTest {

        @Test
        @DisplayName("SENT 상태만 진행 중이다 (타임아웃 복구 대상)")
        void onlySentIsInProgress() {
            assertThat(IntelligenceOutboxStatus.SENT.isInProgress()).isTrue();
        }

        @Test
        @DisplayName("SENT가 아닌 상태는 진행 중이 아니다")
        void nonSentIsNotInProgress() {
            assertThat(IntelligenceOutboxStatus.PENDING.isInProgress()).isFalse();
            assertThat(IntelligenceOutboxStatus.COMPLETED.isInProgress()).isFalse();
            assertThat(IntelligenceOutboxStatus.FAILED.isInProgress()).isFalse();
        }
    }
}
