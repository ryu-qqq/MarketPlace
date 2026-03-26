package com.ryuqq.marketplace.domain.exchange.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Exchange 도메인 이벤트 단위 테스트")
class ExchangeEventTest {

    private static final ExchangeClaimId CLAIM_ID = ExchangeFixtures.defaultExchangeClaimId();
    private static final OrderItemId ORDER_ITEM_ID = ExchangeFixtures.defaultOrderItemId();

    @Nested
    @DisplayName("ExchangeClaimCreatedEvent 테스트")
    class ExchangeClaimCreatedEventTest {

        @Test
        @DisplayName("교환 클레임 생성 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangeClaimCreatedEvent event =
                    new ExchangeClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event).isNotNull();
            assertThat(event.exchangeClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            ExchangeClaimCreatedEvent event =
                    new ExchangeClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now());

            assertThat(event).isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("ExchangeClaimStatusChangedEvent 테스트")
    class ExchangeClaimStatusChangedEventTest {

        @Test
        @DisplayName("교환 상태 변경 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangeClaimStatusChangedEvent event =
                    new ExchangeClaimStatusChangedEvent(
                            CLAIM_ID,
                            ORDER_ITEM_ID,
                            ExchangeStatus.REQUESTED,
                            ExchangeStatus.COLLECTING,
                            now);

            // then
            assertThat(event.exchangeClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.fromStatus()).isEqualTo(ExchangeStatus.REQUESTED);
            assertThat(event.toStatus()).isEqualTo(ExchangeStatus.COLLECTING);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            ExchangeClaimStatusChangedEvent event =
                    new ExchangeClaimStatusChangedEvent(
                            CLAIM_ID,
                            ORDER_ITEM_ID,
                            ExchangeStatus.REQUESTED,
                            ExchangeStatus.COLLECTING,
                            CommonVoFixtures.now());

            assertThat(event).isInstanceOf(DomainEvent.class);
        }

        @Test
        @DisplayName("상태 전이 조합을 정확히 저장한다")
        void storesStatusTransitionCorrectly() {
            ExchangeClaimStatusChangedEvent event =
                    new ExchangeClaimStatusChangedEvent(
                            CLAIM_ID,
                            ORDER_ITEM_ID,
                            ExchangeStatus.COLLECTING,
                            ExchangeStatus.COLLECTED,
                            CommonVoFixtures.now());

            assertThat(event.fromStatus()).isEqualTo(ExchangeStatus.COLLECTING);
            assertThat(event.toStatus()).isEqualTo(ExchangeStatus.COLLECTED);
        }
    }

    @Nested
    @DisplayName("ExchangeCollectingEvent 테스트")
    class ExchangeCollectingEventTest {

        @Test
        @DisplayName("교환 수거 시작 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangeCollectingEvent event =
                    new ExchangeCollectingEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.exchangeClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new ExchangeCollectingEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("ExchangeCollectedEvent 테스트")
    class ExchangeCollectedEventTest {

        @Test
        @DisplayName("교환 수거 완료 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangeCollectedEvent event = new ExchangeCollectedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.exchangeClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new ExchangeCollectedEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("ExchangePreparingEvent 테스트")
    class ExchangePreparingEventTest {

        @Test
        @DisplayName("교환 상품 준비 시작 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangePreparingEvent event = new ExchangePreparingEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.exchangeClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new ExchangePreparingEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("ExchangeShippingEvent 테스트")
    class ExchangeShippingEventTest {

        @Test
        @DisplayName("교환 상품 재배송 시작 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();
            String linkedOrderId = "ORDER-20260101-9999";

            // when
            ExchangeShippingEvent event =
                    new ExchangeShippingEvent(CLAIM_ID, ORDER_ITEM_ID, linkedOrderId, now);

            // then
            assertThat(event.exchangeClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.linkedOrderId()).isEqualTo(linkedOrderId);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(
                            new ExchangeShippingEvent(
                                    CLAIM_ID, ORDER_ITEM_ID, "ORDER-ID", CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("ExchangeCompletedEvent 테스트")
    class ExchangeCompletedEventTest {

        @Test
        @DisplayName("교환 완료 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangeCompletedEvent event = new ExchangeCompletedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.exchangeClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new ExchangeCompletedEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("ExchangeRejectedEvent 테스트")
    class ExchangeRejectedEventTest {

        @Test
        @DisplayName("교환 거절 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangeRejectedEvent event = new ExchangeRejectedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.exchangeClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new ExchangeRejectedEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("ExchangeCancelledEvent 테스트")
    class ExchangeCancelledEventTest {

        @Test
        @DisplayName("교환 철회 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangeCancelledEvent event = new ExchangeCancelledEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.exchangeClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new ExchangeCancelledEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("이벤트 동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 필드를 가진 이벤트는 동일하다")
        void sameFieldsAreEqual() {
            Instant now = CommonVoFixtures.now();

            ExchangeClaimCreatedEvent event1 =
                    new ExchangeClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, now);
            ExchangeClaimCreatedEvent event2 =
                    new ExchangeClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            assertThat(event1).isEqualTo(event2);
        }
    }
}
