package com.ryuqq.marketplace.application.exchange.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.ExchangeCommandFixtures;
import com.ryuqq.marketplace.application.exchange.dto.command.ProcessPendingExchangeOutboxCommand;
import com.ryuqq.marketplace.application.exchange.internal.ExchangeOutboxRelayProcessor;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxReadManager;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessPendingExchangeOutboxService 단위 테스트")
class ProcessPendingExchangeOutboxServiceTest {

    @InjectMocks private ProcessPendingExchangeOutboxService sut;

    @Mock private ExchangeOutboxReadManager outboxReadManager;
    @Mock private ExchangeOutboxRelayProcessor relayProcessor;

    @Nested
    @DisplayName("execute() - PENDING 교환 아웃박스 처리")
    class ExecuteTest {

        @Test
        @DisplayName("PENDING 아웃박스를 조회하여 릴레이하고 결과를 반환한다")
        void execute_PendingOutboxes_ReturnsProcessingResult() {
            // given
            ProcessPendingExchangeOutboxCommand command =
                    ExchangeCommandFixtures.processPendingOutboxCommand();
            ExchangeOutbox outbox1 = Mockito.mock(ExchangeOutbox.class);
            ExchangeOutbox outbox2 = Mockito.mock(ExchangeOutbox.class);

            given(outboxReadManager.findPendingOutboxes(any(), anyInt()))
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
            ProcessPendingExchangeOutboxCommand command =
                    ExchangeCommandFixtures.processPendingOutboxCommand();
            ExchangeOutbox outbox1 = Mockito.mock(ExchangeOutbox.class);
            ExchangeOutbox outbox2 = Mockito.mock(ExchangeOutbox.class);

            given(outboxReadManager.findPendingOutboxes(any(), anyInt()))
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
            ProcessPendingExchangeOutboxCommand command =
                    ExchangeCommandFixtures.processPendingOutboxCommand();

            given(outboxReadManager.findPendingOutboxes(any(), anyInt())).willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(0);
            assertThat(result.success()).isEqualTo(0);
            then(relayProcessor).shouldHaveNoInteractions();
        }
    }
}
