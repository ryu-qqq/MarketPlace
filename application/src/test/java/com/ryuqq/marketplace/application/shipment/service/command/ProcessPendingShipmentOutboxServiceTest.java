package com.ryuqq.marketplace.application.shipment.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ProcessPendingShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentOutboxRelayProcessor;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
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
@DisplayName("ProcessPendingShipmentOutboxService 단위 테스트")
class ProcessPendingShipmentOutboxServiceTest {

    @InjectMocks private ProcessPendingShipmentOutboxService sut;

    @Mock private ShipmentOutboxReadManager outboxReadManager;
    @Mock private ShipmentOutboxRelayProcessor relayProcessor;

    @Nested
    @DisplayName("execute() - PENDING 배송 아웃박스 SQS 발행")
    class ExecuteTest {

        @Test
        @DisplayName("모든 Outbox를 SQS로 발행하면 전체 성공 결과를 반환한다")
        void execute_AllRelaySuccess_ReturnsAllSuccessResult() {
            // given
            ProcessPendingShipmentOutboxCommand command =
                    ProcessPendingShipmentOutboxCommand.of(100, 5);

            ShipmentOutbox outbox1 = ShipmentOutboxFixtures.pendingShipmentOutbox();
            ShipmentOutbox outbox2 = ShipmentOutboxFixtures.pendingShipmentOutbox();

            given(outboxReadManager.findPendingOutboxes(any(Instant.class), eq(100)))
                    .willReturn(List.of(outbox1, outbox2));
            given(relayProcessor.relay(outbox1)).willReturn(true);
            given(relayProcessor.relay(outbox2)).willReturn(true);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.success()).isEqualTo(2);
            assertThat(result.failed()).isZero();
        }

        @Test
        @DisplayName("일부 Outbox 발행에 실패하면 부분 성공 결과를 반환한다")
        void execute_PartialRelayFailure_ReturnsPartialResult() {
            // given
            ProcessPendingShipmentOutboxCommand command =
                    ProcessPendingShipmentOutboxCommand.of(100, 5);

            ShipmentOutbox outbox1 = ShipmentOutboxFixtures.pendingShipmentOutbox();
            ShipmentOutbox outbox2 = ShipmentOutboxFixtures.pendingShipmentOutbox();

            given(outboxReadManager.findPendingOutboxes(any(Instant.class), eq(100)))
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
        @DisplayName("PENDING Outbox가 없으면 빈 결과를 반환한다")
        void execute_NoPendingOutboxes_ReturnsEmptyResult() {
            // given
            ProcessPendingShipmentOutboxCommand command =
                    ProcessPendingShipmentOutboxCommand.of(100, 5);

            given(outboxReadManager.findPendingOutboxes(any(Instant.class), eq(100)))
                    .willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isZero();
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isZero();
            then(relayProcessor).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("모든 Outbox 발행에 실패하면 전체 실패 결과를 반환한다")
        void execute_AllRelayFailure_ReturnsAllFailureResult() {
            // given
            ProcessPendingShipmentOutboxCommand command =
                    ProcessPendingShipmentOutboxCommand.of(100, 5);

            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            given(outboxReadManager.findPendingOutboxes(any(Instant.class), eq(100)))
                    .willReturn(List.of(outbox));
            given(relayProcessor.relay(outbox)).willReturn(false);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isEqualTo(1);
        }
    }
}
