package com.ryuqq.marketplace.application.imagetransform.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagetransform.ImageTransformCommandFixtures;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RecoverTimeoutImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxReadManager;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
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
@DisplayName("RecoverTimeoutImageTransformService 단위 테스트")
class RecoverTimeoutImageTransformServiceTest {

    @InjectMocks private RecoverTimeoutImageTransformService sut;

    @Mock private ImageTransformOutboxReadManager outboxReadManager;
    @Mock private ImageTransformOutboxCommandManager outboxCommandManager;

    @Nested
    @DisplayName("execute() - 타임아웃 Outbox 복구")
    class ExecuteTest {

        @Test
        @DisplayName("타임아웃 Outbox를 PENDING 상태로 복구하고 성공 카운트를 반환한다")
        void execute_TimeoutOutboxes_RecoversToPendingAndReturnsSuccessCount() {
            // given
            RecoverTimeoutImageTransformCommand command =
                    ImageTransformCommandFixtures.recoverTimeoutCommand();
            ImageTransformOutbox outbox1 = ImageTransformFixtures.processingOutbox();
            ImageTransformOutbox outbox2 = ImageTransformFixtures.processingOutbox();
            List<ImageTransformOutbox> timeoutOutboxes = List.of(outbox1, outbox2);

            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(timeoutOutboxes);
            given(outboxCommandManager.persist(any(ImageTransformOutbox.class))).willReturn(1L);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.success()).isEqualTo(2);
            assertThat(result.failed()).isZero();
        }

        @Test
        @DisplayName("타임아웃 Outbox가 없으면 빈 결과를 반환한다")
        void execute_NoTimeoutOutboxes_ReturnsEmptyResult() {
            // given
            RecoverTimeoutImageTransformCommand command =
                    ImageTransformCommandFixtures.recoverTimeoutCommand();

            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isZero();
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isZero();
            then(outboxCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("복구 중 persist 예외 발생 시 실패 카운트에 반영된다")
        void execute_PersistFails_CountsAsFailure() {
            // given
            RecoverTimeoutImageTransformCommand command =
                    ImageTransformCommandFixtures.recoverTimeoutCommand();
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            List<ImageTransformOutbox> timeoutOutboxes = List.of(outbox);

            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(timeoutOutboxes);
            willThrow(new RuntimeException("DB 저장 실패"))
                    .given(outboxCommandManager)
                    .persist(any(ImageTransformOutbox.class));

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isEqualTo(1);
            assertThat(result.hasFailures()).isTrue();
        }

        @Test
        @DisplayName("복구 완료된 Outbox는 PENDING 상태가 된다")
        void execute_AfterRecovery_OutboxIsPending() {
            // given
            RecoverTimeoutImageTransformCommand command =
                    ImageTransformCommandFixtures.recoverTimeoutCommand();
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            List<ImageTransformOutbox> timeoutOutboxes = List.of(outbox);

            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(timeoutOutboxes);
            given(outboxCommandManager.persist(any(ImageTransformOutbox.class))).willReturn(1L);

            // when
            sut.execute(command);

            // then
            assertThat(outbox.isPending()).isTrue();
        }
    }
}
