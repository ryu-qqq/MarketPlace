package com.ryuqq.marketplace.domain.order.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.event.OrderCancelledEvent;
import com.ryuqq.marketplace.domain.order.event.OrderClaimStartedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderConfirmedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderCreatedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderDeliveredEvent;
import com.ryuqq.marketplace.domain.order.event.OrderExchangeCompletedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderPreparedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderRefundCompletedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderShippedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderStatusChangedEvent;
import com.ryuqq.marketplace.domain.order.exception.OrderException;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
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
        @DisplayName("필수 필드로 신규 주문을 생성한다")
        void createNewOrderWithRequiredFields() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            Order order = OrderFixtures.newOrder(now);

            // then
            assertThat(order.id()).isEqualTo(OrderFixtures.defaultOrderId());
            assertThat(order.orderNumber()).isEqualTo(OrderFixtures.defaultOrderNumber());
            assertThat(order.status()).isEqualTo(OrderStatus.ORDERED);
            assertThat(order.buyerInfo()).isEqualTo(OrderFixtures.defaultBuyerInfo());
            assertThat(order.items()).hasSize(1);
            assertThat(order.createdAt()).isEqualTo(now);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 주문 생성 시 OrderCreatedEvent가 등록된다")
        void orderCreatedEventRegistered() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            Order order = OrderFixtures.newOrder(now);
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(OrderCreatedEvent.class);

            OrderCreatedEvent event = (OrderCreatedEvent) events.get(0);
            assertThat(event.orderId()).isEqualTo(order.id());
            assertThat(event.orderNumber()).isEqualTo(order.orderNumber());
        }

        @Test
        @DisplayName("주문 상품이 비어 있으면 예외가 발생한다")
        void createOrderWithEmptyItems_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    Order.forNew(
                                            OrderFixtures.defaultOrderId(),
                                            OrderFixtures.defaultOrderNumber(),
                                            OrderFixtures.defaultBuyerInfo(),
                                            OrderFixtures.defaultPaymentInfo(),
                                            OrderFixtures.defaultExternalOrderReference(),
                                            List.of(),
                                            "system",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("주문 상품이 null이면 예외가 발생한다")
        void createOrderWithNullItems_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    Order.forNew(
                                            OrderFixtures.defaultOrderId(),
                                            OrderFixtures.defaultOrderNumber(),
                                            OrderFixtures.defaultBuyerInfo(),
                                            OrderFixtures.defaultPaymentInfo(),
                                            OrderFixtures.defaultExternalOrderReference(),
                                            null,
                                            "system",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("신규 주문 생성 시 이력이 1건 기록된다")
        void orderHistoryCreatedOnForNew() {
            // when
            Order order = OrderFixtures.newOrder();

            // then
            assertThat(order.histories()).hasSize(1);
            OrderHistory history = order.histories().get(0);
            assertThat(history.toStatus()).isEqualTo(OrderStatus.ORDERED);
            assertThat(history.fromStatus()).isNull();
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("ORDERED 상태로 재구성한다")
        void reconstituteOrderedOrder() {
            // when
            Order order = OrderFixtures.orderedOrder();

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.ORDERED);
            assertThat(order.items()).hasSize(1);
            assertThat(order.histories()).hasSize(1);
        }

        @Test
        @DisplayName("SHIPPED 상태로 재구성한다")
        void reconstituteShippedOrder() {
            // when
            Order order = OrderFixtures.shippedOrder();

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.SHIPPED);
        }

        @Test
        @DisplayName("재구성 시 이벤트가 등록되지 않는다")
        void noEventsOnReconstitute() {
            // when
            Order order = OrderFixtures.orderedOrder();
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).isEmpty();
        }
    }

    @Nested
    @DisplayName("prepare() - 상품 준비 처리")
    class PrepareTest {

        @Test
        @DisplayName("ORDERED 상태에서 PREPARING으로 전이한다")
        void prepareFromOrdered() {
            // given
            Order order = OrderFixtures.orderedOrder();
            Instant now = CommonVoFixtures.now();

            // when
            order.prepare("system", now);

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.PREPARING);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("prepare 시 OrderPreparedEvent와 OrderStatusChangedEvent가 등록된다")
        void preparingEventsRegistered() {
            // given
            Order order = OrderFixtures.orderedOrder();

            // when
            order.prepare("system", CommonVoFixtures.now());
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).hasSize(2);
            assertThat(events).anyMatch(e -> e instanceof OrderPreparedEvent);
            assertThat(events).anyMatch(e -> e instanceof OrderStatusChangedEvent);

            OrderStatusChangedEvent statusEvent =
                    events.stream()
                            .filter(e -> e instanceof OrderStatusChangedEvent)
                            .map(e -> (OrderStatusChangedEvent) e)
                            .findFirst()
                            .orElseThrow();
            assertThat(statusEvent.fromStatus()).isEqualTo(OrderStatus.ORDERED);
            assertThat(statusEvent.toStatus()).isEqualTo(OrderStatus.PREPARING);
        }

        @Test
        @DisplayName("SHIPPED 상태에서 prepare하면 예외가 발생한다")
        void prepareFromShipped_ThrowsException() {
            // given
            Order order = OrderFixtures.shippedOrder();

            // when & then
            assertThatThrownBy(() -> order.prepare("system", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("ship() - 배송 출고 처리")
    class ShipTest {

        @Test
        @DisplayName("PREPARING 상태에서 SHIPPED로 전이한다")
        void shipFromPreparing() {
            // given
            Order order = OrderFixtures.preparingOrder();
            Instant now = CommonVoFixtures.now();

            // when
            order.ship("system", now);

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.SHIPPED);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("ship 시 OrderShippedEvent와 OrderStatusChangedEvent가 등록된다")
        void shippingEventsRegistered() {
            // given
            Order order = OrderFixtures.preparingOrder();

            // when
            order.ship("system", CommonVoFixtures.now());
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).hasSize(2);
            assertThat(events).anyMatch(e -> e instanceof OrderShippedEvent);
            assertThat(events).anyMatch(e -> e instanceof OrderStatusChangedEvent);
        }

        @Test
        @DisplayName("ORDERED 상태에서 ship하면 예외가 발생한다")
        void shipFromOrdered_ThrowsException() {
            // given
            Order order = OrderFixtures.orderedOrder();

            // when & then
            assertThatThrownBy(() -> order.ship("system", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("deliver() - 배송 완료 처리")
    class DeliverTest {

        @Test
        @DisplayName("SHIPPED 상태에서 DELIVERED로 전이한다")
        void deliverFromShipped() {
            // given
            Order order = OrderFixtures.shippedOrder();
            Instant now = CommonVoFixtures.now();

            // when
            order.deliver("system", now);

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.DELIVERED);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("deliver 시 OrderDeliveredEvent와 OrderStatusChangedEvent가 등록된다")
        void deliveredEventsRegistered() {
            // given
            Order order = OrderFixtures.shippedOrder();

            // when
            order.deliver("system", CommonVoFixtures.now());
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).hasSize(2);
            assertThat(events).anyMatch(e -> e instanceof OrderDeliveredEvent);
            assertThat(events).anyMatch(e -> e instanceof OrderStatusChangedEvent);
        }

        @Test
        @DisplayName("PREPARING 상태에서 deliver하면 예외가 발생한다")
        void deliverFromPreparing_ThrowsException() {
            // given
            Order order = OrderFixtures.preparingOrder();

            // when & then
            assertThatThrownBy(() -> order.deliver("system", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("confirm() - 구매 확정 처리")
    class ConfirmTest {

        @Test
        @DisplayName("DELIVERED 상태에서 CONFIRMED로 전이한다")
        void confirmFromDelivered() {
            // given
            Order order = OrderFixtures.deliveredOrder();
            Instant now = CommonVoFixtures.now();

            // when
            order.confirm("buyer", now);

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("confirm 시 OrderConfirmedEvent와 OrderStatusChangedEvent가 등록된다")
        void confirmedEventsRegistered() {
            // given
            Order order = OrderFixtures.deliveredOrder();

            // when
            order.confirm("buyer", CommonVoFixtures.now());
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).hasSize(2);
            assertThat(events).anyMatch(e -> e instanceof OrderConfirmedEvent);
            assertThat(events).anyMatch(e -> e instanceof OrderStatusChangedEvent);
        }

        @Test
        @DisplayName("SHIPPED 상태에서 confirm하면 예외가 발생한다")
        void confirmFromShipped_ThrowsException() {
            // given
            Order order = OrderFixtures.shippedOrder();

            // when & then
            assertThatThrownBy(() -> order.confirm("buyer", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("cancel() - 주문 취소 처리")
    class CancelTest {

        @Test
        @DisplayName("ORDERED 상태에서 CANCELLED로 전이한다")
        void cancelFromOrdered() {
            // given
            Order order = OrderFixtures.orderedOrder();
            Instant now = CommonVoFixtures.now();

            // when
            order.cancel("buyer", "단순 변심", now);

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PREPARING 상태에서 CANCELLED로 전이한다")
        void cancelFromPreparing() {
            // given
            Order order = OrderFixtures.preparingOrder();

            // when
            order.cancel("buyer", "단순 변심", CommonVoFixtures.now());

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("cancel 시 OrderCancelledEvent와 OrderStatusChangedEvent가 등록된다")
        void cancelledEventsRegistered() {
            // given
            Order order = OrderFixtures.orderedOrder();

            // when
            order.cancel("buyer", "단순 변심", CommonVoFixtures.now());
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).hasSize(2);
            assertThat(events).anyMatch(e -> e instanceof OrderCancelledEvent);
            assertThat(events).anyMatch(e -> e instanceof OrderStatusChangedEvent);
        }

        @Test
        @DisplayName("SHIPPED 상태에서 cancel하면 예외가 발생한다")
        void cancelFromShipped_ThrowsException() {
            // given
            Order order = OrderFixtures.shippedOrder();

            // when & then
            assertThatThrownBy(() -> order.cancel("buyer", "단순 변심", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("DELIVERED 상태에서 cancel하면 예외가 발생한다")
        void cancelFromDelivered_ThrowsException() {
            // given
            Order order = OrderFixtures.deliveredOrder();

            // when & then
            assertThatThrownBy(() -> order.cancel("buyer", "단순 변심", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("startClaim() - 클레임 접수 처리")
    class StartClaimTest {

        @Test
        @DisplayName("DELIVERED 상태에서 CLAIM_IN_PROGRESS로 전이한다")
        void startClaimFromDelivered() {
            // given
            Order order = OrderFixtures.deliveredOrder();
            Instant now = CommonVoFixtures.now();

            // when
            order.startClaim("buyer", "상품 불량", now);

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.CLAIM_IN_PROGRESS);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("SHIPPED 상태에서 CLAIM_IN_PROGRESS로 전이한다")
        void startClaimFromShipped() {
            // given
            Order order = OrderFixtures.shippedOrder();

            // when
            order.startClaim("buyer", "배송 중 파손", CommonVoFixtures.now());

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.CLAIM_IN_PROGRESS);
        }

        @Test
        @DisplayName("startClaim 시 OrderClaimStartedEvent와 OrderStatusChangedEvent가 등록된다")
        void claimStartedEventsRegistered() {
            // given
            Order order = OrderFixtures.deliveredOrder();

            // when
            order.startClaim("buyer", "상품 불량", CommonVoFixtures.now());
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).hasSize(2);
            assertThat(events).anyMatch(e -> e instanceof OrderClaimStartedEvent);
            assertThat(events).anyMatch(e -> e instanceof OrderStatusChangedEvent);
        }

        @Test
        @DisplayName("ORDERED 상태에서 startClaim하면 예외가 발생한다")
        void startClaimFromOrdered_ThrowsException() {
            // given
            Order order = OrderFixtures.orderedOrder();

            // when & then
            assertThatThrownBy(() -> order.startClaim("buyer", "사유", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("CONFIRMED 상태에서 startClaim하면 예외가 발생한다")
        void startClaimFromConfirmed_ThrowsException() {
            // given
            Order order = OrderFixtures.confirmedOrder();

            // when & then
            assertThatThrownBy(() -> order.startClaim("buyer", "사유", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("completeRefund() - 환불 완료 처리")
    class CompleteRefundTest {

        @Test
        @DisplayName("CLAIM_IN_PROGRESS 상태에서 REFUNDED로 전이한다")
        void completeRefundFromClaimInProgress() {
            // given
            Order order = OrderFixtures.claimInProgressOrder();
            Instant now = CommonVoFixtures.now();

            // when
            order.completeRefund("system", now);

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.REFUNDED);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("completeRefund 시 OrderRefundCompletedEvent와 OrderStatusChangedEvent가 등록된다")
        void refundCompletedEventsRegistered() {
            // given
            Order order = OrderFixtures.claimInProgressOrder();

            // when
            order.completeRefund("system", CommonVoFixtures.now());
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).hasSize(2);
            assertThat(events).anyMatch(e -> e instanceof OrderRefundCompletedEvent);
            assertThat(events).anyMatch(e -> e instanceof OrderStatusChangedEvent);
        }

        @Test
        @DisplayName("ORDERED 상태에서 completeRefund하면 예외가 발생한다")
        void completeRefundFromOrdered_ThrowsException() {
            // given
            Order order = OrderFixtures.orderedOrder();

            // when & then
            assertThatThrownBy(() -> order.completeRefund("system", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("completeExchange() - 교환 완료 처리")
    class CompleteExchangeTest {

        @Test
        @DisplayName("CLAIM_IN_PROGRESS 상태에서 EXCHANGED로 전이한다")
        void completeExchangeFromClaimInProgress() {
            // given
            Order order = OrderFixtures.claimInProgressOrder();
            Instant now = CommonVoFixtures.now();

            // when
            order.completeExchange("system", now);

            // then
            assertThat(order.status()).isEqualTo(OrderStatus.EXCHANGED);
            assertThat(order.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName(
                "completeExchange 시 OrderExchangeCompletedEvent와 OrderStatusChangedEvent가 등록된다")
        void exchangeCompletedEventsRegistered() {
            // given
            Order order = OrderFixtures.claimInProgressOrder();

            // when
            order.completeExchange("system", CommonVoFixtures.now());
            List<DomainEvent> events = order.pollEvents();

            // then
            assertThat(events).hasSize(2);
            assertThat(events).anyMatch(e -> e instanceof OrderExchangeCompletedEvent);
            assertThat(events).anyMatch(e -> e instanceof OrderStatusChangedEvent);
        }

        @Test
        @DisplayName("DELIVERED 상태에서 completeExchange하면 예외가 발생한다")
        void completeExchangeFromDelivered_ThrowsException() {
            // given
            Order order = OrderFixtures.deliveredOrder();

            // when & then
            assertThatThrownBy(() -> order.completeExchange("system", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("pollEvents() - 이벤트 수집")
    class PollEventsTest {

        @Test
        @DisplayName("pollEvents 호출 후 이벤트가 비워진다")
        void eventsAreClearedAfterPoll() {
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
        @DisplayName("pollEvents 결과는 불변 리스트이다")
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
    @DisplayName("items() - 주문 상품 조회")
    class ItemsTest {

        @Test
        @DisplayName("items()는 불변 리스트를 반환한다")
        void itemsReturnsUnmodifiableList() {
            // given
            Order order = OrderFixtures.orderedOrder();

            // when
            List<OrderItem> items = order.items();

            // then
            assertThatThrownBy(() -> items.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("주문 상품이 1개 이상 존재한다")
        void orderHasAtLeastOneItem() {
            // when
            Order order = OrderFixtures.orderedOrder();

            // then
            assertThat(order.items()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 OrderId의 value 문자열을 반환한다")
        void idValueReturnsString() {
            // when
            Order order = OrderFixtures.orderedOrder();

            // then
            assertThat(order.idValue()).isEqualTo(OrderFixtures.defaultOrderId().value());
        }

        @Test
        @DisplayName("orderNumberValue()는 OrderNumber의 value 문자열을 반환한다")
        void orderNumberValueReturnsString() {
            // when
            Order order = OrderFixtures.orderedOrder();

            // then
            assertThat(order.orderNumberValue())
                    .isEqualTo(OrderFixtures.defaultOrderNumber().value());
        }

        @Test
        @DisplayName("orderedAt()은 외부 주문 시간을 반환한다")
        void orderedAtReturnsExternalOrderedAt() {
            // when
            Order order = OrderFixtures.orderedOrder();

            // then
            assertThat(order.orderedAt())
                    .isEqualTo(order.externalOrderReference().externalOrderedAt());
        }
    }
}
