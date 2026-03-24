package com.ryuqq.marketplace.application.shipment.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.ShipmentCommandFixtures;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentBatchProcessor;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
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

    @Mock private ShipmentCommandFactory commandFactory;
    @Mock private ShipmentBatchProcessor batchProcessor;
    @Mock private com.ryuqq.marketplace.application.order.manager.OrderItemReadManager orderItemReadManager;

    @Nested
    @DisplayName("execute() - 송장등록 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("모든 항목이 성공하면 전체 성공 결과를 반환한다")
        void execute_AllSuccess_ReturnsAllSuccess() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            OrderItemId id1 = OrderItemId.of("01940001-0000-7000-8000-000000000001");
            OrderItemId id2 = OrderItemId.of("01940001-0000-7000-8000-000000000002");

            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData data1 = ShipmentShipData.of("tracking-1", method);
            ShipmentShipData data2 = ShipmentShipData.of("tracking-2", method);

            ShipBatchCommand command = ShipmentCommandFixtures.shipBatchCommand(2);

            // orderItemNumber → OrderItem mock
            for (ShipBatchCommand.ShipBatchItem item : command.items()) {
                com.ryuqq.marketplace.domain.order.aggregate.OrderItem mockItem =
                        org.mockito.Mockito.mock(com.ryuqq.marketplace.domain.order.aggregate.OrderItem.class);
                given(mockItem.id()).willReturn(OrderItemId.of(item.orderItemId()));
                given(orderItemReadManager.getByOrderItemNumber(item.orderItemNumber())).willReturn(mockItem);
            }

            List<UpdateContext<OrderItemId, ShipmentShipData>> contexts =
                    List.of(
                            new UpdateContext<>(id1, data1, now),
                            new UpdateContext<>(id2, data2, now));
            given(commandFactory.createShipContexts(any())).willReturn(contexts);

            BatchProcessingResult<String> expected =
                    BatchProcessingResult.from(
                            List.of(BatchItemResult.success("1"), BatchItemResult.success("2")));
            given(batchProcessor.shipBatch(any(), anyMap())).willReturn(expected);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.successCount()).isEqualTo(2);
            assertThat(result.failureCount()).isZero();
        }

        @Test
        @DisplayName("일부 항목이 실패하면 부분 성공 결과를 반환한다")
        void execute_PartialFailure_ReturnsPartialResult() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            OrderItemId id1 = OrderItemId.of("01940001-0000-7000-8000-000000000001");
            OrderItemId id2 = OrderItemId.of("01940001-0000-7000-8000-000000000002");

            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData data1 = ShipmentShipData.of("tracking-1", method);
            ShipmentShipData data2 = ShipmentShipData.of("tracking-2", method);

            ShipBatchCommand command = ShipmentCommandFixtures.shipBatchCommand(2);

            for (ShipBatchCommand.ShipBatchItem item : command.items()) {
                com.ryuqq.marketplace.domain.order.aggregate.OrderItem mockItem =
                        org.mockito.Mockito.mock(com.ryuqq.marketplace.domain.order.aggregate.OrderItem.class);
                given(mockItem.id()).willReturn(OrderItemId.of(item.orderItemId()));
                given(orderItemReadManager.getByOrderItemNumber(item.orderItemNumber())).willReturn(mockItem);
            }

            List<UpdateContext<OrderItemId, ShipmentShipData>> contexts =
                    List.of(
                            new UpdateContext<>(id1, data1, now),
                            new UpdateContext<>(id2, data2, now));
            given(commandFactory.createShipContexts(any())).willReturn(contexts);

            BatchProcessingResult<String> expected =
                    BatchProcessingResult.from(
                            List.of(
                                    BatchItemResult.success("1"),
                                    BatchItemResult.failure(
                                            "2", "NOT_FOUND", "배송 정보를 찾을 수 없습니다: 2")));
            given(batchProcessor.shipBatch(any(), anyMap())).willReturn(expected);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(1);
            assertThat(result.results().get(0).success()).isTrue();
            assertThat(result.results().get(1).success()).isFalse();
        }

        @Test
        @DisplayName("모든 항목이 실패하면 전체 실패 결과를 반환한다")
        void execute_AllFailure_ReturnsAllFailure() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            OrderItemId id1 = OrderItemId.of("01940001-0000-7000-8000-000000000001");

            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData data1 = ShipmentShipData.of("tracking-1", method);

            ShipBatchCommand command = ShipmentCommandFixtures.shipBatchCommand(1);

            for (ShipBatchCommand.ShipBatchItem item : command.items()) {
                com.ryuqq.marketplace.domain.order.aggregate.OrderItem mockItem =
                        org.mockito.Mockito.mock(com.ryuqq.marketplace.domain.order.aggregate.OrderItem.class);
                given(mockItem.id()).willReturn(OrderItemId.of(item.orderItemId()));
                given(orderItemReadManager.getByOrderItemNumber(item.orderItemNumber())).willReturn(mockItem);
            }

            List<UpdateContext<OrderItemId, ShipmentShipData>> contexts =
                    List.of(new UpdateContext<>(id1, data1, now));
            given(commandFactory.createShipContexts(any())).willReturn(contexts);

            BatchProcessingResult<String> expected =
                    BatchProcessingResult.from(
                            List.of(
                                    BatchItemResult.failure(
                                            "1", "NOT_FOUND", "배송 정보를 찾을 수 없습니다: 1")));
            given(batchProcessor.shipBatch(any(), anyMap())).willReturn(expected);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("빈 목록이면 빈 결과를 반환한다")
        void execute_EmptyItems_ReturnsEmptyResult() {
            // given
            ShipBatchCommand command = new ShipBatchCommand(List.of());

            given(commandFactory.createShipContexts(command)).willReturn(List.of());

            BatchProcessingResult<String> expected = BatchProcessingResult.from(List.of());
            given(batchProcessor.shipBatch(any(), anyMap())).willReturn(expected);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isZero();
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isZero();
        }
    }
}
