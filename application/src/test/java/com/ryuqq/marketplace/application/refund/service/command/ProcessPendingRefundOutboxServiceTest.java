package com.ryuqq.marketplace.application.refund.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.refund.RefundCommandFixtures;
import com.ryuqq.marketplace.application.refund.dto.command.ProcessPendingRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.internal.RefundOutboxRelayProcessor;
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
@DisplayName("ProcessPendingRefundOutboxService 단위 테스트")
class ProcessPendingRefundOutboxServiceTest {

    @InjectMocks private ProcessPendingRefundOutboxService sut;

    @Mock private RefundOutboxReadManager outboxReadManager;
    @Mock private RefundOutboxRelayProcessor relayProcessor;
    @Mock private RefundCommandFactory commandFactory;

    @Nested
    @DisplayName("execute() - PENDING 환불 아웃박스 처리")
    class ExecuteTest {

        @Test
        @DisplayName("PENDING 아웃박스를 조회하여 릴레이하고 결과를 반환한다")
        void execute_PendingOutboxes_ReturnsProcessingResult() {
            // given
            ProcessPendingRefundOutboxCommand command =
                    RefundCommandFixtures.processPendingOutboxCommand();
            RefundOutbox outbox1 = org.mockito.Mockito.mock(RefundOutbox.class);
            RefundOutbox outbox2 = org.mockito.Mockito.mock(RefundOutbox.class);
            Instant threshold = Instant.now();

            given(commandFactory.calculatePendingThreshold(command.delaySeconds()))
                    .willReturn(threshold);
            given(outboxReadManager.findPendingOutboxes(eq(threshold), anyInt()))
                    .willReturn(List.of(outbox1, outbox2));
            given(relayProcessor.relay(outbox1)).willReturn(true);
            given(relayProcessor.relay(outbox2)).willReturn(true);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.success()).isEqualTo(2);
            assertThat(result.failed()).isEqualTo(0);
        }

        @Test
        @DisplayName("일부 릴레이 실패 시 실패 건수를 정확히 집계한다")
        void execute_PartialRelayFailure_CountsFailures() {
            // given
            ProcessPendingRefundOutboxCommand command =
                    RefundCommandFixtures.processPendingOutboxCommand();
            RefundOutbox outbox1 = org.mockito.Mockito.mock(RefundOutbox.class);
            RefundOutbox outbox2 = org.mockito.Mockito.mock(RefundOutbox.class);
            Instant threshold = Instant.now();

            given(commandFactory.calculatePendingThreshold(command.delaySeconds()))
                    .willReturn(threshold);
            given(outboxReadManager.findPendingOutboxes(eq(threshold), anyInt()))
                    .willReturn(List.of(outbox1, outbox2));
            given(relayProcessor.relay(outbox1)).willReturn(true);
            given(relayProcessor.relay(outbox2)).willReturn(false);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.success()).isEqualTo(1);
            assertThat(result.failed()).isEqualTo(1);
        }

        @Test
        @DisplayName("PENDING 아웃박스가 없으면 처리 건수가 0이다")
        void execute_NoPendingOutboxes_ReturnsZeroResult() {
            // given
            ProcessPendingRefundOutboxCommand command =
                    RefundCommandFixtures.processPendingOutboxCommand();
            Instant threshold = Instant.now();

            given(commandFactory.calculatePendingThreshold(command.delaySeconds()))
                    .willReturn(threshold);
            given(outboxReadManager.findPendingOutboxes(eq(threshold), anyInt()))
                    .willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(0);
            assertThat(result.success()).isEqualTo(0);
            then(relayProcessor).shouldHaveNoInteractions();
        }
    }
}
