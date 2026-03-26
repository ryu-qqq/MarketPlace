package com.ryuqq.marketplace.domain.settlement.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.event.SettlementCompletedEvent;
import com.ryuqq.marketplace.domain.settlement.event.SettlementCreatedEvent;
import com.ryuqq.marketplace.domain.settlement.event.SettlementHeldEvent;
import com.ryuqq.marketplace.domain.settlement.event.SettlementReleasedEvent;
import com.ryuqq.marketplace.domain.settlement.event.SettlementStatusChangedEvent;
import com.ryuqq.marketplace.domain.settlement.exception.SettlementException;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Settlement Aggregate 단위 테스트")
class SettlementTest {

    @Nested
    @DisplayName("forNew() - 신규 정산 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 정산을 생성하면 CALCULATING 상태이다")
        void createNewSettlementWithCalculatingStatus() {
            Settlement settlement = SettlementFixtures.newSettlement();

            assertThat(settlement.status()).isEqualTo(SettlementStatus.CALCULATING);
            assertThat(settlement.holdInfo()).isNull();
            assertThat(settlement.isHold()).isFalse();
            assertThat(settlement.settlementDay()).isNull();
            assertThat(settlement.entryCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("신규 정산 생성 시 SettlementCreatedEvent가 등록된다")
        void createdEventRegistered() {
            Settlement settlement = SettlementFixtures.newSettlement();

            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(SettlementCreatedEvent.class);
        }
    }

    @Nested
    @DisplayName("confirm() - CALCULATING → CONFIRMED")
    class ConfirmTest {

        @Test
        @DisplayName("CALCULATING 상태에서 CONFIRMED로 전이한다")
        void confirmFromCalculating() {
            Settlement settlement = SettlementFixtures.calculatingSettlement();
            Instant now = CommonVoFixtures.now();

            settlement.confirm(now);

            assertThat(settlement.status()).isEqualTo(SettlementStatus.CONFIRMED);
        }

        @Test
        @DisplayName("HOLD 상태에서 confirm 호출 시 예외")
        void confirmFromHoldThrows() {
            Settlement settlement = SettlementFixtures.heldSettlement();

            assertThatThrownBy(() -> settlement.confirm(CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }
    }

    @Nested
    @DisplayName("complete() - PAYOUT_REQUESTED → COMPLETED")
    class CompleteTest {

        @Test
        @DisplayName("PAYOUT_REQUESTED 상태에서 완료 처리한다")
        void completeFromPayoutRequested() {
            Settlement settlement = SettlementFixtures.confirmedSettlement();
            settlement.requestPayout(CommonVoFixtures.now());
            LocalDate settlementDay = LocalDate.now();

            settlement.complete(settlementDay, CommonVoFixtures.now());

            assertThat(settlement.status()).isEqualTo(SettlementStatus.COMPLETED);
            assertThat(settlement.settlementDay()).isEqualTo(settlementDay);
        }

        @Test
        @DisplayName("완료 처리 시 SettlementCompletedEvent와 SettlementStatusChangedEvent가 등록된다")
        void completedEventsRegistered() {
            Settlement settlement = SettlementFixtures.confirmedSettlement();
            settlement.requestPayout(CommonVoFixtures.now());
            settlement.pollEvents();

            settlement.complete(LocalDate.now(), CommonVoFixtures.now());

            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(SettlementCompletedEvent.class);
            assertThat(events.get(1)).isInstanceOf(SettlementStatusChangedEvent.class);
        }

        @Test
        @DisplayName("CALCULATING 상태에서 완료 처리하면 예외")
        void completeFromCalculatingThrows() {
            Settlement settlement = SettlementFixtures.calculatingSettlement();

            assertThatThrownBy(() -> settlement.complete(LocalDate.now(), CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }
    }

    @Nested
    @DisplayName("hold() - 정산 보류 처리")
    class HoldTest {

        @Test
        @DisplayName("CALCULATING 상태에서 보류 처리한다")
        void holdCalculatingSettlement() {
            Settlement settlement = SettlementFixtures.calculatingSettlement();

            settlement.hold("이상 거래 의심", CommonVoFixtures.now());

            assertThat(settlement.status()).isEqualTo(SettlementStatus.HOLD);
            assertThat(settlement.isHold()).isTrue();
            assertThat(settlement.holdInfo().holdReason()).isEqualTo("이상 거래 의심");
        }

        @Test
        @DisplayName("보류 처리 시 이벤트 2개 등록")
        void heldEventsRegistered() {
            Settlement settlement = SettlementFixtures.calculatingSettlement();

            settlement.hold("결제 분쟁 발생", CommonVoFixtures.now());

            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(SettlementHeldEvent.class);
            assertThat(events.get(1)).isInstanceOf(SettlementStatusChangedEvent.class);
        }

        @Test
        @DisplayName("보류 사유 null → 예외")
        void holdWithNullReasonThrows() {
            Settlement settlement = SettlementFixtures.calculatingSettlement();

            assertThatThrownBy(() -> settlement.hold(null, CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 보류 불가")
        void holdCompletedThrows() {
            Settlement settlement = SettlementFixtures.completedSettlement();

            assertThatThrownBy(() -> settlement.hold("보류 사유", CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }
    }

    @Nested
    @DisplayName("releaseHold() - 보류 해제")
    class ReleaseHoldTest {

        @Test
        @DisplayName("HOLD → CALCULATING 전환")
        void releaseHoldToCalculating() {
            Settlement settlement = SettlementFixtures.heldSettlement();

            settlement.releaseHold(CommonVoFixtures.now());

            assertThat(settlement.status()).isEqualTo(SettlementStatus.CALCULATING);
            assertThat(settlement.holdInfo()).isNull();
            assertThat(settlement.isHold()).isFalse();
        }

        @Test
        @DisplayName("보류 해제 시 이벤트 2개 등록")
        void releasedEventsRegistered() {
            Settlement settlement = SettlementFixtures.heldSettlement();

            settlement.releaseHold(CommonVoFixtures.now());

            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(SettlementReleasedEvent.class);
            assertThat(events.get(1)).isInstanceOf(SettlementStatusChangedEvent.class);
        }

        @Test
        @DisplayName("CALCULATING 상태에서 보류 해제 불가")
        void releaseCalculatingThrows() {
            Settlement settlement = SettlementFixtures.calculatingSettlement();

            assertThatThrownBy(() -> settlement.releaseHold(CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }
    }

    @Nested
    @DisplayName("pollEvents() - 이벤트 수집")
    class PollEventsTest {

        @Test
        @DisplayName("pollEvents 호출 후 이벤트 비워짐")
        void eventsAreClearedAfterPoll() {
            Settlement settlement = SettlementFixtures.newSettlement();

            List<DomainEvent> firstPoll = settlement.pollEvents();
            List<DomainEvent> secondPoll = settlement.pollEvents();

            assertThat(firstPoll).hasSize(1);
            assertThat(secondPoll).isEmpty();
        }

        @Test
        @DisplayName("pollEvents 결과는 불변 리스트")
        void pollEventsReturnsUnmodifiableList() {
            Settlement settlement = SettlementFixtures.newSettlement();

            List<DomainEvent> events = settlement.pollEvents();

            assertThatThrownBy(() -> events.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
