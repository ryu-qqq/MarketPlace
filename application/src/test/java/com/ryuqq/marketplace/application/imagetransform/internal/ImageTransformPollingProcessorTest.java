package com.ryuqq.marketplace.application.imagetransform.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagetransform.ImageTransformResponseFixtures;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
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
@DisplayName("ImageTransformPollingProcessor 단위 테스트")
class ImageTransformPollingProcessorTest {

    @InjectMocks private ImageTransformPollingProcessor sut;

    @Mock private ImageTransformOutboxCommandManager outboxCommandManager;
    @Mock private ImageTransformManager transformManager;
    @Mock private ImageTransformCompletionCoordinator completionCoordinator;

    @Nested
    @DisplayName("pollOutbox() - PROCESSING Outbox 폴링")
    class PollOutboxTest {

        @Test
        @DisplayName("변환이 완료된 경우 완료 처리 후 true를 반환한다")
        void pollOutbox_TransformCompleted_CallsCompletionCoordinatorAndReturnsTrue() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            ImageTransformResponse completedResponse =
                    ImageTransformResponseFixtures.completedResponse();

            given(transformManager.getTransformRequest(outbox.transformRequestId()))
                    .willReturn(completedResponse);

            // when
            boolean result = sut.pollOutbox(outbox);

            // then
            assertThat(result).isTrue();
            then(completionCoordinator)
                    .should()
                    .complete(eq(outbox), eq(completedResponse), any(Instant.class));
        }

        @Test
        @DisplayName("변환이 실패한 경우 실패를 기록하고 false를 반환한다")
        void pollOutbox_TransformFailed_RecordsFailureAndReturnsFalse() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            ImageTransformResponse failedResponse = ImageTransformResponseFixtures.failedResponse();

            given(transformManager.getTransformRequest(outbox.transformRequestId()))
                    .willReturn(failedResponse);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            boolean result = sut.pollOutbox(outbox);

            // then
            assertThat(result).isFalse();
            then(completionCoordinator).shouldHaveNoInteractions();
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("변환이 아직 진행 중인 경우 false를 반환하고 처리를 하지 않는다")
        void pollOutbox_TransformStillProcessing_ReturnsFalseWithNoAction() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            ImageTransformResponse processingResponse =
                    ImageTransformResponseFixtures.processingResponse();

            given(transformManager.getTransformRequest(outbox.transformRequestId()))
                    .willReturn(processingResponse);

            // when
            boolean result = sut.pollOutbox(outbox);

            // then
            assertThat(result).isFalse();
            then(completionCoordinator).shouldHaveNoInteractions();
            then(outboxCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("폴링 중 예외 발생 시 실패를 기록하고 false를 반환한다")
        void pollOutbox_ExceptionDuringPolling_RecordsFailureAndReturnsFalse() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();

            given(transformManager.getTransformRequest(outbox.transformRequestId()))
                    .willThrow(new RuntimeException("외부 API 호출 실패"));
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            boolean result = sut.pollOutbox(outbox);

            // then
            assertThat(result).isFalse();
            then(outboxCommandManager).should().persist(outbox);
            then(completionCoordinator).shouldHaveNoInteractions();
        }
    }
}
