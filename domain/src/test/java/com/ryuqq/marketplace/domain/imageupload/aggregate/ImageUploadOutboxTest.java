package com.ryuqq.marketplace.domain.imageupload.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageUploadOutbox Aggregate 테스트")
class ImageUploadOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 Outbox 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 Outbox를 생성한다")
        void createNewOutbox() {
            // given
            Long sourceId = 100L;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;
            String originUrl = "https://example.com/image.jpg";
            Instant now = CommonVoFixtures.now();

            // when
            ImageUploadOutbox outbox =
                    ImageUploadOutbox.forNew(sourceId, sourceType, originUrl, now);

            // then
            assertThat(outbox.isNew()).isTrue();
            assertThat(outbox.sourceId()).isEqualTo(sourceId);
            assertThat(outbox.sourceType()).isEqualTo(sourceType);
            assertThat(outbox.originUrlValue()).isEqualTo(originUrl);
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isZero();
            assertThat(outbox.maxRetry()).isEqualTo(3);
            assertThat(outbox.createdAt()).isEqualTo(now);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.processedAt()).isNull();
            assertThat(outbox.errorMessage()).isNull();
            assertThat(outbox.version()).isZero();
            assertThat(outbox.idempotencyKeyValue()).startsWith("IUO:PRODUCT_GROUP_IMAGE:");
        }

        @Test
        @DisplayName("DESCRIPTION_IMAGE 소스 타입으로 Outbox를 생성한다")
        void createNewOutboxWithDescriptionImage() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ImageUploadOutbox outbox =
                    ImageUploadOutbox.forNew(
                            200L,
                            ImageSourceType.DESCRIPTION_IMAGE,
                            "https://example.com/desc.png",
                            now);

            // then
            assertThat(outbox.sourceType()).isEqualTo(ImageSourceType.DESCRIPTION_IMAGE);
            assertThat(outbox.idempotencyKeyValue()).startsWith("IUO:DESCRIPTION_IMAGE:");
        }
    }

    @Nested
    @DisplayName("startProcessing() - 처리 시작")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태에서 PROCESSING으로 전환한다")
        void startProcessingFromPending() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.startProcessing(now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.PROCESSING);
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("COMPLETED 상태에서는 예외가 발생한다")
        void startProcessingFromCompleted_ThrowsException() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.completedOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }

        @Test
        @DisplayName("FAILED 상태에서는 예외가 발생한다")
        void startProcessingFromFailed_ThrowsException() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.failedOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
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
            ImageUploadOutbox outbox = ImageUploadFixtures.processingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.complete(now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.COMPLETED);
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
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.failAndRetry("네트워크 오류", now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo("네트워크 오류");
        }

        @Test
        @DisplayName("최대 재시도 횟수 초과 시 FAILED로 전환한다")
        void failAndRetry_ExceedsMaxRetry_TransitionsToFailed() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.failAndRetry("오류1", now);
            outbox.failAndRetry("오류2", now);
            outbox.failAndRetry("오류3", now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.FAILED);
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
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.fail("복구 불가능한 오류", now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.FAILED);
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
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recordFailure(true, "재시도 가능한 오류", now);

            // then
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("canRetry=false이면 즉시 FAILED 상태로 전환한다")
        void recordFailure_CannotRetry() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recordFailure(false, "재시도 불가능한 오류", now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 PENDING으로 복구한다")
        void recoverFromTimeout() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.processingOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recoverFromTimeout(now);

            // then
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.PENDING);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).isEqualTo("타임아웃으로 인한 복구");
        }

        @Test
        @DisplayName("PENDING 상태에서는 예외가 발생한다")
        void recoverFromTimeout_WhenPending_ThrowsException() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();

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
            ImageUploadOutbox outbox = ImageUploadFixtures.processingOutbox();
            Instant futureTime = Instant.now().plusSeconds(600);

            // when & then
            assertThat(outbox.isProcessingTimeout(futureTime, 300)).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 시간 미초과 시 false를 반환한다")
        void isProcessingTimeout_WhenNotExpired() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.processingOutbox();
            Instant nearFuture = Instant.now().plusSeconds(10);

            // when & then
            assertThat(outbox.isProcessingTimeout(nearFuture, 300)).isFalse();
        }

        @Test
        @DisplayName("PENDING 상태에서는 항상 false를 반환한다")
        void isProcessingTimeout_WhenPending() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();

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
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();

            // when & then
            assertThat(outbox.canRetry()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태에서는 재시도 불가하다")
        void canRetry_WhenFailed() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.failedOutbox();

            // when & then
            assertThat(outbox.canRetry()).isFalse();
        }
    }

    @Nested
    @DisplayName("shouldProcess() - 처리 대상 여부")
    class ShouldProcessTest {

        @Test
        @DisplayName("PENDING 상태이면 처리 대상이다")
        void shouldProcess_WhenPending() {
            assertThat(ImageUploadFixtures.pendingOutbox().shouldProcess()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태이면 처리 대상이 아니다")
        void shouldProcess_WhenProcessing() {
            assertThat(ImageUploadFixtures.processingOutbox().shouldProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("generateFilename() - 파일명 생성")
    class GenerateFilenameTest {

        @Test
        @DisplayName("sourceType_sourceId_epochMilli.extension 형식으로 파일명을 생성한다")
        void generateFilename() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = Instant.ofEpochMilli(1706612400000L);

            // when
            String filename = outbox.generateFilename(now);

            // then
            assertThat(filename).isEqualTo("product_group_image_100_1706612400000.jpg");
        }
    }

    @Nested
    @DisplayName("상태 편의 메서드 테스트")
    class StatusConvenienceMethodsTest {

        @Test
        @DisplayName("각 상태별 편의 메서드가 올바르게 동작한다")
        void statusConvenienceMethods() {
            assertThat(ImageUploadFixtures.pendingOutbox().isPending()).isTrue();
            assertThat(ImageUploadFixtures.processingOutbox().isProcessing()).isTrue();
            assertThat(ImageUploadFixtures.completedOutbox().isCompleted()).isTrue();
            assertThat(ImageUploadFixtures.failedOutbox().isFailed()).isTrue();
        }
    }
}
