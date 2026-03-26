package com.ryuqq.marketplace.application.shipment.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.RecoverTimeoutShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
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
@DisplayName("RecoverTimeoutShipmentOutboxService 단위 테스트")
class RecoverTimeoutShipmentOutboxServiceTest {

    @InjectMocks private RecoverTimeoutShipmentOutboxService sut;

    @Mock private ShipmentOutboxReadManager outboxReadManager;
    @Mock private ShipmentOutboxCommandManager outboxCommandManager;
    @Mock private ShipmentCommandFactory commandFactory;

    @Nested
    @DisplayName("execute() - 타임아웃 배송 아웃박스 복구")
    class ExecuteTest {

        private static final Instant NOW = Instant.parse("2026-02-18T10:00:00Z");
        private static final Long OUTBOX_ID = 1L;

        @Test
        @DisplayName("타임아웃된 Outbox를 PENDING으로 복구하고 성공 결과를 반환한다")
        void execute_TimeoutOutboxes_RecoversToPendingAndReturnsSuccessResult() {
            // given
            RecoverTimeoutShipmentOutboxCommand command =
                    RecoverTimeoutShipmentOutboxCommand.of(50, 300L);

            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            given(commandFactory.resolveTimeoutThreshold(command)).willReturn(NOW);
            given(commandFactory.createOutboxTransitionContext(outbox.idValue()))
                    .willReturn(new StatusChangeContext<>(outbox.idValue(), NOW));
            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    org.mockito.ArgumentMatchers.any(Instant.class),
                                    org.mockito.ArgumentMatchers.eq(50)))
                    .willReturn(List.of(outbox));

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isEqualTo(1);
            assertThat(result.failed()).isZero();
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("타임아웃된 Outbox가 없으면 빈 결과를 반환한다")
        void execute_NoTimeoutOutboxes_ReturnsEmptyResult() {
            // given
            RecoverTimeoutShipmentOutboxCommand command =
                    RecoverTimeoutShipmentOutboxCommand.of(50, 300L);

            given(commandFactory.resolveTimeoutThreshold(command)).willReturn(NOW);
            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    org.mockito.ArgumentMatchers.any(Instant.class),
                                    org.mockito.ArgumentMatchers.eq(50)))
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
        @DisplayName("복구 중 예외가 발생한 Outbox는 실패로 집계된다")
        void execute_ExceptionDuringRecovery_CountsAsFailure() {
            // given
            RecoverTimeoutShipmentOutboxCommand command =
                    RecoverTimeoutShipmentOutboxCommand.of(50, 300L);

            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            given(commandFactory.resolveTimeoutThreshold(command)).willReturn(NOW);
            given(commandFactory.createOutboxTransitionContext(outbox.idValue()))
                    .willReturn(new StatusChangeContext<>(outbox.idValue(), NOW));
            given(
                            outboxReadManager.findProcessingTimeoutOutboxes(
                                    org.mockito.ArgumentMatchers.any(Instant.class),
                                    org.mockito.ArgumentMatchers.eq(50)))
                    .willReturn(List.of(outbox));
            given(outboxCommandManager.persist(outbox)).willThrow(new RuntimeException("DB 연결 실패"));

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isEqualTo(1);
        }
    }
}
