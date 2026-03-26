package com.ryuqq.marketplace.application.cancel.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.cancel.CancelCommandFixtures;
import com.ryuqq.marketplace.application.cancel.dto.command.RecoverTimeoutCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxReadManager;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
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
@DisplayName("RecoverTimeoutCancelOutboxService 단위 테스트")
class RecoverTimeoutCancelOutboxServiceTest {

    @InjectMocks private RecoverTimeoutCancelOutboxService sut;

    @Mock private CancelOutboxReadManager outboxReadManager;
    @Mock private CancelOutboxCommandManager outboxCommandManager;
    @Mock private CancelCommandFactory commandFactory;

    @Nested
    @DisplayName("execute() - 타임아웃 아웃박스 복구")
    class ExecuteTest {

        @Test
        @DisplayName("타임아웃된 아웃박스를 복구하고 성공 결과를 반환한다")
        void execute_TimeoutOutboxes_ReturnsSuccessResult() {
            // given
            RecoverTimeoutCancelOutboxCommand command =
                    CancelCommandFixtures.recoverTimeoutOutboxCommand();
            CancelOutbox outbox = org.mockito.Mockito.mock(CancelOutbox.class);
            Instant now = Instant.now();
            Instant timeoutThreshold = Instant.now();

            given(commandFactory.calculateTimeoutThreshold(command.timeoutSeconds()))
                    .willReturn(timeoutThreshold);
            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    timeoutThreshold, command.batchSize()))
                    .willReturn(List.of(outbox));
            given(commandFactory.now()).willReturn(now);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isEqualTo(1);
            assertThat(result.failed()).isEqualTo(0);
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("복구 중 예외 발생 시 실패로 집계한다")
        void execute_RecoveryFailure_CountsFailures() {
            // given
            RecoverTimeoutCancelOutboxCommand command =
                    CancelCommandFixtures.recoverTimeoutOutboxCommand();
            CancelOutbox outbox = org.mockito.Mockito.mock(CancelOutbox.class);
            Instant now = Instant.now();
            Instant timeoutThreshold = Instant.now();

            given(commandFactory.calculateTimeoutThreshold(command.timeoutSeconds()))
                    .willReturn(timeoutThreshold);
            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    timeoutThreshold, command.batchSize()))
                    .willReturn(List.of(outbox));
            given(commandFactory.now()).willReturn(now);
            willThrow(new RuntimeException("복구 실패")).given(outbox).recoverFromTimeout(now);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isEqualTo(0);
            assertThat(result.failed()).isEqualTo(1);
            then(outboxCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("타임아웃 아웃박스가 없으면 처리 건수가 0이다")
        void execute_NoTimeoutOutboxes_ReturnsZeroResult() {
            // given
            RecoverTimeoutCancelOutboxCommand command =
                    CancelCommandFixtures.recoverTimeoutOutboxCommand();
            Instant timeoutThreshold = Instant.now();

            given(commandFactory.calculateTimeoutThreshold(command.timeoutSeconds()))
                    .willReturn(timeoutThreshold);
            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    timeoutThreshold, command.batchSize()))
                    .willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(0);
            then(outboxCommandManager).shouldHaveNoInteractions();
        }
    }
}
