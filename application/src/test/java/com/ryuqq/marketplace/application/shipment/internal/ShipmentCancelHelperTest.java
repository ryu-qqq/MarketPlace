package com.ryuqq.marketplace.application.shipment.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
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
@DisplayName("ShipmentCancelHelper 단위 테스트")
class ShipmentCancelHelperTest {

    @InjectMocks private ShipmentCancelHelper sut;

    @Mock private ShipmentReadManager shipmentReadManager;

    private static final String ORDER_ITEM_ID_1 = "01940001-0000-7000-8000-000000000001";
    private static final Instant NOW = Instant.parse("2026-02-18T10:00:00Z");

    @Nested
    @DisplayName("cancelPreparingShipments() - PREPARING 상태 배송 취소")
    class CancelPreparingShipmentsTest {

        @Test
        @DisplayName("PREPARING 상태인 배송을 취소하여 반환한다")
        void cancelPreparingShipments_PreparingShipment_ReturnsCancelledList() {
            // given
            OrderItemId orderItemId = OrderItemId.of(ORDER_ITEM_ID_1);
            List<OrderItemId> orderItemIds = List.of(orderItemId);
            Shipment preparingShipment = ShipmentFixtures.preparingShipment();

            given(shipmentReadManager.findByOrderItemIds(orderItemIds))
                    .willReturn(List.of(preparingShipment));

            // when
            List<Shipment> result = sut.cancelPreparingShipments(orderItemIds, NOW);

            // then
            assertThat(result).hasSize(1);
            then(shipmentReadManager).should().findByOrderItemIds(orderItemIds);
        }

        @Test
        @DisplayName("PREPARING 상태가 아닌 배송은 취소 목록에 포함하지 않는다")
        void cancelPreparingShipments_NonPreparingShipment_ReturnsEmptyList() {
            // given
            OrderItemId orderItemId = OrderItemId.of(ORDER_ITEM_ID_1);
            List<OrderItemId> orderItemIds = List.of(orderItemId);
            Shipment readyShipment = ShipmentFixtures.readyShipment();

            given(shipmentReadManager.findByOrderItemIds(orderItemIds))
                    .willReturn(List.of(readyShipment));

            // when
            List<Shipment> result = sut.cancelPreparingShipments(orderItemIds, NOW);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("orderItemIds가 비어 있으면 조회 없이 빈 목록을 반환한다")
        void cancelPreparingShipments_EmptyOrderItemIds_ReturnsEmptyList() {
            // given
            List<OrderItemId> emptyIds = List.of();

            // when
            List<Shipment> result = sut.cancelPreparingShipments(emptyIds, NOW);

            // then
            assertThat(result).isEmpty();
            then(shipmentReadManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("조회된 배송이 없으면 빈 목록을 반환한다")
        void cancelPreparingShipments_NoShipmentsFound_ReturnsEmptyList() {
            // given
            OrderItemId orderItemId = OrderItemId.of(ORDER_ITEM_ID_1);
            List<OrderItemId> orderItemIds = List.of(orderItemId);

            given(shipmentReadManager.findByOrderItemIds(orderItemIds)).willReturn(List.of());

            // when
            List<Shipment> result = sut.cancelPreparingShipments(orderItemIds, NOW);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("혼합 상태의 배송 중 PREPARING 상태만 취소 목록에 포함된다")
        void cancelPreparingShipments_MixedStatusShipments_ReturnsOnlyPreparing() {
            // given
            OrderItemId orderItemId1 = OrderItemId.of("01940001-0000-7000-8000-000000000001");
            OrderItemId orderItemId2 = OrderItemId.of("01940001-0000-7000-8000-000000000002");
            List<OrderItemId> orderItemIds = List.of(orderItemId1, orderItemId2);

            Shipment preparingShipment = ShipmentFixtures.preparingShipment();
            Shipment readyShipment = ShipmentFixtures.readyShipment();

            given(shipmentReadManager.findByOrderItemIds(orderItemIds))
                    .willReturn(List.of(preparingShipment, readyShipment));

            // when
            List<Shipment> result = sut.cancelPreparingShipments(orderItemIds, NOW);

            // then
            assertThat(result).hasSize(1);
        }
    }
}
