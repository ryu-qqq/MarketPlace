package com.ryuqq.marketplace.domain.settlement.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Settlement 도메인 이벤트 단위 테스트")
class SettlementEventsTest {

    private static final SettlementId SETTLEMENT_ID = SettlementFixtures.defaultSettlementId();
    private static final long SELLER_ID = 1L;

    @Nested
    @DisplayName("SettlementCreatedEvent 테스트")
    class SettlementCreatedEventTest {

        @Test
        @DisplayName("정산 생성 이벤트를 올바르게 생성한다")
        void createSettlementCreatedEvent() {
            Instant now = CommonVoFixtures.now();

            SettlementCreatedEvent event =
                    new SettlementCreatedEvent(SETTLEMENT_ID, SELLER_ID, now);

            assertThat(event.settlementId()).isEqualTo(SETTLEMENT_ID);
            assertThat(event.sellerId()).isEqualTo(SELLER_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            SettlementCreatedEvent event1 =
                    new SettlementCreatedEvent(SETTLEMENT_ID, SELLER_ID, now);
            SettlementCreatedEvent event2 =
                    new SettlementCreatedEvent(SETTLEMENT_ID, SELLER_ID, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }

    @Nested
    @DisplayName("SettlementCompletedEvent 테스트")
    class SettlementCompletedEventTest {

        @Test
        @DisplayName("정산 완료 이벤트를 올바르게 생성한다")
        void createSettlementCompletedEvent() {
            Instant now = CommonVoFixtures.now();

            SettlementCompletedEvent event =
                    new SettlementCompletedEvent(SETTLEMENT_ID, SELLER_ID, now);

            assertThat(event.settlementId()).isEqualTo(SETTLEMENT_ID);
            assertThat(event.sellerId()).isEqualTo(SELLER_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            SettlementCompletedEvent event1 =
                    new SettlementCompletedEvent(SETTLEMENT_ID, SELLER_ID, now);
            SettlementCompletedEvent event2 =
                    new SettlementCompletedEvent(SETTLEMENT_ID, SELLER_ID, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }

    @Nested
    @DisplayName("SettlementHeldEvent 테스트")
    class SettlementHeldEventTest {

        @Test
        @DisplayName("정산 보류 이벤트를 올바르게 생성한다")
        void createSettlementHeldEvent() {
            Instant now = CommonVoFixtures.now();
            String holdReason = "이상 거래 의심으로 인한 보류";

            SettlementHeldEvent event =
                    new SettlementHeldEvent(SETTLEMENT_ID, SELLER_ID, holdReason, now);

            assertThat(event.settlementId()).isEqualTo(SETTLEMENT_ID);
            assertThat(event.sellerId()).isEqualTo(SELLER_ID);
            assertThat(event.holdReason()).isEqualTo(holdReason);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("보류 사유가 이벤트에 포함된다")
        void holdReasonIsIncludedInEvent() {
            Instant now = CommonVoFixtures.now();
            String reason = "결제 분쟁 발생";

            SettlementHeldEvent event =
                    new SettlementHeldEvent(SETTLEMENT_ID, SELLER_ID, reason, now);

            assertThat(event.holdReason()).isEqualTo(reason);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();
            String reason = "테스트 보류 사유";

            SettlementHeldEvent event1 =
                    new SettlementHeldEvent(SETTLEMENT_ID, SELLER_ID, reason, now);
            SettlementHeldEvent event2 =
                    new SettlementHeldEvent(SETTLEMENT_ID, SELLER_ID, reason, now);

            assertThat(event1).isEqualTo(event2);
        }
    }

    @Nested
    @DisplayName("SettlementReleasedEvent 테스트")
    class SettlementReleasedEventTest {

        @Test
        @DisplayName("정산 보류 해제 이벤트를 올바르게 생성한다")
        void createSettlementReleasedEvent() {
            Instant now = CommonVoFixtures.now();

            SettlementReleasedEvent event =
                    new SettlementReleasedEvent(SETTLEMENT_ID, SELLER_ID, now);

            assertThat(event.settlementId()).isEqualTo(SETTLEMENT_ID);
            assertThat(event.sellerId()).isEqualTo(SELLER_ID);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            SettlementReleasedEvent event1 =
                    new SettlementReleasedEvent(SETTLEMENT_ID, SELLER_ID, now);
            SettlementReleasedEvent event2 =
                    new SettlementReleasedEvent(SETTLEMENT_ID, SELLER_ID, now);

            assertThat(event1).isEqualTo(event2);
        }
    }

    @Nested
    @DisplayName("SettlementStatusChangedEvent 테스트")
    class SettlementStatusChangedEventTest {

        @Test
        @DisplayName("정산 상태 변경 이벤트를 올바르게 생성한다")
        void createSettlementStatusChangedEvent() {
            Instant now = CommonVoFixtures.now();
            SettlementStatus from = SettlementStatus.CALCULATING;
            SettlementStatus to = SettlementStatus.CONFIRMED;

            SettlementStatusChangedEvent event =
                    new SettlementStatusChangedEvent(SETTLEMENT_ID, SELLER_ID, from, to, now);

            assertThat(event.settlementId()).isEqualTo(SETTLEMENT_ID);
            assertThat(event.sellerId()).isEqualTo(SELLER_ID);
            assertThat(event.fromStatus()).isEqualTo(from);
            assertThat(event.toStatus()).isEqualTo(to);
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("CALCULATING에서 HOLD로의 상태 변경 이벤트를 생성한다")
        void createHoldStatusChangedEvent() {
            Instant now = CommonVoFixtures.now();

            SettlementStatusChangedEvent event =
                    new SettlementStatusChangedEvent(
                            SETTLEMENT_ID,
                            SELLER_ID,
                            SettlementStatus.CALCULATING,
                            SettlementStatus.HOLD,
                            now);

            assertThat(event.fromStatus()).isEqualTo(SettlementStatus.CALCULATING);
            assertThat(event.toStatus()).isEqualTo(SettlementStatus.HOLD);
        }

        @Test
        @DisplayName("같은 값이면 동일하다")
        void equalEvents() {
            Instant now = CommonVoFixtures.now();

            SettlementStatusChangedEvent event1 =
                    new SettlementStatusChangedEvent(
                            SETTLEMENT_ID,
                            SELLER_ID,
                            SettlementStatus.CALCULATING,
                            SettlementStatus.CONFIRMED,
                            now);
            SettlementStatusChangedEvent event2 =
                    new SettlementStatusChangedEvent(
                            SETTLEMENT_ID,
                            SELLER_ID,
                            SettlementStatus.CALCULATING,
                            SettlementStatus.CONFIRMED,
                            now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }
    }
}
