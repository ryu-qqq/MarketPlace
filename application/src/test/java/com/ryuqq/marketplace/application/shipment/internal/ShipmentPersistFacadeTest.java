package com.ryuqq.marketplace.application.shipment.internal;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
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
@DisplayName("ShipmentPersistFacade 단위 테스트")
class ShipmentPersistFacadeTest {

    @InjectMocks private ShipmentPersistFacade sut;

    @Mock private ShipmentCommandManager shipmentCommandManager;
    @Mock private ShipmentOutboxCommandManager outboxCommandManager;
    @Mock private OrderItemCommandManager orderItemCommandManager;

    @Nested
    @DisplayName("persistAll() - ShipmentPersistenceBundle 저장")
    class PersistAllTest {

        @Test
        @DisplayName("Shipment, Outbox, OrderItem을 함께 저장한다")
        void persistAll_ValidBundle_SavesAllComponents() {
            // given
            List<Shipment> shipments = List.of(ShipmentFixtures.preparingShipment());
            List<ShipmentOutbox> outboxes = List.of(ShipmentOutboxFixtures.newShipmentOutbox());
            List<OrderItem> orderItems =
                    List.of(OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.CONFIRMED));
            ShipmentPersistenceBundle bundle =
                    ShipmentPersistenceBundle.of(shipments, outboxes, orderItems);

            // when
            sut.persistAll(bundle);

            // then
            then(shipmentCommandManager).should().persistAll(shipments);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(orderItemCommandManager).should().persistAll(orderItems);
        }

        @Test
        @DisplayName("빈 번들이면 각 Manager를 호출하지 않는다")
        void persistAll_EmptyBundle_SkipsManagerCalls() {
            // given
            ShipmentPersistenceBundle bundle =
                    ShipmentPersistenceBundle.of(List.of(), List.of(), List.of());

            // when
            sut.persistAll(bundle);

            // then
            then(shipmentCommandManager).shouldHaveNoInteractions();
            then(outboxCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistAll() - Shipment + Outbox만 저장")
    class PersistAllShipmentsAndOutboxesTest {

        @Test
        @DisplayName("Shipment 목록과 Outbox 목록을 함께 저장한다")
        void persistAll_ShipmentsAndOutboxes_SavesBothComponents() {
            // given
            List<Shipment> shipments = List.of(ShipmentFixtures.preparingShipment());
            List<ShipmentOutbox> outboxes = List.of(ShipmentOutboxFixtures.newShipmentOutbox());
            ShipmentPersistenceBundle bundle =
                    ShipmentPersistenceBundle.ofShipmentsAndOutboxes(shipments, outboxes);

            // when
            sut.persistAll(bundle);

            // then
            then(shipmentCommandManager).should().persistAll(shipments);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistAll() - 단건 Shipment + Outbox 저장")
    class PersistAllSingleWithOutboxTest {

        @Test
        @DisplayName("단건 Shipment와 단건 Outbox를 함께 저장한다")
        void persistAll_SingleWithOutbox_SavesBothComponents() {
            // given
            Shipment shipment = ShipmentFixtures.shippedShipment();
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();
            ShipmentPersistenceBundle bundle =
                    ShipmentPersistenceBundle.ofSingleWithOutbox(shipment, outbox);

            // when
            sut.persistAll(bundle);

            // then
            then(shipmentCommandManager).should().persistAll(List.of(shipment));
            then(outboxCommandManager).should().persistAll(List.of(outbox));
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }
}
