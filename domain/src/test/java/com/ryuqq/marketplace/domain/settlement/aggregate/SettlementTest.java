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
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
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
        @DisplayName("신규 정산을 생성하면 PENDING 상태이다")
        void createNewSettlementWithPendingStatus() {
            // when
            Settlement settlement = SettlementFixtures.newSettlement();

            // then
            assertThat(settlement.status()).isEqualTo(SettlementStatus.PENDING);
            assertThat(settlement.holdInfo()).isNull();
            assertThat(settlement.isHold()).isFalse();
            assertThat(settlement.settlementDay()).isNull();
        }

        @Test
        @DisplayName("신규 정산 생성 시 SettlementCreatedEvent가 등록된다")
        void createdEventRegistered() {
            // given
            SettlementId id = SettlementFixtures.defaultSettlementId();
            String orderId = "ORDER-20260101-001";
            Instant now = CommonVoFixtures.now();

            // when
            Settlement settlement =
                    Settlement.forNew(
                            id,
                            orderId,
                            1L,
                            SettlementFixtures.defaultSettlementAmounts(),
                            LocalDate.now().plusDays(14),
                            CommonVoFixtures.yesterday(),
                            now,
                            now);

            // then
            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(SettlementCreatedEvent.class);

            SettlementCreatedEvent event = (SettlementCreatedEvent) events.get(0);
            assertThat(event.settlementId()).isEqualTo(id);
            assertThat(event.orderId()).isEqualTo(orderId);
        }

        @Test
        @DisplayName("신규 정산 생성 시 기본 필드가 올바르게 설정된다")
        void fieldsSetCorrectly() {
            // given
            SettlementId id = SettlementFixtures.defaultSettlementId();
            String orderId = "ORDER-20260101-002";
            long sellerId = 42L;
            LocalDate expectedDay = LocalDate.now().plusDays(14);

            // when
            Settlement settlement =
                    Settlement.forNew(
                            id,
                            orderId,
                            sellerId,
                            SettlementFixtures.defaultSettlementAmounts(),
                            expectedDay,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.now(),
                            CommonVoFixtures.now());

            // then
            assertThat(settlement.id()).isEqualTo(id);
            assertThat(settlement.idValue()).isEqualTo(id.value());
            assertThat(settlement.orderId()).isEqualTo(orderId);
            assertThat(settlement.sellerId()).isEqualTo(sellerId);
            assertThat(settlement.expectedSettlementDay()).isEqualTo(expectedDay);
            assertThat(settlement.amounts()).isNotNull();
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("PENDING 상태로 재구성한다")
        void reconstitutePendingSettlement() {
            // when
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // then
            assertThat(settlement.status()).isEqualTo(SettlementStatus.PENDING);
            assertThat(settlement.holdInfo()).isNull();
            assertThat(settlement.isHold()).isFalse();
            assertThat(settlement.settlementDay()).isNull();
        }

        @Test
        @DisplayName("COMPLETED 상태로 재구성한다")
        void reconstituteCompletedSettlement() {
            // when
            Settlement settlement = SettlementFixtures.completedSettlement();

            // then
            assertThat(settlement.status()).isEqualTo(SettlementStatus.COMPLETED);
            assertThat(settlement.settlementDay()).isNotNull();
            assertThat(settlement.holdInfo()).isNull();
        }

        @Test
        @DisplayName("HOLD 상태로 재구성한다")
        void reconstituteHeldSettlement() {
            // when
            Settlement settlement = SettlementFixtures.heldSettlement();

            // then
            assertThat(settlement.status()).isEqualTo(SettlementStatus.HOLD);
            assertThat(settlement.holdInfo()).isNotNull();
            assertThat(settlement.isHold()).isTrue();
            assertThat(settlement.holdInfo().holdReason()).isNotBlank();
        }

        @Test
        @DisplayName("재구성 시 도메인 이벤트가 등록되지 않는다")
        void noEventsOnReconstitute() {
            // when
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // then
            assertThat(settlement.pollEvents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("complete() - 정산 완료 처리")
    class CompleteTest {

        @Test
        @DisplayName("PENDING 상태의 정산을 완료 처리한다")
        void completePendingSettlement() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();
            LocalDate settlementDay = LocalDate.now();
            Instant now = CommonVoFixtures.now();

            // when
            settlement.complete(settlementDay, now);

            // then
            assertThat(settlement.status()).isEqualTo(SettlementStatus.COMPLETED);
            assertThat(settlement.settlementDay()).isEqualTo(settlementDay);
            assertThat(settlement.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("완료 처리 시 SettlementCompletedEvent와 SettlementStatusChangedEvent가 등록된다")
        void completedEventsRegistered() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();
            Instant now = CommonVoFixtures.now();

            // when
            settlement.complete(LocalDate.now(), now);

            // then
            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(2);

            assertThat(events.get(0)).isInstanceOf(SettlementCompletedEvent.class);
            assertThat(events.get(1)).isInstanceOf(SettlementStatusChangedEvent.class);

            SettlementCompletedEvent completedEvent = (SettlementCompletedEvent) events.get(0);
            assertThat(completedEvent.sellerId()).isEqualTo(settlement.sellerId());

            SettlementStatusChangedEvent changedEvent =
                    (SettlementStatusChangedEvent) events.get(1);
            assertThat(changedEvent.fromStatus()).isEqualTo(SettlementStatus.PENDING);
            assertThat(changedEvent.toStatus()).isEqualTo(SettlementStatus.COMPLETED);
        }

        @Test
        @DisplayName("HOLD 상태에서 완료 처리하면 예외가 발생한다")
        void completeHeldSettlement_ThrowsException() {
            // given
            Settlement settlement = SettlementFixtures.heldSettlement();

            // when & then
            assertThatThrownBy(() -> settlement.complete(LocalDate.now(), CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 다시 완료 처리하면 예외가 발생한다")
        void completeAlreadyCompletedSettlement_ThrowsException() {
            // given
            Settlement settlement = SettlementFixtures.completedSettlement();

            // when & then
            assertThatThrownBy(() -> settlement.complete(LocalDate.now(), CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }
    }

    @Nested
    @DisplayName("hold() - 정산 보류 처리")
    class HoldTest {

        @Test
        @DisplayName("PENDING 상태의 정산을 보류 처리한다")
        void holdPendingSettlement() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();
            String reason = "이상 거래 의심";
            Instant now = CommonVoFixtures.now();

            // when
            settlement.hold(reason, now);

            // then
            assertThat(settlement.status()).isEqualTo(SettlementStatus.HOLD);
            assertThat(settlement.isHold()).isTrue();
            assertThat(settlement.holdInfo()).isNotNull();
            assertThat(settlement.holdInfo().holdReason()).isEqualTo(reason);
            assertThat(settlement.holdInfo().holdAt()).isEqualTo(now);
            assertThat(settlement.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("보류 처리 시 SettlementHeldEvent와 SettlementStatusChangedEvent가 등록된다")
        void heldEventsRegistered() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();
            String reason = "결제 분쟁 발생";
            Instant now = CommonVoFixtures.now();

            // when
            settlement.hold(reason, now);

            // then
            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(2);

            assertThat(events.get(0)).isInstanceOf(SettlementHeldEvent.class);
            assertThat(events.get(1)).isInstanceOf(SettlementStatusChangedEvent.class);

            SettlementHeldEvent heldEvent = (SettlementHeldEvent) events.get(0);
            assertThat(heldEvent.holdReason()).isEqualTo(reason);
            assertThat(heldEvent.sellerId()).isEqualTo(settlement.sellerId());

            SettlementStatusChangedEvent changedEvent =
                    (SettlementStatusChangedEvent) events.get(1);
            assertThat(changedEvent.fromStatus()).isEqualTo(SettlementStatus.PENDING);
            assertThat(changedEvent.toStatus()).isEqualTo(SettlementStatus.HOLD);
        }

        @Test
        @DisplayName("보류 사유가 null이면 예외가 발생한다")
        void holdWithNullReason_ThrowsException() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // when & then
            assertThatThrownBy(() -> settlement.hold(null, CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }

        @Test
        @DisplayName("보류 사유가 빈 문자열이면 예외가 발생한다")
        void holdWithBlankReason_ThrowsException() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // when & then
            assertThatThrownBy(() -> settlement.hold("   ", CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }

        @Test
        @DisplayName("HOLD 상태에서 다시 보류 처리하면 예외가 발생한다")
        void holdAlreadyHeldSettlement_ThrowsException() {
            // given
            Settlement settlement = SettlementFixtures.heldSettlement();

            // when & then
            assertThatThrownBy(() -> settlement.hold("추가 보류 사유", CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 보류 처리하면 예외가 발생한다")
        void holdCompletedSettlement_ThrowsException() {
            // given
            Settlement settlement = SettlementFixtures.completedSettlement();

            // when & then
            assertThatThrownBy(() -> settlement.hold("보류 사유", CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }
    }

    @Nested
    @DisplayName("releaseHold() - 보류 해제 처리")
    class ReleaseHoldTest {

        @Test
        @DisplayName("HOLD 상태의 정산을 PENDING으로 전환한다")
        void releaseHoldToTransitionToPending() {
            // given
            Settlement settlement = SettlementFixtures.heldSettlement();
            Instant now = CommonVoFixtures.now();

            // when
            settlement.releaseHold(now);

            // then
            assertThat(settlement.status()).isEqualTo(SettlementStatus.PENDING);
            assertThat(settlement.holdInfo()).isNull();
            assertThat(settlement.isHold()).isFalse();
            assertThat(settlement.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("보류 해제 시 SettlementReleasedEvent와 SettlementStatusChangedEvent가 등록된다")
        void releasedEventsRegistered() {
            // given
            Settlement settlement = SettlementFixtures.heldSettlement();
            Instant now = CommonVoFixtures.now();

            // when
            settlement.releaseHold(now);

            // then
            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(2);

            assertThat(events.get(0)).isInstanceOf(SettlementReleasedEvent.class);
            assertThat(events.get(1)).isInstanceOf(SettlementStatusChangedEvent.class);

            SettlementReleasedEvent releasedEvent = (SettlementReleasedEvent) events.get(0);
            assertThat(releasedEvent.sellerId()).isEqualTo(settlement.sellerId());

            SettlementStatusChangedEvent changedEvent =
                    (SettlementStatusChangedEvent) events.get(1);
            assertThat(changedEvent.fromStatus()).isEqualTo(SettlementStatus.HOLD);
            assertThat(changedEvent.toStatus()).isEqualTo(SettlementStatus.PENDING);
        }

        @Test
        @DisplayName("PENDING 상태에서 보류 해제하면 예외가 발생한다")
        void releasePendingSettlement_ThrowsException() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // when & then
            assertThatThrownBy(() -> settlement.releaseHold(CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 보류 해제하면 예외가 발생한다")
        void releaseCompletedSettlement_ThrowsException() {
            // given
            Settlement settlement = SettlementFixtures.completedSettlement();

            // when & then
            assertThatThrownBy(() -> settlement.releaseHold(CommonVoFixtures.now()))
                    .isInstanceOf(SettlementException.class);
        }
    }

    @Nested
    @DisplayName("isHold() - 보류 상태 확인")
    class IsHoldTest {

        @Test
        @DisplayName("holdInfo가 null이 아니면 isHold가 true이다")
        void isHoldTrueWhenHoldInfoPresent() {
            // when
            Settlement settlement = SettlementFixtures.heldSettlement();

            // then
            assertThat(settlement.isHold()).isTrue();
        }

        @Test
        @DisplayName("holdInfo가 null이면 isHold가 false이다")
        void isHoldFalseWhenNoHoldInfo() {
            // when
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // then
            assertThat(settlement.isHold()).isFalse();
        }

        @Test
        @DisplayName("보류 후 해제하면 isHold가 false가 된다")
        void isHoldFalseAfterRelease() {
            // given
            Settlement settlement = SettlementFixtures.heldSettlement();

            // when
            settlement.releaseHold(CommonVoFixtures.now());

            // then
            assertThat(settlement.isHold()).isFalse();
        }
    }

    @Nested
    @DisplayName("pollEvents() - 이벤트 수집")
    class PollEventsTest {

        @Test
        @DisplayName("pollEvents 호출 후 이벤트가 비워진다")
        void eventsAreClearedAfterPoll() {
            // given
            Settlement settlement = SettlementFixtures.newSettlement();

            // when
            List<DomainEvent> firstPoll = settlement.pollEvents();
            List<DomainEvent> secondPoll = settlement.pollEvents();

            // then
            assertThat(firstPoll).hasSize(1);
            assertThat(secondPoll).isEmpty();
        }

        @Test
        @DisplayName("pollEvents 결과는 불변 리스트이다")
        void pollEventsReturnsUnmodifiableList() {
            // given
            Settlement settlement = SettlementFixtures.newSettlement();

            // when
            List<DomainEvent> events = settlement.pollEvents();

            // then
            assertThatThrownBy(() -> events.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("complete 처리 후 2개의 이벤트가 등록된다")
        void twoEventsAfterComplete() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // when
            settlement.complete(LocalDate.now(), CommonVoFixtures.now());

            // then
            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(2);
        }

        @Test
        @DisplayName("hold 처리 후 2개의 이벤트가 등록된다")
        void twoEventsAfterHold() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // when
            settlement.hold("보류 사유", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(2);
        }

        @Test
        @DisplayName("releaseHold 처리 후 2개의 이벤트가 등록된다")
        void twoEventsAfterReleaseHold() {
            // given
            Settlement settlement = SettlementFixtures.heldSettlement();

            // when
            settlement.releaseHold(CommonVoFixtures.now());

            // then
            List<DomainEvent> events = settlement.pollEvents();
            assertThat(events).hasSize(2);
        }
    }

    @Nested
    @DisplayName("SettlementStatusChangedEvent - 상태 변경 이벤트 검증")
    class StatusChangedEventTest {

        @Test
        @DisplayName("완료 처리 시 StatusChangedEvent의 fromStatus와 toStatus가 올바르다")
        void statusChangedEventOnComplete() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // when
            settlement.complete(LocalDate.now(), CommonVoFixtures.now());

            // then
            List<DomainEvent> events = settlement.pollEvents();
            SettlementStatusChangedEvent changedEvent =
                    events.stream()
                            .filter(e -> e instanceof SettlementStatusChangedEvent)
                            .map(e -> (SettlementStatusChangedEvent) e)
                            .findFirst()
                            .orElseThrow();

            assertThat(changedEvent.fromStatus()).isEqualTo(SettlementStatus.PENDING);
            assertThat(changedEvent.toStatus()).isEqualTo(SettlementStatus.COMPLETED);
        }

        @Test
        @DisplayName("보류 처리 시 StatusChangedEvent의 fromStatus와 toStatus가 올바르다")
        void statusChangedEventOnHold() {
            // given
            Settlement settlement = SettlementFixtures.pendingSettlement();

            // when
            settlement.hold("보류 사유", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = settlement.pollEvents();
            SettlementStatusChangedEvent changedEvent =
                    events.stream()
                            .filter(e -> e instanceof SettlementStatusChangedEvent)
                            .map(e -> (SettlementStatusChangedEvent) e)
                            .findFirst()
                            .orElseThrow();

            assertThat(changedEvent.fromStatus()).isEqualTo(SettlementStatus.PENDING);
            assertThat(changedEvent.toStatus()).isEqualTo(SettlementStatus.HOLD);
        }
    }
}
