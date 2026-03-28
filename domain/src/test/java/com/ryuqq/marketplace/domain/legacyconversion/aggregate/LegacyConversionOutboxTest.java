package com.ryuqq.marketplace.domain.legacyconversion.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyConversionOutbox Aggregate 테스트")
class LegacyConversionOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 Outbox 생성")
    class ForNewTest {

        @Test
        @DisplayName("legacyProductGroupId와 현재 시각으로 신규 Outbox를 생성한다")
        void createNewOutboxWithLegacyProductGroupId() {
            // given
            long legacyProductGroupId = LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_GROUP_ID;
            Instant now = CommonVoFixtures.now();

            // when
            LegacyConversionOutbox outbox =
                    LegacyConversionOutbox.forNew(legacyProductGroupId, now);

            // then
            assertThat(outbox.isNew()).isTrue();
            assertThat(outbox.id().isNew()).isTrue();
            assertThat(outbox.legacyProductGroupId()).isEqualTo(legacyProductGroupId);
            assertThat(outbox.status()).isEqualTo(LegacyConversionOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isZero();
            assertThat(outbox.maxRetry()).isEqualTo(3);
            assertThat(outbox.createdAt()).isEqualTo(now);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.processedAt()).isNull();
            assertThat(outbox.errorMessage()).isNull();
            assertThat(outbox.version()).isZero();
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("PENDING 상태의 Outbox를 재구성한다")
        void reconstitutePendingOutbox() {
            // given
            LegacyConversionOutboxId id =
                    LegacyConversionOutboxId.of(LegacyConversionFixtures.DEFAULT_OUTBOX_ID);
            Instant now = CommonVoFixtures.now();

            // when
            LegacyConversionOutbox outbox =
                    LegacyConversionOutbox.reconstitute(
                            id,
                            LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                            LegacyConversionOutboxStatus.PENDING,
                            0,
                            3,
                            now,
                            now,
                            null,
                            null,
                            0L);

            // then
            assertThat(outbox.isNew()).isFalse();
            assertThat(outbox.id()).isEqualTo(id);
            assertThat(outbox.idValue()).isEqualTo(LegacyConversionFixtures.DEFAULT_OUTBOX_ID);
            assertThat(outbox.status()).isEqualTo(LegacyConversionOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("FAILED 상태의 Outbox를 에러메시지와 함께 재구성한다")
        void reconstituteFailedOutboxWithErrorMessage() {
            // given
            String errorMessage = "변환 실패";

            // when
            LegacyConversionOutbox outbox = LegacyConversionFixtures.failedOutbox();

            // then
            assertThat(outbox.status()).isEqualTo(LegacyConversionOutboxStatus.FAILED);
            assertThat(outbox.errorMessage()).isNotNull();
            assertThat(outbox.processedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("startProcessing() - 처리 시작")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태에서 처리를 시작하면 PROCESSING으로 변경된다")
        void startProcessingFromPending() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.startProcessing(now);

            // then
            assertThat(outbox.status()).isEqualTo(LegacyConversionOutboxStatus.PROCESSING);
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 처리 시작 시 예외가 발생한다")
        void startProcessingFromCompleted_ThrowsException() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.completedOutbox();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(now))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }

        @Test
        @DisplayName("FAILED 상태에서 처리 시작 시 예외가 발생한다")
        void startProcessingFromFailed_ThrowsException() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.failedOutbox();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(now))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }
    }

    @Nested
    @DisplayName("complete() - 처리 완료")
    class CompleteTest {

        @Test
        @DisplayName("처리를 완료하면 COMPLETED 상태로 변경된다")
        void complete() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.processingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.complete(now);

            // then
            assertThat(outbox.status()).isEqualTo(LegacyConversionOutboxStatus.COMPLETED);
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).isNull();
        }
    }

    @Nested
    @DisplayName("failAndRetry() - 실패 및 재시도")
    class FailAndRetryTest {

        @Test
        @DisplayName("실패 후 재시도 가능하면 PENDING 상태로 재설정된다")
        void failAndRetry_WhenRetryAvailable() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.pendingOutbox();
            String errorMessage = "변환 서버 연결 실패";
            Instant now = CommonVoFixtures.now();

            // when
            outbox.failAndRetry(errorMessage, now);

            // then
            assertThat(outbox.status()).isEqualTo(LegacyConversionOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo(errorMessage);
            assertThat(outbox.processedAt()).isNull();
        }

        @Test
        @DisplayName("최대 재시도 횟수 초과 시 FAILED 상태로 변경된다")
        void failAndRetry_WhenMaxRetryExceeded() {
            // given: retryCount=1인 Outbox에서 2번 더 실패하면 총 3번 → FAILED
            LegacyConversionOutbox outbox = LegacyConversionFixtures.retriedPendingOutbox(1);
            String errorMessage = "변환 서버 연결 실패";
            Instant now = CommonVoFixtures.now();

            // when
            outbox.failAndRetry(errorMessage, now);
            outbox.failAndRetry(errorMessage, now);

            // then
            assertThat(outbox.status()).isEqualTo(LegacyConversionOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("retryCount가 증가하고 에러 메시지가 저장된다")
        void failAndRetry_IncreasesRetryCount() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.failAndRetry("첫 번째 실패", now);

            // then
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 복구 시 PENDING으로 변경된다")
        void recoverFromTimeout_FromProcessing() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.processingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recoverFromTimeout(now);

            // then
            assertThat(outbox.status()).isEqualTo(LegacyConversionOutboxStatus.PENDING);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).contains("타임아웃");
        }

        @Test
        @DisplayName("PENDING 상태에서 타임아웃 복구 시 예외가 발생한다")
        void recoverFromTimeout_FromPending_ThrowsException() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> outbox.recoverFromTimeout(now))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING 상태에서만");
        }
    }

    @Nested
    @DisplayName("canRetry() - 재시도 가능 여부")
    class CanRetryTest {

        @Test
        @DisplayName("PENDING 상태이고 재시도 횟수가 남았으면 true를 반환한다")
        void canRetry_WhenPendingAndRetryAvailable() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.pendingOutbox();

            // when & then
            assertThat(outbox.canRetry()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태이면 false를 반환한다")
        void canRetry_WhenFailed() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.failedOutbox();

            // when & then
            assertThat(outbox.canRetry()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태이면 false를 반환한다")
        void canRetry_WhenCompleted() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.completedOutbox();

            // when & then
            assertThat(outbox.canRetry()).isFalse();
        }
    }

    @Nested
    @DisplayName("refreshVersion() - 버전 갱신")
    class RefreshVersionTest {

        @Test
        @DisplayName("영속화 후 버전을 갱신한다")
        void refreshVersion() {
            // given
            LegacyConversionOutbox outbox = LegacyConversionFixtures.pendingOutbox();

            // when
            outbox.refreshVersion(5L);

            // then
            assertThat(outbox.version()).isEqualTo(5L);
        }
    }
}
