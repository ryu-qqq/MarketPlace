package com.ryuqq.marketplace.application.refund.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.refund.RefundCommandFixtures;
import com.ryuqq.marketplace.application.refund.dto.command.RecoverTimeoutRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxReadManager;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
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
@DisplayName("RecoverTimeoutRefundOutboxService 단위 테스트")
class RecoverTimeoutRefundOutboxServiceTest {

    @InjectMocks private RecoverTimeoutRefundOutboxService sut;

    @Mock private RefundOutboxReadManager outboxReadManager;
    @Mock private RefundOutboxCommandManager outboxCommandManager;
    @Mock private RefundCommandFactory commandFactory;

    @Nested
    @DisplayName("execute() - 타임아웃 환불 아웃박스 복구")
    class ExecuteTest {

        @Test
        @DisplayName("타임아웃된 아웃박스를 복구하고 성공 결과를 반환한다")
        void execute_TimeoutOutboxes_ReturnsSuccessResult() {
            // given
            RecoverTimeoutRefundOutboxCommand command =
                    RefundCommandFixtures.recoverTimeoutOutboxCommand();
            RefundOutbox outbox = org.mockito.Mockito.mock(RefundOutbox.class);
            Long outboxId = 1L;
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            Instant now = Instant.now();

            given(commandFactory.calculateTimeoutThreshold(command.timeoutSeconds()))
                    .willReturn(timeoutThreshold);
            given(outboxReadManager.findProcessingTimeoutOutboxes(eq(timeoutThreshold), anyInt()))
                    .willReturn(List.of(outbox));
            given(outbox.idValue()).willReturn(outboxId);
            given(commandFactory.createOutboxChangeContext(outboxId))
                    .willReturn(new StatusChangeContext<>(outboxId, now));

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
            RecoverTimeoutRefundOutboxCommand command =
                    RefundCommandFixtures.recoverTimeoutOutboxCommand();
            RefundOutbox outbox = org.mockito.Mockito.mock(RefundOutbox.class);
            Long outboxId = 1L;
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            Instant now = Instant.now();

            given(commandFactory.calculateTimeoutThreshold(command.timeoutSeconds()))
                    .willReturn(timeoutThreshold);
            given(outboxReadManager.findProcessingTimeoutOutboxes(eq(timeoutThreshold), anyInt()))
                    .willReturn(List.of(outbox));
            given(outbox.idValue()).willReturn(outboxId);
            given(commandFactory.createOutboxChangeContext(outboxId))
                    .willReturn(new StatusChangeContext<>(outboxId, now));
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
            RecoverTimeoutRefundOutboxCommand command =
                    RefundCommandFixtures.recoverTimeoutOutboxCommand();
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            given(commandFactory.calculateTimeoutThreshold(command.timeoutSeconds()))
                    .willReturn(timeoutThreshold);
            given(outboxReadManager.findProcessingTimeoutOutboxes(eq(timeoutThreshold), anyInt()))
                    .willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(0);
            then(outboxCommandManager).shouldHaveNoInteractions();
        }
    }
}
