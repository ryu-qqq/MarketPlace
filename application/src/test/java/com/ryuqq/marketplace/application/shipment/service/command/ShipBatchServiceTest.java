package com.ryuqq.marketplace.application.shipment.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.ShipmentCommandFixtures;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentNotFoundException;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
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
@DisplayName("ShipBatchService 단위 테스트")
class ShipBatchServiceTest {

    @InjectMocks private ShipBatchService sut;

    @Mock private ShipmentReadManager readManager;
    @Mock private ShipmentCommandManager writeManager;
    @Mock private ShipmentCommandFactory commandFactory;

    @Nested
    @DisplayName("execute() - 송장등록 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("모든 항목이 성공하면 전체 성공 결과를 반환한다")
        void execute_AllSuccess_ReturnsAllSuccess() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            ShipmentId id1 = ShipmentId.of("id-1");
            ShipmentId id2 = ShipmentId.of("id-2");

            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData data1 = ShipmentShipData.of("tracking-1", method);
            ShipmentShipData data2 = ShipmentShipData.of("tracking-2", method);

            ShipBatchCommand command = ShipmentCommandFixtures.shipBatchCommand(2);

            List<UpdateContext<ShipmentId, ShipmentShipData>> contexts =
                    List.of(
                            new UpdateContext<>(id1, data1, now),
                            new UpdateContext<>(id2, data2, now));
            given(commandFactory.createShipContexts(command)).willReturn(contexts);

            Shipment shipment1 = ShipmentFixtures.preparingShipment();
            Shipment shipment2 = ShipmentFixtures.preparingShipment();
            given(readManager.getById(id1)).willReturn(shipment1);
            given(readManager.getById(id2)).willReturn(shipment2);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.successCount()).isEqualTo(2);
            assertThat(result.failureCount()).isZero();
            then(writeManager).should().persist(shipment1);
            then(writeManager).should().persist(shipment2);
        }

        @Test
        @DisplayName("일부 항목이 실패하면 부분 성공 결과를 반환한다")
        void execute_PartialFailure_ReturnsPartialResult() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            ShipmentId id1 = ShipmentId.of("id-1");
            ShipmentId id2 = ShipmentId.of("id-2");

            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData data1 = ShipmentShipData.of("tracking-1", method);
            ShipmentShipData data2 = ShipmentShipData.of("tracking-2", method);

            ShipBatchCommand command = ShipmentCommandFixtures.shipBatchCommand(2);

            List<UpdateContext<ShipmentId, ShipmentShipData>> contexts =
                    List.of(
                            new UpdateContext<>(id1, data1, now),
                            new UpdateContext<>(id2, data2, now));
            given(commandFactory.createShipContexts(command)).willReturn(contexts);

            Shipment shipment1 = ShipmentFixtures.preparingShipment();
            given(readManager.getById(id1)).willReturn(shipment1);
            given(readManager.getById(id2)).willThrow(new ShipmentNotFoundException("id-2"));

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(1);

            assertThat(result.results().get(0).success()).isTrue();
            assertThat(result.results().get(0).id()).isEqualTo("id-1");

            assertThat(result.results().get(1).success()).isFalse();
            assertThat(result.results().get(1).id()).isEqualTo("id-2");
        }

        @Test
        @DisplayName("모든 항목이 실패하면 전체 실패 결과를 반환한다")
        void execute_AllFailure_ReturnsAllFailure() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            ShipmentId id1 = ShipmentId.of("id-1");

            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData data1 = ShipmentShipData.of("tracking-1", method);

            ShipBatchCommand command = ShipmentCommandFixtures.shipBatchCommand(1);

            List<UpdateContext<ShipmentId, ShipmentShipData>> contexts =
                    List.of(new UpdateContext<>(id1, data1, now));
            given(commandFactory.createShipContexts(command)).willReturn(contexts);
            given(readManager.getById(id1)).willThrow(new ShipmentNotFoundException("id-1"));

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isEqualTo(1);
            verify(writeManager, never()).persist(org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("빈 목록이면 빈 결과를 반환한다")
        void execute_EmptyItems_ReturnsEmptyResult() {
            // given
            ShipBatchCommand command = new ShipBatchCommand(List.of());

            given(commandFactory.createShipContexts(command)).willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isZero();
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isZero();
        }
    }
}
