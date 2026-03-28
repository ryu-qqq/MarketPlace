package com.ryuqq.marketplace.domain.settlement.entry.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementEntry 도메인 이벤트 단위 테스트")
class SettlementEntryEventsTest {

    private static final SettlementEntryId ENTRY_ID =
            SettlementEntryId.of("01900000-0000-7000-8000-000000000002");
    private static final long SELLER_ID = 100L;

    @Nested
    @DisplayName("SettlementEntryCreatedEvent 테스트")
    class SettlementEntryCreatedEventTest {

        @Test
        @DisplayName("판매 Entry 생성 이벤트를 올바르게 생성한다")
        void createSalesEntryCreatedEvent() {
            Instant now = CommonVoFixtures.now();
            Long orderItemId = 1001L;

            SettlementEntryCreatedEvent event =
                    new SettlementEntryCreatedEvent(
                            ENTRY_ID, SELLER_ID, EntryType.SALES, orderItemId, now);

            assertThat(event.entryId()).isEqualTo(ENTRY_ID);
            assertThat(event.sellerId()).isEqualTo(SELLER_ID);
            assertThat(event.entryType()).isEqualTo(EntryType.SALES);
            assertThat(event.orderItemId()).isEqualTo(orderItemId);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("취소 역분개 Entry 생성 이벤트를 올바르게 생성한다")
        void createCancelReversalEntryCreatedEvent() {
            Instant now = CommonVoFixtures.now();
            Long orderItemId = 1002L;

            SettlementEntryCreatedEvent event =
                    new SettlementEntryCreatedEvent(
                            ENTRY_ID, SELLER_ID, EntryType.CANCEL, orderItemId, now);

            assertThat(event.entryType()).isEqualTo(EntryType.CANCEL);
            assertThat(event.orderItemId()).isEqualTo(orderItemId);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            SettlementEntryCreatedEvent event1 =
                    new SettlementEntryCreatedEvent(
                            ENTRY_ID, SELLER_ID, EntryType.SALES, 1001L, now);
            SettlementEntryCreatedEvent event2 =
                    new SettlementEntryCreatedEvent(
                            ENTRY_ID, SELLER_ID, EntryType.SALES, 1001L, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }

    @Nested
    @DisplayName("SettlementEntryConfirmedEvent 테스트")
    class SettlementEntryConfirmedEventTest {

        @Test
        @DisplayName("정산 원장 확정 이벤트를 올바르게 생성한다")
        void createSettlementEntryConfirmedEvent() {
            Instant now = CommonVoFixtures.now();

            SettlementEntryConfirmedEvent event =
                    new SettlementEntryConfirmedEvent(ENTRY_ID, SELLER_ID, now);

            assertThat(event.entryId()).isEqualTo(ENTRY_ID);
            assertThat(event.sellerId()).isEqualTo(SELLER_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            SettlementEntryConfirmedEvent event1 =
                    new SettlementEntryConfirmedEvent(ENTRY_ID, SELLER_ID, now);
            SettlementEntryConfirmedEvent event2 =
                    new SettlementEntryConfirmedEvent(ENTRY_ID, SELLER_ID, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }

    @Nested
    @DisplayName("SettlementEntryStatusChangedEvent 테스트")
    class SettlementEntryStatusChangedEventTest {

        @Test
        @DisplayName("정산 원장 상태 변경 이벤트를 올바르게 생성한다")
        void createSettlementEntryStatusChangedEvent() {
            Instant now = CommonVoFixtures.now();

            SettlementEntryStatusChangedEvent event =
                    new SettlementEntryStatusChangedEvent(
                            ENTRY_ID, EntryStatus.PENDING, EntryStatus.CONFIRMED, now);

            assertThat(event.entryId()).isEqualTo(ENTRY_ID);
            assertThat(event.fromStatus()).isEqualTo(EntryStatus.PENDING);
            assertThat(event.toStatus()).isEqualTo(EntryStatus.CONFIRMED);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("CONFIRMED에서 SETTLED로의 상태 변경 이벤트를 생성한다")
        void createSettledStatusChangedEvent() {
            Instant now = CommonVoFixtures.now();

            SettlementEntryStatusChangedEvent event =
                    new SettlementEntryStatusChangedEvent(
                            ENTRY_ID, EntryStatus.CONFIRMED, EntryStatus.SETTLED, now);

            assertThat(event.fromStatus()).isEqualTo(EntryStatus.CONFIRMED);
            assertThat(event.toStatus()).isEqualTo(EntryStatus.SETTLED);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            SettlementEntryStatusChangedEvent event1 =
                    new SettlementEntryStatusChangedEvent(
                            ENTRY_ID, EntryStatus.PENDING, EntryStatus.CONFIRMED, now);
            SettlementEntryStatusChangedEvent event2 =
                    new SettlementEntryStatusChangedEvent(
                            ENTRY_ID, EntryStatus.PENDING, EntryStatus.CONFIRMED, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }
}
