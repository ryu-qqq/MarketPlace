package com.ryuqq.marketplace.domain.imagevariantsync.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariantsync.vo.ImageVariantSyncOutboxStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageVariantSyncOutbox 단위 테스트")
class ImageVariantSyncOutboxTest {

    private static final Long SOURCE_IMAGE_ID = 100L;
    private static final ImageSourceType SOURCE_TYPE = ImageSourceType.PRODUCT_GROUP_IMAGE;

    @Nested
    @DisplayName("forNew() - 새 Outbox 생성")
    class ForNewTest {

        @Test
        @DisplayName("생성 시 PENDING 상태이다")
        void shouldBePendingStatus() {
            Instant now = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, now);

            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.status()).isEqualTo(ImageVariantSyncOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("생성 시 retryCount는 0이다")
        void shouldHaveZeroRetryCount() {
            Instant now = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, now);

            assertThat(outbox.retryCount()).isZero();
        }

        @Test
        @DisplayName("생성 시 processedAt은 null이다")
        void shouldHaveNullProcessedAt() {
            Instant now = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, now);

            assertThat(outbox.processedAt()).isNull();
        }

        @Test
        @DisplayName("생성 시 isNew()가 true이다")
        void shouldBeNew() {
            Instant now = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, now);

            assertThat(outbox.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("complete() - 완료 처리")
    class CompleteTest {

        @Test
        @DisplayName("완료 시 COMPLETED 상태가 된다")
        void shouldBeCompleted() {
            Instant now = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, now);

            outbox.complete(now);

            assertThat(outbox.isCompleted()).isTrue();
            assertThat(outbox.status()).isEqualTo(ImageVariantSyncOutboxStatus.COMPLETED);
        }

        @Test
        @DisplayName("완료 시 processedAt이 설정된다")
        void shouldSetProcessedAt() {
            Instant created = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, created);

            Instant completedAt = created.plusSeconds(10);
            outbox.complete(completedAt);

            assertThat(outbox.processedAt()).isEqualTo(completedAt);
        }

        @Test
        @DisplayName("완료 시 errorMessage가 null로 초기화된다")
        void shouldClearErrorMessage() {
            Instant now = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, now);
            outbox.fail("임시 에러", now);

            outbox.complete(now.plusSeconds(10));

            assertThat(outbox.errorMessage()).isNull();
        }
    }

    @Nested
    @DisplayName("fail() - 실패 처리")
    class FailTest {

        @Test
        @DisplayName("재시도 횟수가 maxRetry 미만이면 PENDING 상태를 유지한다")
        void shouldRemainPendingWhenRetryCountBelowMax() {
            Instant now = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, now);

            outbox.fail("네트워크 오류", now);

            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo("네트워크 오류");
        }

        @Test
        @DisplayName("재시도 횟수가 maxRetry에 도달하면 FAILED 상태가 된다")
        void shouldBeFailedWhenRetryCountReachesMax() {
            Instant now = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, now);

            outbox.fail("에러 1", now);
            outbox.fail("에러 2", now);
            outbox.fail("에러 3", now);

            assertThat(outbox.status()).isEqualTo(ImageVariantSyncOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 시 processedAt이 설정된다")
        void shouldSetProcessedAtOnFailed() {
            Instant now = Instant.now();
            ImageVariantSyncOutbox outbox =
                    ImageVariantSyncOutbox.forNew(SOURCE_IMAGE_ID, SOURCE_TYPE, now);

            Instant failTime = now.plusSeconds(30);
            outbox.fail("에러", now);
            outbox.fail("에러", now);
            outbox.fail("에러", failTime);

            assertThat(outbox.processedAt()).isEqualTo(failTime);
        }
    }
}
