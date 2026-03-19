package com.ryuqq.marketplace.application.shipment.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.shipment.dto.command.ExecuteShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
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
@DisplayName("ExecuteShipmentOutboxService 단위 테스트")
class ExecuteShipmentOutboxServiceTest {

    @InjectMocks private ExecuteShipmentOutboxService sut;

    @Mock private ShipmentOutboxReadManager outboxReadManager;
    @Mock private ShipmentOutboxCommandManager outboxCommandManager;
    @Mock private ShipmentSyncStrategy syncStrategy;

    @Nested
    @DisplayName("execute() - 배송 Outbox 실행")
    class ExecuteTest {

        @Test
        @DisplayName("외부 동기화가 성공하면 Outbox를 COMPLETED 상태로 업데이트한다")
        void execute_SyncSuccess_CompletesOutbox() {
            // given
            Long outboxId = 1L;
            ExecuteShipmentOutboxCommand command =
                    ExecuteShipmentOutboxCommand.of(
                            outboxId, "01940001-0000-7000-8000-000000000001", "SHIP");

            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();

            given(outboxReadManager.getById(outboxId)).willReturn(outbox).willReturn(freshOutbox);
            given(syncStrategy.execute(outbox)).willReturn(OutboxSyncResult.success());

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 동기화가 실패하면 Outbox를 FAILED 상태로 업데이트한다")
        void execute_SyncFailure_RecordsFailure() {
            // given
            Long outboxId = 1L;
            ExecuteShipmentOutboxCommand command =
                    ExecuteShipmentOutboxCommand.of(
                            outboxId, "01940001-0000-7000-8000-000000000001", "SHIP");

            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();

            given(outboxReadManager.getById(outboxId)).willReturn(outbox).willReturn(freshOutbox);
            given(syncStrategy.execute(outbox))
                    .willReturn(OutboxSyncResult.failure(true, "외부 API 응답 오류"));

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 서비스 일시 장애 시 Outbox를 PENDING으로 복구한다")
        void execute_ExternalServiceUnavailable_RecoversToPending() {
            // given
            Long outboxId = 1L;
            ExecuteShipmentOutboxCommand command =
                    ExecuteShipmentOutboxCommand.of(
                            outboxId, "01940001-0000-7000-8000-000000000001", "SHIP");

            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();

            given(outboxReadManager.getById(outboxId)).willReturn(outbox).willReturn(freshOutbox);
            given(syncStrategy.execute(outbox))
                    .willThrow(new ExternalServiceUnavailableException("외부 서비스 일시 장애"));

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("예기치 않은 예외 발생 시 re-read 후 실패 상태로 업데이트한다")
        void execute_UnexpectedException_RecordsFailureWithReRead() {
            // given
            Long outboxId = 1L;
            ExecuteShipmentOutboxCommand command =
                    ExecuteShipmentOutboxCommand.of(
                            outboxId, "01940001-0000-7000-8000-000000000001", "SHIP");

            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();

            given(outboxReadManager.getById(outboxId)).willReturn(outbox).willReturn(freshOutbox);
            given(syncStrategy.execute(outbox)).willThrow(new RuntimeException("예기치 않은 오류"));

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(any(ShipmentOutbox.class));
        }
    }
}
