package com.ryuqq.marketplace.domain.order.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.event.OrderCreatedEvent;
import com.ryuqq.marketplace.domain.order.exception.OrderException;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.PaymentInfo;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Order Aggregate 단위 테스트")
class OrderTest {

    @Nested
    @DisplayName("forNew() - 신규 주문 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 주문을 생성한다")
        void createNewOrderWithRequiredFields() {
            // given
            OrderId id = OrderFixtures.defaultOrderId();
            OrderNumber orderNumber = OrderFixtures.defaultOrderNumber();
            BuyerInfo buyerInfo = OrderFixtures.defaultBuyerInfo();
            PaymentInfo paymentInfo = OrderFixtures.defaultPaymentInfo();
            ExternalOrderReference externalOrderReference =
                    OrderFixtures.defaultExternalOrderReference();
            List<OrderItem> items = List.of(OrderFixtures.defaultOrderItem());
            Instant now = CommonVoFixtures.now();

            // when
            Order order =
                    Order.forNew(
                            id,
                            orderNumber,
                            buyerInfo,
                            paymentInfo,
                            externalOrderReference,
                            items,
                            now);

            // then
            assertThat(order.id()).isEqualTo(id);
            assertThat(order.orderNumber()).isEqualTo(orderNumber);
            assertThat(order.buyerInfo()).isEqualTo(buyerInfo);
            assertThat(order.paymentInfo()).isEqualTo(paymentInfo);
            assertThat(order.externalOrderReference()).isEqualTo(externalOrderReference);
            assertThat(order.items()).hasSize(1);
            assertThat(order.createdAt()).isEqualTo(now);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("주문 상품이 비어있으면 예외가 발생한다")
        void createNewOrderWithEmptyItems_ThrowsException() {
            // given
            OrderId id = OrderFixtures.defaultOrderId();
            OrderNumber orderNumber = OrderFixtures.defaultOrderNumber();

            // when & then
            assertThatThrownBy(
                            () ->
                                    Order.forNew(
                                            id,
                                            orderNumber,
                                            OrderFixtures.defaultBuyerInfo(),
                                            OrderFixtures.defaultPaymentInfo(),
                                            OrderFixtures.defaultExternalOrderReference(),
                                            List.of(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("주문 상품이 null이면 예외가 발생한다")
        void createNewOrderWithNullItems_ThrowsException() {
            // given
            OrderId id = OrderFixtures.defaultOrderId();
            OrderNumber orderNumber = OrderFixtures.defaultOrderNumber();

            // when & then
            assertThatThrownBy(
                            () ->
                                    Order.forNew(
                                            id,
                                            orderNumber,
                                            OrderFixtures.defaultBuyerInfo(),
                                            OrderFixtures.defaultPaymentInfo(),
                                            OrderFixtures.defaultExternalOrderReference(),
                                            null,
                                            CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("신규 주문 생성 시 OrderCreatedEvent가 등록된다")
        void forNewOrderRegistersOrderCreatedEvent() {
            // when
            Order order = OrderFixtures.newOrder();

            // then
            List<DomainEvent> events = order.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(OrderCreatedEvent.class);

            OrderCreatedEvent event = (OrderCreatedEvent) events.get(0);
            assertThat(event.orderId()).isEqualTo(OrderFixtures.defaultOrderId());
            assertThat(event.orderNumber()).isEqualTo(OrderFixtures.defaultOrderNumber());
        }

        @Test
        @DisplayName("여러 주문 상품으로 신규 주문을 생성한다")
        void createNewOrderWithMultipleItems() {
            // given
            List<OrderItem> items =
                    List.of(OrderFixtures.defaultOrderItem(), OrderFixtures.defaultOrderItem());

            // when
            Order order =
                    Order.forNew(
                            OrderFixtures.defaultOrderId(),
                            OrderFixtures.defaultOrderNumber(),
                            OrderFixtures.defaultBuyerInfo(),
                            OrderFixtures.defaultPaymentInfo(),
                            OrderFixtures.defaultExternalOrderReference(),
                            items,
                            CommonVoFixtures.now());

            // then
            assertThat(order.items()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 주문을 복원한다")
        void reconstituteOrder() {
            // given
            OrderId id = OrderFixtures.defaultOrderId();
            OrderNumber orderNumber = OrderFixtures.defaultOrderNumber();
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.now();

            // when
            Order order =
                    Order.reconstitute(
                            id,
                            orderNumber,
                            OrderFixtures.defaultBuyerInfo(),
                            OrderFixtures.defaultPaymentInfo(),
                            OrderFixtures.defaultExternalOrderReference(),
                            createdAt,
                            updatedAt,
                            List.of(OrderFixtures.reconstitutedOrderItem()));

            // then
            assertThat(order.id()).isEqualTo(id);
            assertThat(order.orderNumber()).isEqualTo(orderNumber);
            assertThat(order.createdAt()).isEqualTo(createdAt);
            assertThat(order.updatedAt()).isEqualTo(updatedAt);
            assertThat(order.items()).hasSize(1);
        }

        @Test
        @DisplayName("reconstitute 시 이벤트가 등록되지 않는다")
        void reconstituteOrderDoesNotRegisterEvents() {
            // when
            Order order = OrderFixtures.reconstitutedOrder();

            // then
            List<DomainEvent> events = order.pollEvents();
            assertThat(events).isEmpty();
        }

        @Test
        @DisplayName("items가 null이면 빈 목록으로 복원한다")
        void reconstituteWithNullItems() {
            // when
            Order order =
                    Order.reconstitute(
                            OrderFixtures.defaultOrderId(),
                            OrderFixtures.defaultOrderNumber(),
                            OrderFixtures.defaultBuyerInfo(),
                            OrderFixtures.defaultPaymentInfo(),
                            OrderFixtures.defaultExternalOrderReference(),
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.now(),
                            null);

            // then
            assertThat(order.items()).isEmpty();
        }
    }

    @Nested
    @DisplayName("pollEvents() - 이벤트 조회 및 소비")
    class PollEventsTest {

        @Test
        @DisplayName("pollEvents()는 이벤트를 반환한 후 비운다")
        void pollEventsReturnsAndClearsEvents() {
            // given
            Order order = OrderFixtures.newOrder();

            // when
            List<DomainEvent> firstPoll = order.pollEvents();
            List<DomainEvent> secondPoll = order.pollEvents();

            // then
            assertThat(firstPoll).hasSize(1);
            assertThat(secondPoll).isEmpty();
        }

        @Test
        @DisplayName("pollEvents()가 반환하는 목록은 불변이다")
        void pollEventsReturnsUnmodifiableList() {
            // given
            Order order = OrderFixtures.newOrder();

            // when
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThatThrownBy(() -> events.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 String 값을 반환한다")
        void idValueReturnsStringValue() {
            // given
            Order order = OrderFixtures.reconstitutedOrder();

            // when
            String idValue = order.idValue();

            // then
            assertThat(idValue).isEqualTo(OrderFixtures.defaultOrderId().value());
        }

        @Test
        @DisplayName("orderNumberValue()는 주문번호의 String 값을 반환한다")
        void orderNumberValueReturnsStringValue() {
            // given
            Order order = OrderFixtures.reconstitutedOrder();

            // when
            String orderNumberValue = order.orderNumberValue();

            // then
            assertThat(orderNumberValue).isEqualTo(OrderFixtures.defaultOrderNumber().value());
        }

        @Test
        @DisplayName("orderedAt()은 externalOrderReference의 주문시각을 반환한다")
        void orderedAtDelegatesToExternalOrderReference() {
            // given
            Order order = OrderFixtures.reconstitutedOrder();

            // when
            Instant orderedAt = order.orderedAt();

            // then
            assertThat(orderedAt).isEqualTo(order.externalOrderReference().externalOrderedAt());
        }

        @Test
        @DisplayName("items()는 불변 목록을 반환한다")
        void itemsReturnsUnmodifiableList() {
            // given
            Order order = OrderFixtures.reconstitutedOrder();

            // when
            List<OrderItem> items = order.items();

            // then
            assertThatThrownBy(() -> items.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
