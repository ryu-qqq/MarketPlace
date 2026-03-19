package com.ryuqq.marketplace.domain.refund.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Refund 도메인 이벤트 단위 테스트")
class RefundEventTest {

    private static final RefundClaimId CLAIM_ID = RefundFixtures.defaultRefundClaimId();
    private static final OrderItemId ORDER_ITEM_ID = RefundFixtures.defaultOrderItemId();

    @Nested
    @DisplayName("RefundClaimCreatedEvent 테스트")
    class RefundClaimCreatedEventTest {

        @Test
        @DisplayName("환불 클레임 생성 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            RefundClaimCreatedEvent event =
                    new RefundClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event).isNotNull();
            assertThat(event.refundClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            RefundClaimCreatedEvent event =
                    new RefundClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now());

            assertThat(event).isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("RefundClaimStatusChangedEvent 테스트")
    class RefundClaimStatusChangedEventTest {

        @Test
        @DisplayName("환불 상태 변경 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            RefundClaimStatusChangedEvent event =
                    new RefundClaimStatusChangedEvent(
                            CLAIM_ID,
                            ORDER_ITEM_ID,
                            RefundStatus.REQUESTED,
                            RefundStatus.COLLECTING,
                            now);

            // then
            assertThat(event.refundClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.fromStatus()).isEqualTo(RefundStatus.REQUESTED);
            assertThat(event.toStatus()).isEqualTo(RefundStatus.COLLECTING);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            RefundClaimStatusChangedEvent event =
                    new RefundClaimStatusChangedEvent(
                            CLAIM_ID,
                            ORDER_ITEM_ID,
                            RefundStatus.REQUESTED,
                            RefundStatus.COLLECTING,
                            CommonVoFixtures.now());

            assertThat(event).isInstanceOf(DomainEvent.class);
        }

        @Test
        @DisplayName("다양한 상태 전이를 저장할 수 있다")
        void storesVariousStatusTransitions() {
            RefundClaimStatusChangedEvent event1 =
                    new RefundClaimStatusChangedEvent(
                            CLAIM_ID,
                            ORDER_ITEM_ID,
                            RefundStatus.COLLECTING,
                            RefundStatus.COLLECTED,
                            CommonVoFixtures.now());

            RefundClaimStatusChangedEvent event2 =
                    new RefundClaimStatusChangedEvent(
                            CLAIM_ID,
                            ORDER_ITEM_ID,
                            RefundStatus.COLLECTED,
                            RefundStatus.COMPLETED,
                            CommonVoFixtures.now());

            assertThat(event1.fromStatus()).isEqualTo(RefundStatus.COLLECTING);
            assertThat(event1.toStatus()).isEqualTo(RefundStatus.COLLECTED);
            assertThat(event2.fromStatus()).isEqualTo(RefundStatus.COLLECTED);
            assertThat(event2.toStatus()).isEqualTo(RefundStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("RefundCollectingEvent 테스트")
    class RefundCollectingEventTest {

        @Test
        @DisplayName("환불 수거 시작 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            RefundCollectingEvent event = new RefundCollectingEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.refundClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new RefundCollectingEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("RefundCollectedEvent 테스트")
    class RefundCollectedEventTest {

        @Test
        @DisplayName("환불 수거 완료 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            RefundCollectedEvent event = new RefundCollectedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.refundClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new RefundCollectedEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("RefundCompletedEvent 테스트")
    class RefundCompletedEventTest {

        @Test
        @DisplayName("환불 완료 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            RefundCompletedEvent event = new RefundCompletedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.refundClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new RefundCompletedEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("RefundRejectedEvent 테스트")
    class RefundRejectedEventTest {

        @Test
        @DisplayName("환불 거절 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            RefundRejectedEvent event = new RefundRejectedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.refundClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new RefundRejectedEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
                    .isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("RefundCancelledEvent 테스트")
    class RefundCancelledEventTest {

        @Test
        @DisplayName("환불 철회 이벤트를 생성한다")
        void createEvent() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            RefundCancelledEvent event = new RefundCancelledEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            // then
            assertThat(event.refundClaimId()).isEqualTo(CLAIM_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            assertThat(new RefundCancelledEvent(CLAIM_ID, ORDER_ITEM_ID, CommonVoFixtures.now()))
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

            RefundClaimCreatedEvent event1 =
                    new RefundClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, now);
            RefundClaimCreatedEvent event2 =
                    new RefundClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, now);

            assertThat(event1).isEqualTo(event2);
        }

        @Test
        @DisplayName("다른 필드를 가진 이벤트는 동일하지 않다")
        void differentFieldsAreNotEqual() {
            Instant now = CommonVoFixtures.now();
            Instant later = CommonVoFixtures.tomorrow();

            RefundClaimCreatedEvent event1 =
                    new RefundClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, now);
            RefundClaimCreatedEvent event2 =
                    new RefundClaimCreatedEvent(CLAIM_ID, ORDER_ITEM_ID, later);

            assertThat(event1).isNotEqualTo(event2);
        }
    }
}
