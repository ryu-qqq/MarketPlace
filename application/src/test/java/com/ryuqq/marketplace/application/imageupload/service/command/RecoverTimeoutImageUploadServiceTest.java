package com.ryuqq.marketplace.application.imageupload.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.ImageUploadCommandFixtures;
import com.ryuqq.marketplace.application.imageupload.dto.command.RecoverTimeoutImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
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
@DisplayName("RecoverTimeoutImageUploadService 단위 테스트")
class RecoverTimeoutImageUploadServiceTest {

    @InjectMocks private RecoverTimeoutImageUploadService sut;

    @Mock private ImageUploadOutboxReadManager outboxReadManager;
    @Mock private ImageUploadOutboxCommandManager outboxCommandManager;

    @Nested
    @DisplayName("execute() - 타임아웃 Outbox 복구")
    class ExecuteTest {

        @Test
        @DisplayName("모든 Outbox 복구 성공 시 성공 카운트가 일치하는 결과를 반환한다")
        void execute_AllOutboxesRecovered_ReturnsAllSuccessResult() {
            // given
            RecoverTimeoutImageUploadCommand command =
                    ImageUploadCommandFixtures.recoverTimeoutCommand();
            ImageUploadOutbox outbox1 = ImageUploadFixtures.processingOutbox();
            ImageUploadOutbox outbox2 = ImageUploadFixtures.processingOutbox();
            List<ImageUploadOutbox> outboxes = List.of(outbox1, outbox2);

            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(outboxes);
            given(outboxCommandManager.persist(outbox1)).willReturn(1L);
            given(outboxCommandManager.persist(outbox2)).willReturn(2L);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.success()).isEqualTo(2);
            assertThat(result.failed()).isZero();
            assertThat(result.hasFailures()).isFalse();
        }

        @Test
        @DisplayName("복구 중 예외 발생 시 해당 Outbox는 실패로 카운트된다")
        void execute_ExceptionDuringRecovery_CountsAsFailed() {
            // given
            RecoverTimeoutImageUploadCommand command =
                    ImageUploadCommandFixtures.recoverTimeoutCommand();
            ImageUploadOutbox outbox1 = ImageUploadFixtures.processingOutbox();
            ImageUploadOutbox outbox2 = ImageUploadFixtures.processingOutbox();
            List<ImageUploadOutbox> outboxes = List.of(outbox1, outbox2);

            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(outboxes);
            given(outboxCommandManager.persist(outbox1)).willReturn(1L);
            willThrow(new RuntimeException("DB 저장 실패"))
                    .given(outboxCommandManager)
                    .persist(outbox2);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.success()).isEqualTo(1);
            assertThat(result.failed()).isEqualTo(1);
            assertThat(result.hasFailures()).isTrue();
        }

        @Test
        @DisplayName("처리할 Outbox가 없으면 빈 결과를 반환한다")
        void execute_NoOutboxes_ReturnsEmptyResult() {
            // given
            RecoverTimeoutImageUploadCommand command =
                    ImageUploadCommandFixtures.recoverTimeoutCommand();

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
        @DisplayName("모든 Outbox 복구 실패 시 실패 카운트가 전체와 일치하는 결과를 반환한다")
        void execute_AllOutboxesFail_ReturnsAllFailedResult() {
            // given
            RecoverTimeoutImageUploadCommand command =
                    ImageUploadCommandFixtures.recoverTimeoutCommand();
            ImageUploadOutbox outbox = ImageUploadFixtures.processingOutbox();
            List<ImageUploadOutbox> outboxes = List.of(outbox);

            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    any(Instant.class), eq(command.batchSize())))
                    .willReturn(outboxes);
            willThrow(new RuntimeException("복구 실패")).given(outboxCommandManager).persist(outbox);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isEqualTo(1);
        }
    }
}
