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
    @DisplayName("persistConfirmBundle() - 발주확인 번들 일괄 저장")
    class PersistConfirmBundleTest {

        @Test
        @DisplayName("Shipment, Outbox, OrderItem을 함께 저장한다")
        void persistConfirmBundle_ValidBundle_SavesAllComponents() {
            // given
            List<Shipment> shipments = List.of(ShipmentFixtures.preparingShipment());
            List<ShipmentOutbox> outboxes = List.of(ShipmentOutboxFixtures.newShipmentOutbox());
            List<OrderItem> orderItems =
                    List.of(OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.CONFIRMED));
            ConfirmShipmentBundle bundle =
                    new ConfirmShipmentBundle(shipments, outboxes, orderItems);

            // when
            sut.persistConfirmBundle(bundle);

            // then
            then(shipmentCommandManager).should().persistAll(shipments);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(orderItemCommandManager).should().persistAll(orderItems);
        }

        @Test
        @DisplayName("빈 번들이어도 각 Manager를 호출한다")
        void persistConfirmBundle_EmptyBundle_StillCallsManagers() {
            // given
            ConfirmShipmentBundle bundle =
                    new ConfirmShipmentBundle(List.of(), List.of(), List.of());

            // when
            sut.persistConfirmBundle(bundle);

            // then
            then(shipmentCommandManager).should().persistAll(List.of());
            then(outboxCommandManager).should().persistAll(List.of());
            then(orderItemCommandManager).should().persistAll(List.of());
        }
    }

    @Nested
    @DisplayName("persistAllWithOutboxes() - Shipment + Outbox 일괄 저장")
    class PersistAllWithOutboxesTest {

        @Test
        @DisplayName("Shipment 목록과 Outbox 목록을 함께 저장한다")
        void persistAllWithOutboxes_ValidLists_SavesBothComponents() {
            // given
            List<Shipment> shipments = List.of(ShipmentFixtures.preparingShipment());
            List<ShipmentOutbox> outboxes = List.of(ShipmentOutboxFixtures.newShipmentOutbox());

            // when
            sut.persistAllWithOutboxes(shipments, outboxes);

            // then
            then(shipmentCommandManager).should().persistAll(shipments);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistWithOutbox() - 단건 Shipment + Outbox 저장")
    class PersistWithOutboxTest {

        @Test
        @DisplayName("단건 Shipment와 단건 Outbox를 함께 저장한다")
        void persistWithOutbox_ValidPair_SavesBothComponents() {
            // given
            Shipment shipment = ShipmentFixtures.shippedShipment();
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();

            // when
            sut.persistWithOutbox(shipment, outbox);

            // then
            then(shipmentCommandManager).should().persist(shipment);
            then(outboxCommandManager).should().persist(outbox);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }
}
