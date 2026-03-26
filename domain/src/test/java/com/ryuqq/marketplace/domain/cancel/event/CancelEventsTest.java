package com.ryuqq.marketplace.domain.cancel.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Cancel 도메인 이벤트 단위 테스트")
class CancelEventsTest {

    private static final CancelId CANCEL_ID = CancelFixtures.defaultCancelId();
    private static final OrderItemId ORDER_ITEM_ID =
            OrderItemId.of("01940001-0000-7000-8000-000000000001");

    @Nested
    @DisplayName("CancelCreatedEvent 테스트")
    class CancelCreatedEventTest {

        @Test
        @DisplayName("취소 생성 이벤트를 올바르게 생성한다")
        void createCancelCreatedEvent() {
            Instant now = CommonVoFixtures.now();

            CancelCreatedEvent event =
                    new CancelCreatedEvent(CANCEL_ID, ORDER_ITEM_ID, CancelType.BUYER_CANCEL, now);

            assertThat(event.cancelId()).isEqualTo(CANCEL_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.cancelType()).isEqualTo(CancelType.BUYER_CANCEL);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("판매자 취소 생성 이벤트의 cancelType은 SELLER_CANCEL이다")
        void sellerCancelCreatedEventHasSellerCancelType() {
            Instant now = CommonVoFixtures.now();

            CancelCreatedEvent event =
                    new CancelCreatedEvent(CANCEL_ID, ORDER_ITEM_ID, CancelType.SELLER_CANCEL, now);

            assertThat(event.cancelType()).isEqualTo(CancelType.SELLER_CANCEL);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            CancelCreatedEvent event1 =
                    new CancelCreatedEvent(CANCEL_ID, ORDER_ITEM_ID, CancelType.BUYER_CANCEL, now);
            CancelCreatedEvent event2 =
                    new CancelCreatedEvent(CANCEL_ID, ORDER_ITEM_ID, CancelType.BUYER_CANCEL, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }

    @Nested
    @DisplayName("CancelApprovedEvent 테스트")
    class CancelApprovedEventTest {

        @Test
        @DisplayName("취소 승인 이벤트를 올바르게 생성한다")
        void createCancelApprovedEvent() {
            Instant now = CommonVoFixtures.now();

            CancelApprovedEvent event = new CancelApprovedEvent(CANCEL_ID, ORDER_ITEM_ID, now);

            assertThat(event.cancelId()).isEqualTo(CANCEL_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            CancelApprovedEvent event1 = new CancelApprovedEvent(CANCEL_ID, ORDER_ITEM_ID, now);
            CancelApprovedEvent event2 = new CancelApprovedEvent(CANCEL_ID, ORDER_ITEM_ID, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }

    @Nested
    @DisplayName("CancelRejectedEvent 테스트")
    class CancelRejectedEventTest {

        @Test
        @DisplayName("취소 거절 이벤트를 올바르게 생성한다")
        void createCancelRejectedEvent() {
            Instant now = CommonVoFixtures.now();

            CancelRejectedEvent event = new CancelRejectedEvent(CANCEL_ID, ORDER_ITEM_ID, now);

            assertThat(event.cancelId()).isEqualTo(CANCEL_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            CancelRejectedEvent event1 = new CancelRejectedEvent(CANCEL_ID, ORDER_ITEM_ID, now);
            CancelRejectedEvent event2 = new CancelRejectedEvent(CANCEL_ID, ORDER_ITEM_ID, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }

    @Nested
    @DisplayName("CancelCompletedEvent 테스트")
    class CancelCompletedEventTest {

        @Test
        @DisplayName("취소 완료 이벤트를 올바르게 생성한다")
        void createCancelCompletedEvent() {
            Instant now = CommonVoFixtures.now();

            CancelCompletedEvent event = new CancelCompletedEvent(CANCEL_ID, ORDER_ITEM_ID, now);

            assertThat(event.cancelId()).isEqualTo(CANCEL_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            CancelCompletedEvent event1 = new CancelCompletedEvent(CANCEL_ID, ORDER_ITEM_ID, now);
            CancelCompletedEvent event2 = new CancelCompletedEvent(CANCEL_ID, ORDER_ITEM_ID, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }

    @Nested
    @DisplayName("CancelWithdrawnEvent 테스트")
    class CancelWithdrawnEventTest {

        @Test
        @DisplayName("취소 철회 이벤트를 올바르게 생성한다")
        void createCancelWithdrawnEvent() {
            Instant now = CommonVoFixtures.now();

            CancelWithdrawnEvent event = new CancelWithdrawnEvent(CANCEL_ID, ORDER_ITEM_ID, now);

            assertThat(event.cancelId()).isEqualTo(CANCEL_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            CancelWithdrawnEvent event1 = new CancelWithdrawnEvent(CANCEL_ID, ORDER_ITEM_ID, now);
            CancelWithdrawnEvent event2 = new CancelWithdrawnEvent(CANCEL_ID, ORDER_ITEM_ID, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }

    @Nested
    @DisplayName("CancelStatusChangedEvent 테스트")
    class CancelStatusChangedEventTest {

        @Test
        @DisplayName("취소 상태 변경 이벤트를 올바르게 생성한다")
        void createCancelStatusChangedEvent() {
            Instant now = CommonVoFixtures.now();

            CancelStatusChangedEvent event =
                    new CancelStatusChangedEvent(
                            CANCEL_ID,
                            ORDER_ITEM_ID,
                            CancelStatus.REQUESTED,
                            CancelStatus.APPROVED,
                            now);

            assertThat(event.cancelId()).isEqualTo(CANCEL_ID);
            assertThat(event.orderItemId()).isEqualTo(ORDER_ITEM_ID);
            assertThat(event.fromStatus()).isEqualTo(CancelStatus.REQUESTED);
            assertThat(event.toStatus()).isEqualTo(CancelStatus.APPROVED);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("REQUESTED에서 REJECTED로의 상태 변경 이벤트를 생성한다")
        void createRejectedStatusChangedEvent() {
            Instant now = CommonVoFixtures.now();

            CancelStatusChangedEvent event =
                    new CancelStatusChangedEvent(
                            CANCEL_ID,
                            ORDER_ITEM_ID,
                            CancelStatus.REQUESTED,
                            CancelStatus.REJECTED,
                            now);

            assertThat(event.fromStatus()).isEqualTo(CancelStatus.REQUESTED);
            assertThat(event.toStatus()).isEqualTo(CancelStatus.REJECTED);
        }

        @Test
        @DisplayName("APPROVED에서 COMPLETED로의 상태 변경 이벤트를 생성한다")
        void createCompletedStatusChangedEvent() {
            Instant now = CommonVoFixtures.now();

            CancelStatusChangedEvent event =
                    new CancelStatusChangedEvent(
                            CANCEL_ID,
                            ORDER_ITEM_ID,
                            CancelStatus.APPROVED,
                            CancelStatus.COMPLETED,
                            now);

            assertThat(event.fromStatus()).isEqualTo(CancelStatus.APPROVED);
            assertThat(event.toStatus()).isEqualTo(CancelStatus.COMPLETED);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            CancelStatusChangedEvent event1 =
                    new CancelStatusChangedEvent(
                            CANCEL_ID,
                            ORDER_ITEM_ID,
                            CancelStatus.REQUESTED,
                            CancelStatus.APPROVED,
                            now);
            CancelStatusChangedEvent event2 =
                    new CancelStatusChangedEvent(
                            CANCEL_ID,
                            ORDER_ITEM_ID,
                            CancelStatus.REQUESTED,
                            CancelStatus.APPROVED,
                            now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }
}
