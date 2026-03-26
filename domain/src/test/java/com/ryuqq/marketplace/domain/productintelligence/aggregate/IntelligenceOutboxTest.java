package com.ryuqq.marketplace.domain.productintelligence.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.productintelligence.ProductIntelligenceFixtures;
import com.ryuqq.marketplace.domain.productintelligence.exception.InvalidOutboxStateException;
import com.ryuqq.marketplace.domain.productintelligence.vo.IntelligenceOutboxStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("IntelligenceOutbox Aggregate 단위 테스트")
class IntelligenceOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 Outbox 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 Outbox는 PENDING 상태로 생성된다")
        void forNewCreatesWithPendingStatus() {
            Instant now = Instant.now();

            IntelligenceOutbox outbox = IntelligenceOutbox.forNew(100L, now);

            assertThat(outbox.status()).isEqualTo(IntelligenceOutboxStatus.PENDING);
            assertThat(outbox.productGroupId()).isEqualTo(100L);
            assertThat(outbox.profileId()).isNull();
            assertThat(outbox.retryCount()).isEqualTo(0);
            assertThat(outbox.maxRetry()).isEqualTo(3);
        }

        @Test
        @DisplayName("신규 Outbox는 isNew가 true이다")
        void forNewIsNew() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.newPendingOutbox();

            assertThat(outbox.isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 Outbox는 멱등키가 PI: 형식으로 생성된다")
        void forNewHasIdempotencyKeyWithPiPrefix() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.newPendingOutbox();

            assertThat(outbox.idempotencyKeyValue()).startsWith("PI:");
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("기존 Outbox를 재구성하면 isNew가 false이다")
        void reconstitutedOutboxIsNotNew() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();

            assertThat(outbox.isNew()).isFalse();
        }

        @Test
        @DisplayName("재구성 시 멱등키가 올바르게 설정된다")
        void reconstitutedOutboxHasCorrectIdempotencyKey() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();

            assertThat(outbox.idempotencyKeyValue())
                    .isEqualTo(ProductIntelligenceFixtures.DEFAULT_IDEMPOTENCY_KEY);
        }
    }

    @Nested
    @DisplayName("assignProfile() - ProfileId 할당")
    class AssignProfileTest {

        @Test
        @DisplayName("ProfileId를 할당한다")
        void assignProfileIdSuccess() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.newPendingOutbox();

            outbox.assignProfile(1L);

            assertThat(outbox.profileId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("markAsSent() - PENDING → SENT")
    class MarkAsSentTest {

        @Test
        @DisplayName("PENDING 상태에서 SENT로 전환한다")
        void pendingToSent() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();
            Instant now = Instant.now();

            outbox.markAsSent(now);

            assertThat(outbox.status()).isEqualTo(IntelligenceOutboxStatus.SENT);
        }

        @Test
        @DisplayName("PENDING이 아닌 상태에서 markAsSent 호출 시 예외가 발생한다")
        void markAsSentFromNonPendingThrows() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();
            Instant now = Instant.now();
            outbox.markAsSent(now);

            assertThatThrownBy(() -> outbox.markAsSent(now))
                    .isInstanceOf(InvalidOutboxStateException.class);
        }
    }

    @Nested
    @DisplayName("complete() - SENT → COMPLETED")
    class CompleteTest {

        @Test
        @DisplayName("SENT 상태에서 COMPLETED로 전환한다")
        void sentToCompleted() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();
            Instant now = Instant.now();
            outbox.markAsSent(now);

            outbox.complete(now);

            assertThat(outbox.status()).isEqualTo(IntelligenceOutboxStatus.COMPLETED);
            assertThat(outbox.processedAt()).isNotNull();
            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("PENDING 상태에서 complete 호출 시 예외가 발생한다")
        void completeFromPendingThrows() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();

            assertThatThrownBy(() -> outbox.complete(Instant.now()))
                    .isInstanceOf(InvalidOutboxStateException.class);
        }
    }

    @Nested
    @DisplayName("recordFailure() - 실패 결과 반영")
    class RecordFailureTest {

        @Test
        @DisplayName("재시도 가능한 경우 retryCount가 증가하고 PENDING으로 복귀한다")
        void recordFailureWithRetryIncreasesRetryCount() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();
            Instant now = Instant.now();

            outbox.recordFailure(true, "SQS 전송 실패", now);

            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.status()).isEqualTo(IntelligenceOutboxStatus.PENDING);
            assertThat(outbox.errorMessage()).isEqualTo("SQS 전송 실패");
        }

        @Test
        @DisplayName("재시도 불가한 경우 즉시 FAILED로 전환한다")
        void recordFailureWithoutRetrySetsFailed() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();
            Instant now = Instant.now();

            outbox.recordFailure(false, "치명적 오류", now);

            assertThat(outbox.status()).isEqualTo(IntelligenceOutboxStatus.FAILED);
            assertThat(outbox.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("최대 재시도 횟수 초과 시 FAILED로 전환한다")
        void recordFailureAfterMaxRetrySetsFailed() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();
            Instant now = Instant.now();

            outbox.recordFailure(true, "오류1", now);
            outbox.recordFailure(true, "오류2", now);
            outbox.recordFailure(true, "오류3", now);

            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.status()).isEqualTo(IntelligenceOutboxStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("SENT 상태에서 타임아웃 복구하면 PENDING으로 전환한다")
        void recoverSentStatusToPending() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();
            Instant now = Instant.now();
            outbox.markAsSent(now);

            outbox.recoverFromTimeout(now);

            assertThat(outbox.status()).isEqualTo(IntelligenceOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("PENDING 상태에서 타임아웃 복구 시 예외가 발생한다")
        void recoverFromPendingThrows() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();

            assertThatThrownBy(() -> outbox.recoverFromTimeout(Instant.now()))
                    .isInstanceOf(InvalidOutboxStateException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 타임아웃 복구 시 예외가 발생한다")
        void recoverFromCompletedThrows() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();
            Instant now = Instant.now();
            outbox.markAsSent(now);
            outbox.complete(now);

            assertThatThrownBy(() -> outbox.recoverFromTimeout(now))
                    .isInstanceOf(InvalidOutboxStateException.class);
        }
    }

    @Nested
    @DisplayName("hasExpectedStatus() - 기대 상태 확인")
    class HasExpectedStatusTest {

        @Test
        @DisplayName("현재 상태가 기대 상태와 일치하면 true이다")
        void matchingStatusReturnsTrue() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();

            assertThat(outbox.hasExpectedStatus(IntelligenceOutboxStatus.PENDING)).isTrue();
        }

        @Test
        @DisplayName("현재 상태가 기대 상태와 다르면 false이다")
        void nonMatchingStatusReturnsFalse() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();

            assertThat(outbox.hasExpectedStatus(IntelligenceOutboxStatus.SENT)).isFalse();
        }
    }

    @Nested
    @DisplayName("canRetry() - 재시도 가능 여부")
    class CanRetryTest {

        @Test
        @DisplayName("retryCount가 maxRetry 미만이고 PENDING 상태이면 재시도 가능하다")
        void canRetryWhenBelowMaxAndPending() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();

            assertThat(outbox.canRetry()).isTrue();
        }

        @Test
        @DisplayName("retryCount가 maxRetry 이상이면 재시도 불가하다")
        void cannotRetryWhenAtMaxRetry() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();
            Instant now = Instant.now();

            outbox.recordFailure(true, "오류1", now);
            outbox.recordFailure(true, "오류2", now);
            outbox.recordFailure(true, "오류3", now);

            assertThat(outbox.canRetry()).isFalse();
        }
    }

    @Nested
    @DisplayName("refreshVersion() - 버전 갱신")
    class RefreshVersionTest {

        @Test
        @DisplayName("버전을 갱신한다")
        void refreshVersionUpdatesVersion() {
            IntelligenceOutbox outbox = ProductIntelligenceFixtures.existingPendingOutbox();

            outbox.refreshVersion(5L);

            assertThat(outbox.version()).isEqualTo(5L);
        }
    }
}
