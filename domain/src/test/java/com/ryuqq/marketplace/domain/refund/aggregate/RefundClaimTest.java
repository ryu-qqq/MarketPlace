package com.ryuqq.marketplace.domain.refund.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.event.RefundCancelledEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundClaimCreatedEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundClaimStatusChangedEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundCollectedEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundCollectingEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundCompletedEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundRejectedEvent;
import com.ryuqq.marketplace.domain.refund.exception.RefundException;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundClaim Aggregate 단위 테스트")
class RefundClaimTest {

    @Nested
    @DisplayName("forNew() - 환불 클레임 신규 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 필드로 새 환불 클레임을 생성한다")
        void createNewRefundClaim() {
            // when
            RefundClaim claim = RefundFixtures.newRefundClaim();

            // then
            assertThat(claim).isNotNull();
            assertThat(claim.id()).isEqualTo(RefundFixtures.defaultRefundClaimId());
            assertThat(claim.claimNumber()).isEqualTo(RefundFixtures.defaultRefundClaimNumber());
            assertThat(claim.orderItemId()).isEqualTo(RefundFixtures.defaultOrderItemId());
            assertThat(claim.sellerId()).isEqualTo(10L);
            assertThat(claim.refundQty()).isEqualTo(1);
            assertThat(claim.status()).isEqualTo(RefundStatus.REQUESTED);
            assertThat(claim.reason()).isEqualTo(RefundFixtures.defaultRefundReason());
            assertThat(claim.refundInfo()).isNull();
            assertThat(claim.holdInfo()).isNull();
            assertThat(claim.processedBy()).isNull();
            assertThat(claim.processedAt()).isNull();
            assertThat(claim.completedAt()).isNull();
        }

        @Test
        @DisplayName("신규 생성 시 REQUESTED 상태이다")
        void newClaimHasRequestedStatus() {
            // when
            RefundClaim claim = RefundFixtures.newRefundClaim();

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.REQUESTED);
        }

        @Test
        @DisplayName("신규 생성 시 RefundClaimCreatedEvent가 등록된다")
        void createdEventRegistered() {
            // when
            RefundClaim claim = RefundFixtures.newRefundClaim();

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(RefundClaimCreatedEvent.class);

            RefundClaimCreatedEvent event = (RefundClaimCreatedEvent) events.get(0);
            assertThat(event.refundClaimId()).isEqualTo(claim.id());
            assertThat(event.orderItemId()).isEqualTo(claim.orderItemId());
        }

        @Test
        @DisplayName("환불 수량이 0이면 예외가 발생한다")
        void createWithZeroQty_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    RefundClaim.forNew(
                                            RefundFixtures.defaultRefundClaimId(),
                                            RefundFixtures.defaultRefundClaimNumber(),
                                            RefundFixtures.defaultOrderItemId(),
                                            10L,
                                            0,
                                            RefundFixtures.defaultRefundReason(),
                                            "customer@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("환불 수량이 음수이면 예외가 발생한다")
        void createWithNegativeQty_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    RefundClaim.forNew(
                                            RefundFixtures.defaultRefundClaimId(),
                                            RefundFixtures.defaultRefundClaimNumber(),
                                            RefundFixtures.defaultOrderItemId(),
                                            10L,
                                            -1,
                                            RefundFixtures.defaultRefundReason(),
                                            "customer@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("REQUESTED 상태로 재구성한다")
        void reconstituteRequestedClaim() {
            // when
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.REQUESTED);
            assertThat(claim.refundInfo()).isNull();
            assertThat(claim.processedBy()).isNull();
        }

        @Test
        @DisplayName("COLLECTING 상태로 재구성한다")
        void reconstituteCollectingClaim() {
            // when
            RefundClaim claim = RefundFixtures.collectingRefundClaim();

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.COLLECTING);
            assertThat(claim.processedBy()).isNotNull();
        }

        @Test
        @DisplayName("COMPLETED 상태로 재구성한다")
        void reconstituteCompletedClaim() {
            // when
            RefundClaim claim = RefundFixtures.completedRefundClaim();

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.COMPLETED);
            assertThat(claim.refundInfo()).isNotNull();
            assertThat(claim.completedAt()).isNotNull();
        }

        @Test
        @DisplayName("재구성 시 도메인 이벤트가 등록되지 않는다")
        void reconstituteDoesNotRegisterEvents() {
            // when
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).isEmpty();
        }
    }

    @Nested
    @DisplayName("startCollecting() - 수거 시작")
    class StartCollectingTest {

        @Test
        @DisplayName("REQUESTED 상태에서 수거를 시작한다")
        void startCollectingFromRequested() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.startCollecting("admin@marketplace.com", now);

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.COLLECTING);
            assertThat(claim.processedBy()).isEqualTo("admin@marketplace.com");
            assertThat(claim.processedAt()).isEqualTo(now);
            assertThat(claim.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("수거 시작 시 RefundCollectingEvent와 RefundClaimStatusChangedEvent가 등록된다")
        void startCollectingRegistersEvents() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when
            claim.startCollecting("admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(RefundCollectingEvent.class);
            assertThat(events.get(1)).isInstanceOf(RefundClaimStatusChangedEvent.class);

            RefundClaimStatusChangedEvent statusEvent =
                    (RefundClaimStatusChangedEvent) events.get(1);
            assertThat(statusEvent.fromStatus()).isEqualTo(RefundStatus.REQUESTED);
            assertThat(statusEvent.toStatus()).isEqualTo(RefundStatus.COLLECTING);
        }

        @Test
        @DisplayName("COLLECTING 상태에서 다시 수거를 시작하면 예외가 발생한다")
        void startCollectingFromCollecting_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.collectingRefundClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.startCollecting(
                                            "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 수거를 시작하면 예외가 발생한다")
        void startCollectingFromCompleted_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.completedRefundClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.startCollecting(
                                            "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }
    }

    @Nested
    @DisplayName("completeCollection() - 수거 완료")
    class CompleteCollectionTest {

        @Test
        @DisplayName("COLLECTING 상태에서 수거를 완료한다")
        void completeCollectionFromCollecting() {
            // given
            RefundClaim claim = RefundFixtures.collectingRefundClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.completeCollection("admin@marketplace.com", now);

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.COLLECTED);
            assertThat(claim.processedBy()).isEqualTo("admin@marketplace.com");
            assertThat(claim.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("수거 완료 시 RefundCollectedEvent와 RefundClaimStatusChangedEvent가 등록된다")
        void completeCollectionRegistersEvents() {
            // given
            RefundClaim claim = RefundFixtures.collectingRefundClaim();

            // when
            claim.completeCollection("admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(RefundCollectedEvent.class);
            assertThat(events.get(1)).isInstanceOf(RefundClaimStatusChangedEvent.class);

            RefundClaimStatusChangedEvent statusEvent =
                    (RefundClaimStatusChangedEvent) events.get(1);
            assertThat(statusEvent.fromStatus()).isEqualTo(RefundStatus.COLLECTING);
            assertThat(statusEvent.toStatus()).isEqualTo(RefundStatus.COLLECTED);
        }

        @Test
        @DisplayName("REQUESTED 상태에서 수거 완료하면 예외가 발생한다")
        void completeCollectionFromRequested_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.completeCollection(
                                            "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("COLLECTED 상태에서 다시 수거 완료하면 예외가 발생한다")
        void completeCollectionFromCollected_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.collectedRefundClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.completeCollection(
                                            "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }
    }

    @Nested
    @DisplayName("complete() - 환불 완료 처리")
    class CompleteTest {

        @Test
        @DisplayName("COLLECTED 상태에서 환불을 완료한다")
        void completeFromCollected() {
            // given
            RefundClaim claim = RefundFixtures.collectedRefundClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.complete(RefundFixtures.defaultRefundInfo(), "admin@marketplace.com", now);

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.COMPLETED);
            assertThat(claim.refundInfo()).isNotNull();
            assertThat(claim.processedBy()).isEqualTo("admin@marketplace.com");
            assertThat(claim.completedAt()).isEqualTo(now);
            assertThat(claim.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("환불 완료 시 RefundCompletedEvent와 RefundClaimStatusChangedEvent가 등록된다")
        void completeRegistersEvents() {
            // given
            RefundClaim claim = RefundFixtures.collectedRefundClaim();

            // when
            claim.complete(
                    RefundFixtures.defaultRefundInfo(),
                    "admin@marketplace.com",
                    CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(RefundCompletedEvent.class);
            assertThat(events.get(1)).isInstanceOf(RefundClaimStatusChangedEvent.class);

            RefundClaimStatusChangedEvent statusEvent =
                    (RefundClaimStatusChangedEvent) events.get(1);
            assertThat(statusEvent.fromStatus()).isEqualTo(RefundStatus.COLLECTED);
            assertThat(statusEvent.toStatus()).isEqualTo(RefundStatus.COMPLETED);
        }

        @Test
        @DisplayName("COLLECTING 상태에서 환불 완료하면 예외가 발생한다")
        void completeFromCollecting_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.collectingRefundClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.complete(
                                            RefundFixtures.defaultRefundInfo(),
                                            "admin@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("REQUESTED 상태에서 환불 완료하면 예외가 발생한다")
        void completeFromRequested_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.complete(
                                            RefundFixtures.defaultRefundInfo(),
                                            "admin@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }
    }

    @Nested
    @DisplayName("reject() - 환불 거절")
    class RejectTest {

        @Test
        @DisplayName("REQUESTED 상태에서 거절한다")
        void rejectFromRequested() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.reject("admin@marketplace.com", now);

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.REJECTED);
            assertThat(claim.processedBy()).isEqualTo("admin@marketplace.com");
            assertThat(claim.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("COLLECTING 상태에서 거절한다")
        void rejectFromCollecting() {
            // given
            RefundClaim claim = RefundFixtures.collectingRefundClaim();

            // when
            claim.reject("admin@marketplace.com", CommonVoFixtures.now());

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.REJECTED);
        }

        @Test
        @DisplayName("COLLECTED 상태에서 거절한다")
        void rejectFromCollected() {
            // given
            RefundClaim claim = RefundFixtures.collectedRefundClaim();

            // when
            claim.reject("admin@marketplace.com", CommonVoFixtures.now());

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.REJECTED);
        }

        @Test
        @DisplayName("거절 시 RefundRejectedEvent와 RefundClaimStatusChangedEvent가 등록된다")
        void rejectRegistersEvents() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when
            claim.reject("admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(RefundRejectedEvent.class);
            assertThat(events.get(1)).isInstanceOf(RefundClaimStatusChangedEvent.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 거절하면 예외가 발생한다")
        void rejectFromCompleted_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.completedRefundClaim();

            // when & then
            assertThatThrownBy(() -> claim.reject("admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("CANCELLED 상태에서 거절하면 예외가 발생한다")
        void rejectFromCancelled_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.cancelledRefundClaim();

            // when & then
            assertThatThrownBy(() -> claim.reject("admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }
    }

    @Nested
    @DisplayName("cancel() - 환불 철회")
    class CancelTest {

        @Test
        @DisplayName("REQUESTED 상태에서 철회한다")
        void cancelFromRequested() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.cancel(now);

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.CANCELLED);
            assertThat(claim.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("COLLECTING 상태에서 철회한다")
        void cancelFromCollecting() {
            // given
            RefundClaim claim = RefundFixtures.collectingRefundClaim();

            // when
            claim.cancel(CommonVoFixtures.now());

            // then
            assertThat(claim.status()).isEqualTo(RefundStatus.CANCELLED);
        }

        @Test
        @DisplayName("철회 시 RefundCancelledEvent와 RefundClaimStatusChangedEvent가 등록된다")
        void cancelRegistersEvents() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when
            claim.cancel(CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(RefundCancelledEvent.class);
            assertThat(events.get(1)).isInstanceOf(RefundClaimStatusChangedEvent.class);
        }

        @Test
        @DisplayName("COLLECTED 상태에서 철회하면 예외가 발생한다")
        void cancelFromCollected_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.collectedRefundClaim();

            // when & then
            assertThatThrownBy(() -> claim.cancel(CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 철회하면 예외가 발생한다")
        void cancelFromCompleted_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.completedRefundClaim();

            // when & then
            assertThatThrownBy(() -> claim.cancel(CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }
    }

    @Nested
    @DisplayName("hold() - 환불 보류")
    class HoldTest {

        @Test
        @DisplayName("보류 사유와 함께 보류 상태로 전환한다")
        void holdWithReason() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.hold("추가 서류 확인 필요", now);

            // then
            assertThat(claim.isHold()).isTrue();
            assertThat(claim.holdInfo()).isNotNull();
            assertThat(claim.holdInfo().holdReason()).isEqualTo("추가 서류 확인 필요");
            assertThat(claim.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("보류 사유가 null이면 예외가 발생한다")
        void holdWithNullReason_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when & then
            assertThatThrownBy(() -> claim.hold(null, CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("보류 사유가 빈 문자열이면 예외가 발생한다")
        void holdWithBlankReason_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when & then
            assertThatThrownBy(() -> claim.hold("   ", CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("이미 보류 중인 클레임에 다시 보류를 설정하면 예외가 발생한다")
        void holdAlreadyHeld_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.holdRefundClaim();

            // when & then
            assertThatThrownBy(() -> claim.hold("또 다른 사유", CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("COLLECTING 상태에서도 보류를 설정할 수 있다")
        void holdFromCollecting() {
            // given
            RefundClaim claim = RefundFixtures.collectingRefundClaim();

            // when & then
            assertThatCode(() -> claim.hold("수거 중 문제 발생", CommonVoFixtures.now()))
                    .doesNotThrowAnyException();
            assertThat(claim.isHold()).isTrue();
        }
    }

    @Nested
    @DisplayName("releaseHold() - 보류 해제")
    class ReleaseHoldTest {

        @Test
        @DisplayName("보류 중인 클레임의 보류를 해제한다")
        void releaseHoldFromHeldClaim() {
            // given
            RefundClaim claim = RefundFixtures.holdRefundClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.releaseHold(now);

            // then
            assertThat(claim.isHold()).isFalse();
            assertThat(claim.holdInfo()).isNull();
            assertThat(claim.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("보류 상태가 아닌 클레임의 보류를 해제하면 예외가 발생한다")
        void releaseHoldFromNonHeldClaim_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when & then
            assertThatThrownBy(() -> claim.releaseHold(CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }
    }

    @Nested
    @DisplayName("updateReason() - 환불 사유 변경")
    class UpdateReasonTest {

        @Test
        @DisplayName("REQUESTED 상태에서 환불 사유를 변경한다")
        void updateReasonFromRequested() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.updateReason(RefundFixtures.defectiveRefundReason(), now);

            // then
            assertThat(claim.reason()).isEqualTo(RefundFixtures.defectiveRefundReason());
            assertThat(claim.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("COLLECTING 상태에서 사유를 변경하면 예외가 발생한다")
        void updateReasonFromCollecting_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.collectingRefundClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.updateReason(
                                            RefundFixtures.defectiveRefundReason(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 사유를 변경하면 예외가 발생한다")
        void updateReasonFromCompleted_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.completedRefundClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.updateReason(
                                            RefundFixtures.defectiveRefundReason(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("REJECTED 상태에서 사유를 변경하면 예외가 발생한다")
        void updateReasonFromRejected_ThrowsException() {
            // given
            RefundClaim claim = RefundFixtures.rejectedRefundClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.updateReason(
                                            RefundFixtures.defectiveRefundReason(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(RefundException.class);
        }
    }

    @Nested
    @DisplayName("isHold() - 보류 상태 확인")
    class IsHoldTest {

        @Test
        @DisplayName("보류 정보가 없으면 false를 반환한다")
        void isHoldReturnsFalseWhenNoHoldInfo() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // then
            assertThat(claim.isHold()).isFalse();
        }

        @Test
        @DisplayName("보류 정보가 있으면 true를 반환한다")
        void isHoldReturnsTrueWhenHoldInfoPresent() {
            // given
            RefundClaim claim = RefundFixtures.holdRefundClaim();

            // then
            assertThat(claim.isHold()).isTrue();
        }
    }

    @Nested
    @DisplayName("pollEvents() - 도메인 이벤트 수집")
    class PollEventsTest {

        @Test
        @DisplayName("pollEvents 호출 후 이벤트가 비워진다")
        void eventsAreClearedAfterPoll() {
            // given
            RefundClaim claim = RefundFixtures.newRefundClaim();

            // when
            List<DomainEvent> firstPoll = claim.pollEvents();
            List<DomainEvent> secondPoll = claim.pollEvents();

            // then
            assertThat(firstPoll).hasSize(1);
            assertThat(secondPoll).isEmpty();
        }

        @Test
        @DisplayName("pollEvents 결과는 불변 리스트이다")
        void pollEventsReturnsUnmodifiableList() {
            // given
            RefundClaim claim = RefundFixtures.newRefundClaim();

            // when
            List<DomainEvent> events = claim.pollEvents();

            // then
            assertThatThrownBy(() -> events.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("상태 전이 메서드 호출 시 두 개의 이벤트가 등록된다")
        void transitionRegistersStatusChangedEventAlongWithDomainEvent() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when
            claim.startCollecting("admin@marketplace.com", CommonVoFixtures.now());
            List<DomainEvent> events = claim.pollEvents();

            // then
            assertThat(events).hasSize(2);
            long statusChangedCount =
                    events.stream().filter(e -> e instanceof RefundClaimStatusChangedEvent).count();
            assertThat(statusChangedCount).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 문자열 값을 반환한다")
        void idValueReturnsStringValue() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // then
            assertThat(claim.idValue()).isEqualTo(RefundFixtures.defaultRefundClaimId().value());
        }

        @Test
        @DisplayName("claimNumberValue()는 클레임 번호의 문자열 값을 반환한다")
        void claimNumberValueReturnsStringValue() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // then
            assertThat(claim.claimNumberValue())
                    .isEqualTo(RefundFixtures.defaultRefundClaimNumber().value());
        }

        @Test
        @DisplayName("orderItemIdValue()는 주문상품 ID의 문자열 값을 반환한다")
        void orderItemIdValueReturnsStringValue() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // then
            assertThat(claim.orderItemIdValue())
                    .isEqualTo(RefundFixtures.defaultOrderItemId().value());
        }

        @Test
        @DisplayName("sellerId()는 판매자 ID를 반환한다")
        void sellerIdReturnsValue() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // then
            assertThat(claim.sellerId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("refundQty()는 환불 수량을 반환한다")
        void refundQtyReturnsValue() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // then
            assertThat(claim.refundQty()).isEqualTo(1);
        }

        @Test
        @DisplayName("nullable 필드가 null일 때 안전하게 반환한다")
        void nullableFieldsSafeReturn() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // then
            assertThat(claim.refundInfo()).isNull();
            assertThat(claim.holdInfo()).isNull();
            assertThat(claim.collectShipment()).isNull();
            assertThat(claim.processedBy()).isNull();
            assertThat(claim.processedAt()).isNull();
            assertThat(claim.completedAt()).isNull();
        }
    }
}
