package com.ryuqq.marketplace.application.shipment.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.ShipmentCommandFixtures;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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
@DisplayName("ShipmentBatchProcessor 단위 테스트")
class ShipmentBatchProcessorTest {

    @InjectMocks private ShipmentBatchProcessor sut;

    @Mock private ShipmentReadManager readManager;
    @Mock private ShipmentPersistFacade persistFacade;

    private static final String ORDER_ITEM_ID_1 = "01940001-0000-7000-8000-000000000001";

    @Nested
    @DisplayName("confirmBatch() - 발주확인 일괄 처리")
    class ConfirmBatchTest {

        @Test
        @DisplayName("PREPARING 상태로 전환 가능한 배송이 있으면 성공 처리한다")
        void confirmBatch_PreparableShipments_ReturnsSuccessResult() {
            // given
            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            OrderItemId orderItemId = OrderItemId.of(ORDER_ITEM_ID_1);
            BulkStatusChangeContext<OrderItemId> context =
                    new BulkStatusChangeContext<>(List.of(orderItemId), changedAt);

            Shipment shipment = ShipmentFixtures.readyShipment();
            given(readManager.findByOrderItemIds(List.of(orderItemId)))
                    .willReturn(List.of(shipment));

            // when
            BatchProcessingResult<String> result = sut.confirmBatch(context);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isZero();
            then(persistFacade).should().persistAll(any(ShipmentPersistenceBundle.class));
        }

        @Test
        @DisplayName("배송 정보가 없는 ID는 NOT_FOUND로 실패 처리한다")
        void confirmBatch_ShipmentNotFound_ReturnsNotFoundFailure() {
            // given
            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            OrderItemId orderItemId = OrderItemId.of(ORDER_ITEM_ID_1);
            BulkStatusChangeContext<OrderItemId> context =
                    new BulkStatusChangeContext<>(List.of(orderItemId), changedAt);

            given(readManager.findByOrderItemIds(List.of(orderItemId))).willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.confirmBatch(context);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isEqualTo(1);
            assertThat(result.results().get(0).errorCode()).isEqualTo("NOT_FOUND");
        }

        @Test
        @DisplayName("이미 PREPARING 상태인 배송은 INVALID_STATUS로 실패 처리한다")
        void confirmBatch_AlreadyPreparing_ReturnsInvalidStatusFailure() {
            // given
            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            OrderItemId orderItemId = OrderItemId.of(ORDER_ITEM_ID_1);
            BulkStatusChangeContext<OrderItemId> context =
                    new BulkStatusChangeContext<>(List.of(orderItemId), changedAt);

            Shipment shipment = ShipmentFixtures.preparingShipment();
            given(readManager.findByOrderItemIds(List.of(orderItemId)))
                    .willReturn(List.of(shipment));

            // when
            BatchProcessingResult<String> result = sut.confirmBatch(context);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isEqualTo(1);
            assertThat(result.results().get(0).errorCode()).isEqualTo("INVALID_STATUS");
        }
    }

    @Nested
    @DisplayName("shipBatch() - 송장등록 일괄 처리")
    class ShipBatchTest {

        @Test
        @DisplayName("PREPARING 상태 배송에 송장을 등록하면 성공 처리한다")
        void shipBatch_PreparingShipments_ReturnsSuccessResult() {
            // given
            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            OrderItemId orderItemId = OrderItemId.of(ORDER_ITEM_ID_1);
            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData shipData = ShipmentShipData.of("1234567890", method);
            UpdateContext<OrderItemId, ShipmentShipData> ctx =
                    new UpdateContext<>(orderItemId, shipData, changedAt);

            ShipBatchItem batchItem =
                    ShipmentCommandFixtures.shipBatchItem(
                            ORDER_ITEM_ID_1, "ORD-20260101-0001-001", "1234567890");
            Map<String, ShipBatchItem> itemMap = Map.of(ORDER_ITEM_ID_1, batchItem);

            Shipment shipment = ShipmentFixtures.preparingShipment();
            given(readManager.findByOrderItemIds(List.of(orderItemId)))
                    .willReturn(List.of(shipment));

            // when
            BatchProcessingResult<String> result = sut.shipBatch(List.of(ctx), itemMap);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isZero();
        }

        @Test
        @DisplayName("배송 정보가 없는 ID는 NOT_FOUND로 실패 처리한다")
        void shipBatch_ShipmentNotFound_ReturnsNotFoundFailure() {
            // given
            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            OrderItemId orderItemId = OrderItemId.of(ORDER_ITEM_ID_1);
            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData shipData = ShipmentShipData.of("1234567890", method);
            UpdateContext<OrderItemId, ShipmentShipData> ctx =
                    new UpdateContext<>(orderItemId, shipData, changedAt);

            ShipBatchItem batchItem =
                    ShipmentCommandFixtures.shipBatchItem(
                            ORDER_ITEM_ID_1, "ORD-20260101-0001-001", "1234567890");
            Map<String, ShipBatchItem> itemMap = Map.of(ORDER_ITEM_ID_1, batchItem);

            given(readManager.findByOrderItemIds(List.of(orderItemId))).willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.shipBatch(List.of(ctx), itemMap);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isEqualTo(1);
            assertThat(result.results().get(0).errorCode()).isEqualTo("NOT_FOUND");
        }

        @Test
        @DisplayName("READY 상태 배송에 송장 등록 시 INVALID_STATUS로 실패 처리한다")
        void shipBatch_ReadyShipment_ReturnsInvalidStatusFailure() {
            // given
            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            OrderItemId orderItemId = OrderItemId.of(ORDER_ITEM_ID_1);
            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData shipData = ShipmentShipData.of("1234567890", method);
            UpdateContext<OrderItemId, ShipmentShipData> ctx =
                    new UpdateContext<>(orderItemId, shipData, changedAt);

            ShipBatchItem batchItem =
                    ShipmentCommandFixtures.shipBatchItem(
                            ORDER_ITEM_ID_1, "ORD-20260101-0001-001", "1234567890");
            Map<String, ShipBatchItem> itemMap = Map.of(ORDER_ITEM_ID_1, batchItem);

            Shipment shipment = ShipmentFixtures.readyShipment();
            given(readManager.findByOrderItemIds(List.of(orderItemId)))
                    .willReturn(List.of(shipment));

            // when
            BatchProcessingResult<String> result = sut.shipBatch(List.of(ctx), itemMap);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isEqualTo(1);
            assertThat(result.results().get(0).errorCode()).isEqualTo("INVALID_STATUS");
        }
    }
}
