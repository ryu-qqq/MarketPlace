package com.ryuqq.marketplace.application.imageupload.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.ImageUploadCommandFixtures;
import com.ryuqq.marketplace.application.imageupload.dto.command.ProcessPendingImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.internal.ImageUploadOutboxProcessor;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxReadManager;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import java.util.List;
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
@DisplayName("ProcessPendingImageUploadService 단위 테스트")
class ProcessPendingImageUploadServiceTest {

    @InjectMocks private ProcessPendingImageUploadService sut;

    @Mock private ImageUploadOutboxReadManager outboxReadManager;
    @Mock private ImageUploadOutboxProcessor outboxProcessor;

    @Nested
    @DisplayName("execute() - PENDING Outbox 배치 처리")
    class ExecuteTest {

        @Test
        @DisplayName("모든 Outbox 처리 성공 시 성공 카운트가 일치하는 결과를 반환한다")
        void execute_AllOutboxesSucceed_ReturnsAllSuccessResult() {
            // given
            ProcessPendingImageUploadCommand command =
                    ImageUploadCommandFixtures.processPendingCommand();
            ImageUploadOutbox outbox1 = ImageUploadFixtures.pendingOutbox(1L);
            ImageUploadOutbox outbox2 = ImageUploadFixtures.pendingOutbox(2L);
            List<ImageUploadOutbox> outboxes = List.of(outbox1, outbox2);

            given(
                            outboxReadManager.findPendingOutboxesForRetry(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(outboxes);
            given(outboxProcessor.processOutbox(outbox1)).willReturn(true);
            given(outboxProcessor.processOutbox(outbox2)).willReturn(true);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.success()).isEqualTo(2);
            assertThat(result.failed()).isZero();
            assertThat(result.hasFailures()).isFalse();
        }

        @Test
        @DisplayName("일부 Outbox 처리 실패 시 실패 카운트가 반영된 결과를 반환한다")
        void execute_SomeOutboxesFail_ReturnsPartialFailureResult() {
            // given
            ProcessPendingImageUploadCommand command =
                    ImageUploadCommandFixtures.processPendingCommand();
            ImageUploadOutbox outbox1 = ImageUploadFixtures.pendingOutbox(1L);
            ImageUploadOutbox outbox2 = ImageUploadFixtures.pendingOutbox(2L);
            ImageUploadOutbox outbox3 = ImageUploadFixtures.pendingOutbox(3L);
            List<ImageUploadOutbox> outboxes = List.of(outbox1, outbox2, outbox3);

            given(
                            outboxReadManager.findPendingOutboxesForRetry(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(outboxes);
            given(outboxProcessor.processOutbox(outbox1)).willReturn(true);
            given(outboxProcessor.processOutbox(outbox2)).willReturn(false);
            given(outboxProcessor.processOutbox(outbox3)).willReturn(true);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(3);
            assertThat(result.success()).isEqualTo(2);
            assertThat(result.failed()).isEqualTo(1);
            assertThat(result.hasFailures()).isTrue();
        }

        @Test
        @DisplayName("처리할 Outbox가 없으면 빈 결과를 반환한다")
        void execute_NoOutboxes_ReturnsEmptyResult() {
            // given
            ProcessPendingImageUploadCommand command =
                    ImageUploadCommandFixtures.processPendingCommand();

            given(
                            outboxReadManager.findPendingOutboxesForRetry(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isZero();
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isZero();
            then(outboxProcessor).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("모든 Outbox 처리 실패 시 실패 카운트가 전체와 일치하는 결과를 반환한다")
        void execute_AllOutboxesFail_ReturnsAllFailedResult() {
            // given
            ProcessPendingImageUploadCommand command =
                    ImageUploadCommandFixtures.processPendingCommand();
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox(1L);
            List<ImageUploadOutbox> outboxes = List.of(outbox);

            given(
                            outboxReadManager.findPendingOutboxesForRetry(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(outboxes);
            given(outboxProcessor.processOutbox(outbox)).willReturn(false);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isEqualTo(1);
        }
    }
}
