package com.ryuqq.marketplace.domain.shipment.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Shipment 도메인 이벤트 단위 테스트")
class ShipmentEventsTest {

    private static final String SHIPMENT_ID = ShipmentFixtures.defaultShipmentId().value();
    private static final long ORDER_ITEM_ID = 1L;

    @Nested
    @DisplayName("ShipmentCreatedEvent 테스트")
    class ShipmentCreatedEventTest {

        @Test
        @DisplayName("배송 생성 이벤트를 올바르게 생성한다")
        void createShipmentCreatedEvent() {
            Instant now = CommonVoFixtures.now();

            ShipmentCreatedEvent event = new ShipmentCreatedEvent(SHIPMENT_ID, ORDER_ITEM_ID, now);

            assertThat(event.shipmentId()).isEqualTo(SHIPMENT_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("배송 생성 이벤트는 DomainEvent를 구현한다")
        void shipmentCreatedEventIsDomainEvent() {
            Instant now = CommonVoFixtures.now();

            ShipmentCreatedEvent event = new ShipmentCreatedEvent(SHIPMENT_ID, ORDER_ITEM_ID, now);

            assertThat(event)
                    .isInstanceOf(com.ryuqq.marketplace.domain.common.event.DomainEvent.class);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            ShipmentCreatedEvent event1 = new ShipmentCreatedEvent(SHIPMENT_ID, ORDER_ITEM_ID, now);
            ShipmentCreatedEvent event2 = new ShipmentCreatedEvent(SHIPMENT_ID, ORDER_ITEM_ID, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }

        @Test
        @DisplayName("다른 shipmentId이면 다르다")
        void differentShipmentIdProducesDifferentEvents() {
            Instant now = CommonVoFixtures.now();

            ShipmentCreatedEvent event1 = new ShipmentCreatedEvent(SHIPMENT_ID, ORDER_ITEM_ID, now);
            ShipmentCreatedEvent event2 =
                    new ShipmentCreatedEvent("different-id", ORDER_ITEM_ID, now);

            assertThat(event1).isNotEqualTo(event2);
        }
    }

    @Nested
    @DisplayName("ShipmentStatusChangedEvent 테스트")
    class ShipmentStatusChangedEventTest {

        @Test
        @DisplayName("배송 상태 변경 이벤트를 올바르게 생성한다")
        void createShipmentStatusChangedEvent() {
            Instant now = CommonVoFixtures.now();
            ShipmentStatus from = ShipmentStatus.READY;
            ShipmentStatus to = ShipmentStatus.PREPARING;

            ShipmentStatusChangedEvent event =
                    new ShipmentStatusChangedEvent(SHIPMENT_ID, from, to, now);

            assertThat(event.shipmentId()).isEqualTo(SHIPMENT_ID);
            assertThat(event.fromStatus()).isEqualTo(from);
            assertThat(event.toStatus()).isEqualTo(to);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PREPARING에서 SHIPPED로의 상태 변경 이벤트를 생성한다")
        void createPreparingToShippedEvent() {
            Instant now = CommonVoFixtures.now();

            ShipmentStatusChangedEvent event =
                    new ShipmentStatusChangedEvent(
                            SHIPMENT_ID, ShipmentStatus.PREPARING, ShipmentStatus.SHIPPED, now);

            assertThat(event.fromStatus()).isEqualTo(ShipmentStatus.PREPARING);
            assertThat(event.toStatus()).isEqualTo(ShipmentStatus.SHIPPED);
        }

        @Test
        @DisplayName("IN_TRANSIT에서 DELIVERED로의 상태 변경 이벤트를 생성한다")
        void createInTransitToDeliveredEvent() {
            Instant now = CommonVoFixtures.now();

            ShipmentStatusChangedEvent event =
                    new ShipmentStatusChangedEvent(
                            SHIPMENT_ID, ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED, now);

            assertThat(event.fromStatus()).isEqualTo(ShipmentStatus.IN_TRANSIT);
            assertThat(event.toStatus()).isEqualTo(ShipmentStatus.DELIVERED);
        }

        @Test
        @DisplayName("배송 상태 변경 이벤트는 DomainEvent를 구현한다")
        void shipmentStatusChangedEventIsDomainEvent() {
            Instant now = CommonVoFixtures.now();

            ShipmentStatusChangedEvent event =
                    new ShipmentStatusChangedEvent(
                            SHIPMENT_ID, ShipmentStatus.READY, ShipmentStatus.PREPARING, now);

            assertThat(event)
                    .isInstanceOf(com.ryuqq.marketplace.domain.common.event.DomainEvent.class);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            ShipmentStatusChangedEvent event1 =
                    new ShipmentStatusChangedEvent(
                            SHIPMENT_ID, ShipmentStatus.READY, ShipmentStatus.PREPARING, now);
            ShipmentStatusChangedEvent event2 =
                    new ShipmentStatusChangedEvent(
                            SHIPMENT_ID, ShipmentStatus.READY, ShipmentStatus.PREPARING, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }

        @Test
        @DisplayName("다른 상태 전이이면 다르다")
        void differentStatusTransitionProducesDifferentEvents() {
            Instant now = CommonVoFixtures.now();

            ShipmentStatusChangedEvent event1 =
                    new ShipmentStatusChangedEvent(
                            SHIPMENT_ID, ShipmentStatus.READY, ShipmentStatus.PREPARING, now);
            ShipmentStatusChangedEvent event2 =
                    new ShipmentStatusChangedEvent(
                            SHIPMENT_ID, ShipmentStatus.PREPARING, ShipmentStatus.SHIPPED, now);

            assertThat(event1).isNotEqualTo(event2);
        }
    }
}
