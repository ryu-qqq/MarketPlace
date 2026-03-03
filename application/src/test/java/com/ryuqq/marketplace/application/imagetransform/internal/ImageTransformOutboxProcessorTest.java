package com.ryuqq.marketplace.application.imagetransform.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.application.imagetransform.ImageTransformResponseFixtures;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
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
@DisplayName("ImageTransformOutboxProcessor 단위 테스트")
class ImageTransformOutboxProcessorTest {

    @InjectMocks private ImageTransformOutboxProcessor sut;

    @Mock private ImageTransformOutboxCommandManager outboxCommandManager;
    @Mock private ImageTransformManager transformManager;

    @Nested
    @DisplayName("processOutbox() - PENDING Outbox 처리")
    class ProcessOutboxTest {

        @Test
        @DisplayName("변환 요청 생성 성공 시 true를 반환하고 Outbox가 PROCESSING 상태가 된다")
        void processOutbox_SuccessfulRequest_ReturnsTrueAndOutboxIsProcessing() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            ImageTransformResponse processingResponse =
                    ImageTransformResponseFixtures.processingResponse();

            given(outboxCommandManager.persist(any())).willReturn(1L);
            given(
                            transformManager.createTransformRequest(
                                    outbox.uploadedUrlValue(),
                                    outbox.variantType(),
                                    outbox.fileAssetId()))
                    .willReturn(processingResponse);

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isTrue();
            assertThat(outbox.isProcessing()).isTrue();
            then(outboxCommandManager).should(times(1)).persist(any());
        }

        @Test
        @DisplayName("변환 요청 생성 실패(예외) 시 false를 반환하고 Outbox에 실패가 기록된다")
        void processOutbox_ExceptionDuringRequest_ReturnsFalseAndRecordsFailure() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();

            given(outboxCommandManager.persist(any())).willReturn(1L);
            given(
                            transformManager.createTransformRequest(
                                    anyString(), any(ImageVariantType.class), anyString()))
                    .willThrow(new RuntimeException("외부 API 호출 실패"));

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isFalse();
            then(outboxCommandManager).should(times(1)).persist(any());
        }

        @Test
        @DisplayName("transformRequestId가 성공 응답에 설정된다")
        void processOutbox_SuccessfulRequest_SetsTransformRequestId() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            ImageTransformResponse processingResponse =
                    ImageTransformResponseFixtures.processingResponse();

            given(outboxCommandManager.persist(any())).willReturn(1L);
            given(
                            transformManager.createTransformRequest(
                                    outbox.uploadedUrlValue(),
                                    outbox.variantType(),
                                    outbox.fileAssetId()))
                    .willReturn(processingResponse);

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isTrue();
            assertThat(outbox.transformRequestId())
                    .isEqualTo(ImageTransformResponseFixtures.DEFAULT_TRANSFORM_REQUEST_ID);
        }

        @Test
        @DisplayName("API 호출 실패 시 persist가 실패해도 예외가 전파되지 않는다")
        void processOutbox_PersistFailureAfterApiFailure_DoesNotThrow() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();

            given(
                            transformManager.createTransformRequest(
                                    anyString(), any(ImageVariantType.class), anyString()))
                    .willThrow(new RuntimeException("외부 API 호출 실패"));
            given(outboxCommandManager.persist(any())).willThrow(new RuntimeException("DB 저장 실패"));

            // when
            boolean result = sut.processOutbox(outbox);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("API 호출 성공 시 persist는 정확히 1회 호출된다")
        void processOutbox_SuccessfulRequest_PersistsExactlyOnce() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.pendingOutbox();
            ImageTransformResponse processingResponse =
                    ImageTransformResponseFixtures.processingResponse();

            given(outboxCommandManager.persist(any())).willReturn(1L);
            given(
                            transformManager.createTransformRequest(
                                    outbox.uploadedUrlValue(),
                                    outbox.variantType(),
                                    outbox.fileAssetId()))
                    .willReturn(processingResponse);

            // when
            sut.processOutbox(outbox);

            // then
            then(outboxCommandManager).should(times(1)).persist(outbox);
        }
    }
}
