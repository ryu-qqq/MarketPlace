package com.ryuqq.marketplace.application.imageupload.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.imageupload.factory.ImageUploadProcessBundleFactory;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ImageUploadOutboxProcessor 단위 테스트")
class ImageUploadOutboxProcessorTest {

    @InjectMocks private ImageUploadOutboxProcessor sut;

    @Mock private ImageUploadOutboxCommandManager outboxCommandManager;
    @Mock private FileStorageManager fileStorageManager;
    @Mock private ImageUploadProcessBundleFactory bundleFactory;

    @Nested
    @DisplayName("processOutbox() - 다운로드 태스크 생성 (논블로킹)")
    class ProcessOutboxTest {

        @Test
        @DisplayName("다운로드 태스크 생성 성공 시 PROCESSING 상태로 전환하고 true를 반환한다")
        void processOutbox_SuccessfulTaskCreation_ReturnsTrue() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = Instant.now();
            ExternalDownloadRequest downloadRequest =
                    new ExternalDownloadRequest(
                            ImageUploadFixtures.DEFAULT_ORIGIN_URL,
                            "product-images",
                            "product_100_123456.jpg");
            ImageUploadProcessBundle bundle =
                    new ImageUploadProcessBundle(outbox, downloadRequest, now);

            given(bundleFactory.create(any(ImageUploadOutbox.class), any(Instant.class)))
                    .willReturn(bundle);
            given(fileStorageManager.createDownloadTask(downloadRequest))
                    .willReturn("dtask-12345");
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isTrue();
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.PROCESSING);
            assertThat(outbox.downloadTaskId()).isEqualTo("dtask-12345");
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("ExternalServiceUnavailableException 발생 시 deferRetry하고 false를 반환한다")
        void processOutbox_CircuitBreakerOpen_DefersRetryAndReturnsFalse() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            ExternalDownloadRequest downloadRequest =
                    new ExternalDownloadRequest(
                            ImageUploadFixtures.DEFAULT_ORIGIN_URL,
                            "product-images",
                            "product_100_123456.jpg");
            ImageUploadProcessBundle bundle =
                    new ImageUploadProcessBundle(outbox, downloadRequest, Instant.now());

            given(bundleFactory.create(any(ImageUploadOutbox.class), any(Instant.class)))
                    .willReturn(bundle);
            given(fileStorageManager.createDownloadTask(downloadRequest))
                    .willThrow(new ExternalServiceUnavailableException("Circuit Breaker OPEN"));

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isFalse();
            assertThat(outbox.status()).isEqualTo(ImageUploadOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isZero();
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("일반 예외 발생 시 failAndRetry를 기록하고 false를 반환한다")
        void processOutbox_GeneralException_RecordsFailureAndReturnsFalse() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();

            given(bundleFactory.create(any(ImageUploadOutbox.class), any(Instant.class)))
                    .willThrow(new RuntimeException("Bundle 생성 실패"));

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isFalse();
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo("Bundle 생성 실패");
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("FileStorageManager 예외 발생 시 Outbox에 실패를 기록하고 false를 반환한다")
        void processOutbox_FileStorageException_RecordsFailureAndReturnsFalse() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = Instant.now();
            ExternalDownloadRequest downloadRequest =
                    new ExternalDownloadRequest(
                            ImageUploadFixtures.DEFAULT_ORIGIN_URL,
                            "product-images",
                            "product_100_123456.jpg");
            ImageUploadProcessBundle bundle =
                    new ImageUploadProcessBundle(outbox, downloadRequest, now);

            given(bundleFactory.create(any(ImageUploadOutbox.class), any(Instant.class)))
                    .willReturn(bundle);
            given(fileStorageManager.createDownloadTask(downloadRequest))
                    .willThrow(new RuntimeException("외부 API 호출 실패"));

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isFalse();
            assertThat(outbox.retryCount()).isEqualTo(1);
            then(outboxCommandManager).should().persist(outbox);
        }
    }
}
