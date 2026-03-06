package com.ryuqq.marketplace.domain.outboundsync.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundSyncOutbox Aggregate 테스트")
class OutboundSyncOutboxTest {

    @Nested
    @DisplayName("retry() - FAILED 상태 재처리")
    class RetryTest {

        @Test
        @DisplayName("FAILED 상태에서 retry() 호출 시 PENDING 상태로 전이한다")
        void retryFromFailedStatusTransitionsToPending() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.failedOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.retry(now);

            // then
            assertThat(outbox.status()).isEqualTo(SyncStatus.PENDING);
        }

        @Test
        @DisplayName("FAILED 상태에서 retry() 호출 시 retryCount가 0으로 초기화된다")
        void retryFromFailedStatusResetsRetryCountToZero() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.failedOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.retry(now);

            // then
            assertThat(outbox.retryCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("FAILED 상태에서 retry() 호출 시 errorMessage가 null로 초기화된다")
        void retryFromFailedStatusClearsErrorMessage() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.failedOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.retry(now);

            // then
            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("FAILED 상태에서 retry() 호출 시 updatedAt이 전달된 now로 갱신된다")
        void retryFromFailedStatusUpdatesUpdatedAtToNow() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.failedOutbox();
            Instant now = Instant.parse("2026-03-03T10:00:00Z");

            // when
            outbox.retry(now);

            // then
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PENDING 상태에서 retry() 호출 시 IllegalStateException이 발생한다")
        void retryFromPendingStatusThrowsIllegalStateException() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.pendingOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.retry(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED 상태에서만 재처리할 수 있습니다")
                    .hasMessageContaining("PENDING");
        }

        @Test
        @DisplayName("PROCESSING 상태에서 retry() 호출 시 IllegalStateException이 발생한다")
        void retryFromProcessingStatusThrowsIllegalStateException() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.processingOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.retry(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED 상태에서만 재처리할 수 있습니다")
                    .hasMessageContaining("PROCESSING");
        }

        @Test
        @DisplayName("COMPLETED 상태에서 retry() 호출 시 IllegalStateException이 발생한다")
        void retryFromCompletedStatusThrowsIllegalStateException() {
            // given
            OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.completedOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.retry(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED 상태에서만 재처리할 수 있습니다")
                    .hasMessageContaining("COMPLETED");
        }
    }
}
