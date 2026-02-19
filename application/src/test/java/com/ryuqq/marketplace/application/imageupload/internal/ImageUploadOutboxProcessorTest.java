package com.ryuqq.marketplace.application.imageupload.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadResponse;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.imageupload.factory.ImageUploadProcessBundleFactory;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
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
    @Mock private ImageUploadCompletionCoordinator completionCoordinator;
    @Mock private FileStorageManager fileStorageManager;
    @Mock private ImageUploadProcessBundleFactory bundleFactory;

    @Nested
    @DisplayName("processOutbox() - 단일 Outbox 처리")
    class ProcessOutboxTest {

        @Test
        @DisplayName("외부 URL 다운로드 성공 시 완료 처리를 수행하고 true를 반환한다")
        void processOutbox_SuccessfulDownload_CallsCompletionAndReturnsTrue() {
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
            ExternalDownloadResponse successResponse =
                    ExternalDownloadResponse.success(
                            ImageUploadFixtures.DEFAULT_ORIGIN_URL,
                            "https://cdn.example.com/new-image.jpg",
                            "asset-id-abc");

            given(outboxCommandManager.persist(outbox)).willReturn(1L);
            given(bundleFactory.create(any(ImageUploadOutbox.class), any(Instant.class)))
                    .willReturn(bundle);
            given(fileStorageManager.downloadFromExternalUrl(downloadRequest))
                    .willReturn(successResponse);

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isTrue();
            then(completionCoordinator)
                    .should()
                    .complete(
                            outbox,
                            successResponse.newCdnUrl(),
                            successResponse.fileAssetId(),
                            now);
        }

        @Test
        @DisplayName("외부 URL 다운로드 실패 시 Outbox에 실패를 기록하고 false를 반환한다")
        void processOutbox_FailedDownload_RecordsFailureAndReturnsFalse() {
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
            ExternalDownloadResponse failureResponse =
                    ExternalDownloadResponse.failure(
                            ImageUploadFixtures.DEFAULT_ORIGIN_URL, "다운로드 실패");

            given(outboxCommandManager.persist(outbox)).willReturn(1L);
            given(bundleFactory.create(any(ImageUploadOutbox.class), any(Instant.class)))
                    .willReturn(bundle);
            given(fileStorageManager.downloadFromExternalUrl(downloadRequest))
                    .willReturn(failureResponse);

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isFalse();
            then(completionCoordinator).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("처리 중 예외 발생 시 Outbox에 실패를 기록하고 false를 반환한다")
        void processOutbox_ExceptionDuringProcessing_RecordsFailureAndReturnsFalse() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();

            given(outboxCommandManager.persist(outbox)).willReturn(1L);
            given(bundleFactory.create(any(ImageUploadOutbox.class), any(Instant.class)))
                    .willThrow(new RuntimeException("Bundle 생성 실패"));

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isFalse();
            then(completionCoordinator).shouldHaveNoInteractions();
            then(outboxCommandManager)
                    .should(org.mockito.Mockito.atLeast(2))
                    .persist(any(ImageUploadOutbox.class));
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

            given(outboxCommandManager.persist(outbox)).willReturn(1L);
            given(bundleFactory.create(any(ImageUploadOutbox.class), any(Instant.class)))
                    .willReturn(bundle);
            given(fileStorageManager.downloadFromExternalUrl(downloadRequest))
                    .willThrow(new RuntimeException("외부 API 호출 실패"));

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isFalse();
            then(completionCoordinator).shouldHaveNoInteractions();
        }
    }
}
