package com.ryuqq.marketplace.domain.order.event;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderCreatedEvent 단위 테스트")
class OrderCreatedEventTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("필수 값으로 OrderCreatedEvent를 생성한다")
        void createWithRequiredValues() {
            // given
            OrderId orderId = OrderFixtures.defaultOrderId();
            OrderNumber orderNumber = OrderFixtures.defaultOrderNumber();
            Instant occurredAt = CommonVoFixtures.now();

            // when
            OrderCreatedEvent event = new OrderCreatedEvent(orderId, orderNumber, occurredAt);

            // then
            assertThat(event.orderId()).isEqualTo(orderId);
            assertThat(event.orderNumber()).isEqualTo(orderNumber);
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        @DisplayName("OrderCreatedEvent는 DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            // when
            OrderCreatedEvent event =
                    new OrderCreatedEvent(
                            OrderFixtures.defaultOrderId(),
                            OrderFixtures.defaultOrderNumber(),
                            CommonVoFixtures.now());

            // then
            assertThat(event).isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 OrderCreatedEvent는 동일하다")
        void sameValuesAreEqual() {
            // given
            OrderId orderId = OrderFixtures.defaultOrderId();
            OrderNumber orderNumber = OrderFixtures.defaultOrderNumber();
            Instant occurredAt = CommonVoFixtures.now();

            // when
            OrderCreatedEvent event1 = new OrderCreatedEvent(orderId, orderNumber, occurredAt);
            OrderCreatedEvent event2 = new OrderCreatedEvent(orderId, orderNumber, occurredAt);

            // then
            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }

        @Test
        @DisplayName("다른 orderId를 가진 OrderCreatedEvent는 동일하지 않다")
        void differentOrderIdNotEqual() {
            // given
            Instant occurredAt = CommonVoFixtures.now();
            OrderCreatedEvent event1 =
                    new OrderCreatedEvent(
                            OrderId.of("01900000-0000-7000-8000-000000000001"),
                            OrderFixtures.defaultOrderNumber(),
                            occurredAt);
            OrderCreatedEvent event2 =
                    new OrderCreatedEvent(
                            OrderId.of("01900000-0000-7000-8000-000000000002"),
                            OrderFixtures.defaultOrderNumber(),
                            occurredAt);

            // then
            assertThat(event1).isNotEqualTo(event2);
        }
    }

    @Nested
    @DisplayName("Order Aggregate 통합 시나리오")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("신규 주문 생성 시 OrderCreatedEvent가 발행된다")
        void newOrderPublishesOrderCreatedEvent() {
            // when
            var order = OrderFixtures.newOrder();

            // then
            var events = order.pollEvents();
            assertThat(events).hasSize(1);

            OrderCreatedEvent event = (OrderCreatedEvent) events.get(0);
            assertThat(event.orderId()).isEqualTo(OrderFixtures.defaultOrderId());
            assertThat(event.orderNumber()).isEqualTo(OrderFixtures.defaultOrderNumber());
            assertThat(event.occurredAt()).isNotNull();
        }
    }
}
