package com.ryuqq.marketplace.domain.imagetransform.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformOutboxStatus;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageTransformOutbox Aggregate 테스트")
class ImageTransformOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 Outbox 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 Outbox를 생성한다")
        void createNewOutbox() {
            // given
            Long sourceImageId = 100L;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;
            ImageUrl uploadedUrl = ImageUrl.of("https://cdn.example.com/image.jpg");
            ImageVariantType variantType = ImageVariantType.SMALL_WEBP;
            Instant now = CommonVoFixtures.now();

            // when
            ImageTransformOutbox outbox =
                    ImageTransformOutbox.forNew(
                            sourceImageId, sourceType, uploadedUrl, variantType, now);

            // then
            assertThat(outbox.isNew()).isTrue();
            assertThat(outbox.sourceImageId()).isEqualTo(sourceImageId);
            assertThat(outbox.sourceType()).isEqualTo(sourceType);
            assertThat(outbox.uploadedUrlValue()).isEqualTo("https://cdn.example.com/image.jpg");
            assertThat(outbox.variantType()).isEqualTo(variantType);
            assertThat(outbox.transformRequestId()).isNull();
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isZero();
            assertThat(outbox.maxRetry()).isEqualTo(3);
            assertThat(outbox.createdAt()).isEqualTo(now);
            assertThat(outbox.processedAt()).isNull();
            assertThat(outbox.errorMessage()).isNull();
            assertThat(outbox.version()).isZero();
            assertThat(outbox.idempotencyKeyValue()).startsWith("ITO:");
        }

        @Test
        @DisplayName("ORIGINAL_WEBP variantType으로 Outbox를 생성한다")
        void createNewOutboxWithOriginalWebp() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ImageTransformOutbox outbox =
                    ImageTransformOutbox.forNew(
                            200L,
                            ImageSourceType.DESCRIPTION_IMAGE,
                            ImageUrl.of("https://cdn.example.com/desc.jpg"),
                            ImageVariantType.ORIGINAL_WEBP,
                            now);

            // then
            assertThat(outbox.variantType()).isEqualTo(ImageVariantType.ORIGINAL_WEBP);
            assertThat(outbox.idempotencyKeyValue()).contains("ORIGINAL_WEBP");
        }
    }

    @Nested
    @DisplayName("startProcessing() - 처리 시작")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태에서 PROCESSING으로 전환하고 transformRequestId를 설정한다")
        void startProcessingFromPending() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();
            String requestId = "tr-req-123";

            // when
            outbox.startProcessing(now, requestId);

            // then
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.PROCESSING);
            assertThat(outbox.transformRequestId()).isEqualTo(requestId);
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("transformRequestId가 null이어도 PROCESSING으로 전환한다")
        void startProcessingWithNullRequestId() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.startProcessing(now, null);

            // then
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.PROCESSING);
            assertThat(outbox.transformRequestId()).isNull();
        }

        @Test
        @DisplayName("COMPLETED 상태에서는 예외가 발생한다")
        void startProcessingFromCompleted_ThrowsException() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.completedOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now(), "req-123"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }

        @Test
        @DisplayName("FAILED 상태에서는 예외가 발생한다")
        void startProcessingFromFailed_ThrowsException() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.failedOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now(), "req-123"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }
    }

    @Nested
    @DisplayName("complete() - 처리 완료")
    class CompleteTest {

        @Test
        @DisplayName("완료 처리 시 상태와 시각이 변경된다")
        void completeOutbox() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.complete(now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.COMPLETED);
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).isNull();
        }
    }

    @Nested
    @DisplayName("failAndRetry() - 실패 및 재시도")
    class FailAndRetryTest {

        @Test
        @DisplayName("재시도 횟수 미달 시 PENDING으로 복귀한다")
        void failAndRetry_UnderMaxRetry_ReturnsToPending() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.failAndRetry("변환 오류", now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo("변환 오류");
        }

        @Test
        @DisplayName("최대 재시도 횟수 초과 시 FAILED로 전환한다")
        void failAndRetry_ExceedsMaxRetry_TransitionsToFailed() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.failAndRetry("오류1", now);
            outbox.failAndRetry("오류2", now);
            outbox.failAndRetry("오류3", now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.processedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("fail() - 즉시 실패")
    class FailTest {

        @Test
        @DisplayName("즉시 FAILED 상태로 전환한다")
        void failImmediately() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.fail("복구 불가능한 오류", now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.FAILED);
            assertThat(outbox.errorMessage()).isEqualTo("복구 불가능한 오류");
            assertThat(outbox.processedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("recordFailure() - 실패 결과 반영")
    class RecordFailureTest {

        @Test
        @DisplayName("canRetry=true이면 failAndRetry를 호출한다")
        void recordFailure_CanRetry() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recordFailure(true, "재시도 가능한 오류", now);

            // then
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("canRetry=false이면 즉시 FAILED 상태로 전환한다")
        void recordFailure_CannotRetry() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recordFailure(false, "재시도 불가능한 오류", now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 PENDING으로 복구한다")
        void recoverFromTimeout() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recoverFromTimeout(now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageTransformOutboxStatus.PENDING);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).isEqualTo("타임아웃으로 인한 복구");
        }

        @Test
        @DisplayName("PENDING 상태에서는 예외가 발생한다")
        void recoverFromTimeout_WhenPending_ThrowsException() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.recoverFromTimeout(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING 상태에서만 가능");
        }
    }

    @Nested
    @DisplayName("isProcessingTimeout() - 타임아웃 판별")
    class IsProcessingTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 시간 초과 시 true를 반환한다")
        void isProcessingTimeout_WhenExpired() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            Instant futureTime = Instant.now().plusSeconds(600);

            // when & then
            assertThat(outbox.isProcessingTimeout(futureTime, 300)).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 시간 미초과 시 false를 반환한다")
        void isProcessingTimeout_WhenNotExpired() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            Instant nearFuture = Instant.now().plusSeconds(10);

            // when & then
            assertThat(outbox.isProcessingTimeout(nearFuture, 300)).isFalse();
        }

        @Test
        @DisplayName("PENDING 상태에서는 항상 false를 반환한다")
        void isProcessingTimeout_WhenPending() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();

            // when & then
            assertThat(outbox.isProcessingTimeout(Instant.now().plusSeconds(600), 300)).isFalse();
        }
    }

    @Nested
    @DisplayName("canRetry() - 재시도 가능 여부")
    class CanRetryTest {

        @Test
        @DisplayName("PENDING 상태에서 재시도 가능하다")
        void canRetry_WhenPending() {
            assertThat(ImageTransformFixtures.pendingOutbox().canRetry()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태에서는 재시도 불가하다")
        void canRetry_WhenFailed() {
            assertThat(ImageTransformFixtures.failedOutbox().canRetry()).isFalse();
        }
    }

    @Nested
    @DisplayName("상태 편의 메서드 테스트")
    class StatusConvenienceMethodsTest {

        @Test
        @DisplayName("각 상태별 편의 메서드가 올바르게 동작한다")
        void statusConvenienceMethods() {
            assertThat(ImageTransformFixtures.pendingOutbox().isPending()).isTrue();
            assertThat(ImageTransformFixtures.processingOutbox().isProcessing()).isTrue();
            assertThat(ImageTransformFixtures.completedOutbox().isCompleted()).isTrue();
            assertThat(ImageTransformFixtures.failedOutbox().isFailed()).isTrue();
        }
    }
}
