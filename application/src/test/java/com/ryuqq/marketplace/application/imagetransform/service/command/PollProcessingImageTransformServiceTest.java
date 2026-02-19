package com.ryuqq.marketplace.application.imagetransform.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagetransform.ImageTransformCommandFixtures;
import com.ryuqq.marketplace.application.imagetransform.dto.command.PollProcessingImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.internal.ImageTransformPollingProcessor;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxReadManager;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
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
@DisplayName("PollProcessingImageTransformService 단위 테스트")
class PollProcessingImageTransformServiceTest {

    @InjectMocks private PollProcessingImageTransformService sut;

    @Mock private ImageTransformOutboxReadManager outboxReadManager;
    @Mock private ImageTransformPollingProcessor pollingProcessor;

    @Nested
    @DisplayName("execute() - PROCESSING Outbox 폴링 배치 처리")
    class ExecuteTest {

        @Test
        @DisplayName("모든 Outbox 폴링 완료 시 성공 카운트가 일치하는 결과를 반환한다")
        void execute_AllOutboxesCompleted_ReturnsAllSuccessResult() {
            // given
            PollProcessingImageTransformCommand command =
                    ImageTransformCommandFixtures.pollProcessingCommand();
            ImageTransformOutbox outbox1 = ImageTransformFixtures.processingOutbox();
            ImageTransformOutbox outbox2 = ImageTransformFixtures.processingOutbox();
            List<ImageTransformOutbox> outboxes = List.of(outbox1, outbox2);

            given(outboxReadManager.findProcessingOutboxes(command.batchSize()))
                    .willReturn(outboxes);
            given(pollingProcessor.pollOutbox(outbox1)).willReturn(true);
            given(pollingProcessor.pollOutbox(outbox2)).willReturn(true);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.success()).isEqualTo(2);
            assertThat(result.failed()).isZero();
        }

        @Test
        @DisplayName("아직 처리 중인 Outbox는 성공/실패 카운트에 포함되지 않는다")
        void execute_OutboxStillProcessing_NotCountedAsSuccess() {
            // given
            PollProcessingImageTransformCommand command =
                    ImageTransformCommandFixtures.pollProcessingCommand();
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            List<ImageTransformOutbox> outboxes = List.of(outbox);

            given(outboxReadManager.findProcessingOutboxes(command.batchSize()))
                    .willReturn(outboxes);
            given(pollingProcessor.pollOutbox(outbox)).willReturn(false);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isEqualTo(1);
        }

        @Test
        @DisplayName("처리할 PROCESSING Outbox가 없으면 빈 결과를 반환한다")
        void execute_NoProcessingOutboxes_ReturnsEmptyResult() {
            // given
            PollProcessingImageTransformCommand command =
                    ImageTransformCommandFixtures.pollProcessingCommand();

            given(outboxReadManager.findProcessingOutboxes(command.batchSize()))
                    .willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isZero();
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isZero();
            then(pollingProcessor).shouldHaveNoInteractions();
        }
    }
}
