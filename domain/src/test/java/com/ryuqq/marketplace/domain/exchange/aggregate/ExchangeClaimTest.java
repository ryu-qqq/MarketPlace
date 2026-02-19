package com.ryuqq.marketplace.domain.exchange.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeCancelledEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeClaimCreatedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeClaimStatusChangedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeCollectedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeCollectingEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeCompletedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangePreparingEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeRejectedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeShippingEvent;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeErrorCode;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeException;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReason;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReasonType;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeTarget;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeClaim Aggregate 단위 테스트")
class ExchangeClaimTest {

    @Nested
    @DisplayName("forNew() - 교환 클레임 신규 생성")
    class ForNewTest {

        @Test
        @DisplayName("유효한 파라미터로 새 교환 클레임을 생성한다")
        void createNewExchangeClaim() {
            // when
            ExchangeClaim claim = ExchangeFixtures.newExchangeClaim();

            // then
            assertThat(claim).isNotNull();
            assertThat(claim.id()).isEqualTo(ExchangeFixtures.defaultExchangeClaimId());
            assertThat(claim.status()).isEqualTo(ExchangeStatus.REQUESTED);
            assertThat(claim.processedBy()).isNull();
            assertThat(claim.processedAt()).isNull();
            assertThat(claim.completedAt()).isNull();
            assertThat(claim.linkedOrderId()).isNull();
        }

        @Test
        @DisplayName("신규 생성 시 교환 아이템 목록이 올바르게 설정된다")
        void newExchangeClaimHasItems() {
            // when
            ExchangeClaim claim = ExchangeFixtures.newExchangeClaim();

            // then
            assertThat(claim.exchangeItems()).hasSize(1);
        }

        @Test
        @DisplayName("신규 생성 시 ExchangeClaimCreatedEvent가 등록된다")
        void createdEventRegistered() {
            // when
            ExchangeClaim claim = ExchangeFixtures.newExchangeClaim();

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(ExchangeClaimCreatedEvent.class);

            ExchangeClaimCreatedEvent event = (ExchangeClaimCreatedEvent) events.get(0);
            assertThat(event.exchangeClaimId()).isEqualTo(claim.id());
        }

        @Test
        @DisplayName("교환 아이템 목록이 비어 있으면 예외가 발생한다")
        void createWithEmptyItems_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExchangeClaim.forNew(
                                            ExchangeFixtures.defaultExchangeClaimId(),
                                            ExchangeFixtures.defaultExchangeClaimNumber(),
                                            "ORDER-001",
                                            List.of(),
                                            ExchangeFixtures.defaultExchangeReason(),
                                            ExchangeFixtures.defaultExchangeTarget(),
                                            ExchangeFixtures.defaultAmountAdjustment(),
                                            ExchangeFixtures.defaultCollectShipment(),
                                            "buyer@example.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }

        @Test
        @DisplayName("교환 아이템 목록이 null이면 예외가 발생한다")
        void createWithNullItems_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExchangeClaim.forNew(
                                            ExchangeFixtures.defaultExchangeClaimId(),
                                            ExchangeFixtures.defaultExchangeClaimNumber(),
                                            "ORDER-001",
                                            null,
                                            ExchangeFixtures.defaultExchangeReason(),
                                            ExchangeFixtures.defaultExchangeTarget(),
                                            ExchangeFixtures.defaultAmountAdjustment(),
                                            ExchangeFixtures.defaultCollectShipment(),
                                            "buyer@example.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("REQUESTED 상태로 재구성한다")
        void reconstituteRequestedClaim() {
            // when
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // then
            assertThat(claim.status()).isEqualTo(ExchangeStatus.REQUESTED);
            assertThat(claim.processedBy()).isNull();
            assertThat(claim.linkedOrderId()).isNull();
        }

        @Test
        @DisplayName("COMPLETED 상태로 재구성한다")
        void reconstituteCompletedClaim() {
            // when
            ExchangeClaim claim = ExchangeFixtures.completedExchangeClaim();

            // then
            assertThat(claim.status()).isEqualTo(ExchangeStatus.COMPLETED);
            assertThat(claim.completedAt()).isNotNull();
            assertThat(claim.linkedOrderId()).isNotNull();
        }

        @Test
        @DisplayName("재구성 시 이벤트가 등록되지 않는다")
        void reconstituteDoesNotRegisterEvents() {
            // when
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // then
            assertThat(claim.pollEvents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("startCollecting() - 수거 시작")
    class StartCollectingTest {

        @Test
        @DisplayName("REQUESTED 상태에서 수거를 시작한다")
        void startCollectingFromRequested() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.startCollecting("admin@marketplace.com", now);

            // then
            assertThat(claim.status()).isEqualTo(ExchangeStatus.COLLECTING);
            assertThat(claim.processedBy()).isEqualTo("admin@marketplace.com");
            assertThat(claim.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("수거 시작 시 ExchangeCollectingEvent와 ExchangeClaimStatusChangedEvent가 등록된다")
        void startCollectingRegistersEvents() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // when
            claim.startCollecting("admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(ExchangeCollectingEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExchangeClaimStatusChangedEvent.class);

            ExchangeClaimStatusChangedEvent changed =
                    (ExchangeClaimStatusChangedEvent) events.get(1);
            assertThat(changed.fromStatus()).isEqualTo(ExchangeStatus.REQUESTED);
            assertThat(changed.toStatus()).isEqualTo(ExchangeStatus.COLLECTING);
        }

        @Test
        @DisplayName("REQUESTED가 아닌 상태에서 수거 시작하면 예외가 발생한다")
        void startCollectingFromInvalidStatus_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectingExchangeClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.startCollecting(
                                            "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("completeCollection() - 수거 완료")
    class CompleteCollectionTest {

        @Test
        @DisplayName("COLLECTING 상태에서 수거를 완료한다")
        void completeCollectionFromCollecting() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectingExchangeClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.completeCollection("admin@marketplace.com", now);

            // then
            assertThat(claim.status()).isEqualTo(ExchangeStatus.COLLECTED);
        }

        @Test
        @DisplayName("수거 완료 시 ExchangeCollectedEvent와 ExchangeClaimStatusChangedEvent가 등록된다")
        void completeCollectionRegistersEvents() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectingExchangeClaim();

            // when
            claim.completeCollection("admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(ExchangeCollectedEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExchangeClaimStatusChangedEvent.class);

            ExchangeClaimStatusChangedEvent changed =
                    (ExchangeClaimStatusChangedEvent) events.get(1);
            assertThat(changed.fromStatus()).isEqualTo(ExchangeStatus.COLLECTING);
            assertThat(changed.toStatus()).isEqualTo(ExchangeStatus.COLLECTED);
        }

        @Test
        @DisplayName("COLLECTING이 아닌 상태에서 수거 완료하면 예외가 발생한다")
        void completeCollectionFromInvalidStatus_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.completeCollection(
                                            "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("startPreparing() - 교환 상품 준비 시작")
    class StartPreparingTest {

        @Test
        @DisplayName("COLLECTED 상태에서 준비를 시작한다")
        void startPreparingFromCollected() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectedExchangeClaim();

            // when
            claim.startPreparing("admin@marketplace.com", CommonVoFixtures.now());

            // then
            assertThat(claim.status()).isEqualTo(ExchangeStatus.PREPARING);
        }

        @Test
        @DisplayName("준비 시작 시 ExchangePreparingEvent와 ExchangeClaimStatusChangedEvent가 등록된다")
        void startPreparingRegistersEvents() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectedExchangeClaim();

            // when
            claim.startPreparing("admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(ExchangePreparingEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExchangeClaimStatusChangedEvent.class);
        }

        @Test
        @DisplayName("COLLECTED가 아닌 상태에서 준비 시작하면 예외가 발생한다")
        void startPreparingFromInvalidStatus_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectingExchangeClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.startPreparing(
                                            "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("startShipping() - 교환 배송 시작")
    class StartShippingTest {

        @Test
        @DisplayName("PREPARING 상태에서 배송을 시작한다")
        void startShippingFromPreparing() {
            // given
            ExchangeClaim claim = ExchangeFixtures.preparingExchangeClaim();
            String linkedOrderId = "ORDER-20260218-9999";
            Instant now = CommonVoFixtures.now();

            // when
            claim.startShipping(linkedOrderId, "admin@marketplace.com", now);

            // then
            assertThat(claim.status()).isEqualTo(ExchangeStatus.SHIPPING);
            assertThat(claim.linkedOrderId()).isEqualTo(linkedOrderId);
        }

        @Test
        @DisplayName("배송 시작 시 ExchangeShippingEvent와 ExchangeClaimStatusChangedEvent가 등록된다")
        void startShippingRegistersEvents() {
            // given
            ExchangeClaim claim = ExchangeFixtures.preparingExchangeClaim();

            // when
            claim.startShipping(
                    "ORDER-20260218-9999", "admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(ExchangeShippingEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExchangeClaimStatusChangedEvent.class);
        }

        @Test
        @DisplayName("PREPARING이 아닌 상태에서 배송 시작하면 예외가 발생한다")
        void startShippingFromInvalidStatus_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectedExchangeClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.startShipping(
                                            "ORDER-999",
                                            "admin@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("complete() - 교환 완료")
    class CompleteTest {

        @Test
        @DisplayName("SHIPPING 상태에서 교환을 완료한다")
        void completeFromShipping() {
            // given
            ExchangeClaim claim = ExchangeFixtures.shippingExchangeClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.complete("admin@marketplace.com", now);

            // then
            assertThat(claim.status()).isEqualTo(ExchangeStatus.COMPLETED);
            assertThat(claim.completedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("완료 시 ExchangeCompletedEvent와 ExchangeClaimStatusChangedEvent가 등록된다")
        void completeRegistersEvents() {
            // given
            ExchangeClaim claim = ExchangeFixtures.shippingExchangeClaim();

            // when
            claim.complete("admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(ExchangeCompletedEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExchangeClaimStatusChangedEvent.class);

            ExchangeClaimStatusChangedEvent changed =
                    (ExchangeClaimStatusChangedEvent) events.get(1);
            assertThat(changed.fromStatus()).isEqualTo(ExchangeStatus.SHIPPING);
            assertThat(changed.toStatus()).isEqualTo(ExchangeStatus.COMPLETED);
        }

        @Test
        @DisplayName("SHIPPING이 아닌 상태에서 완료하면 예외가 발생한다")
        void completeFromInvalidStatus_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.preparingExchangeClaim();

            // when & then
            assertThatThrownBy(
                            () -> claim.complete("admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("reject() - 교환 거절")
    class RejectTest {

        @Test
        @DisplayName("REQUESTED 상태에서 교환을 거절한다")
        void rejectFromRequested() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            Instant now = CommonVoFixtures.now();

            // when
            claim.reject("admin@marketplace.com", now);

            // then
            assertThat(claim.status()).isEqualTo(ExchangeStatus.REJECTED);
            assertThat(claim.processedBy()).isEqualTo("admin@marketplace.com");
            assertThat(claim.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("COLLECTED 상태에서도 교환을 거절할 수 있다")
        void rejectFromCollected() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectedExchangeClaim();

            // when
            assertThatCode(() -> claim.reject("admin@marketplace.com", CommonVoFixtures.now()))
                    .doesNotThrowAnyException();
            assertThat(claim.status()).isEqualTo(ExchangeStatus.REJECTED);
        }

        @Test
        @DisplayName("PREPARING 상태에서도 교환을 거절할 수 있다")
        void rejectFromPreparing() {
            // given
            ExchangeClaim claim = ExchangeFixtures.preparingExchangeClaim();

            // when
            assertThatCode(() -> claim.reject("admin@marketplace.com", CommonVoFixtures.now()))
                    .doesNotThrowAnyException();
            assertThat(claim.status()).isEqualTo(ExchangeStatus.REJECTED);
        }

        @Test
        @DisplayName("거절 시 ExchangeRejectedEvent와 ExchangeClaimStatusChangedEvent가 등록된다")
        void rejectRegistersEvents() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // when
            claim.reject("admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(ExchangeRejectedEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExchangeClaimStatusChangedEvent.class);
        }

        @Test
        @DisplayName("COLLECTING 상태에서 거절하면 예외가 발생한다")
        void rejectFromCollecting_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectingExchangeClaim();

            // when & then
            assertThatThrownBy(() -> claim.reject("admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 거절하면 예외가 발생한다")
        void rejectFromCompleted_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.completedExchangeClaim();

            // when & then
            assertThatThrownBy(() -> claim.reject("admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("cancel() - 교환 취소")
    class CancelTest {

        @Test
        @DisplayName("REQUESTED 상태에서 교환을 취소한다")
        void cancelFromRequested() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // when
            claim.cancel(CommonVoFixtures.now());

            // then
            assertThat(claim.status()).isEqualTo(ExchangeStatus.CANCELLED);
        }

        @Test
        @DisplayName("COLLECTING 상태에서도 교환을 취소할 수 있다")
        void cancelFromCollecting() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectingExchangeClaim();

            // when
            assertThatCode(() -> claim.cancel(CommonVoFixtures.now())).doesNotThrowAnyException();
            assertThat(claim.status()).isEqualTo(ExchangeStatus.CANCELLED);
        }

        @Test
        @DisplayName("취소 시 ExchangeCancelledEvent와 ExchangeClaimStatusChangedEvent가 등록된다")
        void cancelRegistersEvents() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // when
            claim.cancel(CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(ExchangeCancelledEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExchangeClaimStatusChangedEvent.class);

            ExchangeClaimStatusChangedEvent changed =
                    (ExchangeClaimStatusChangedEvent) events.get(1);
            assertThat(changed.fromStatus()).isEqualTo(ExchangeStatus.REQUESTED);
            assertThat(changed.toStatus()).isEqualTo(ExchangeStatus.CANCELLED);
        }

        @Test
        @DisplayName("COLLECTED 상태에서 취소하면 예외가 발생한다")
        void cancelFromCollected_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectedExchangeClaim();

            // when & then
            assertThatThrownBy(() -> claim.cancel(CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 취소하면 예외가 발생한다")
        void cancelFromCompleted_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.completedExchangeClaim();

            // when & then
            assertThatThrownBy(() -> claim.cancel(CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("updateTarget() - 교환 대상 변경")
    class UpdateTargetTest {

        @Test
        @DisplayName("REQUESTED 상태에서 교환 대상을 변경한다")
        void updateTargetFromRequested() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            ExchangeTarget newTarget =
                    ExchangeFixtures.exchangeTarget(1001L, 2002L, "SKU-BLUE-M", 2);
            Instant now = CommonVoFixtures.now();

            // when
            claim.updateTarget(newTarget, ExchangeFixtures.zeroAmountAdjustment(), now);

            // then
            assertThat(claim.exchangeTarget()).isEqualTo(newTarget);
            assertThat(claim.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("REQUESTED가 아닌 상태에서 교환 대상 변경하면 예외가 발생한다")
        void updateTargetFromNonRequested_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectingExchangeClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.updateTarget(
                                            ExchangeFixtures.defaultExchangeTarget(),
                                            ExchangeFixtures.defaultAmountAdjustment(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class)
                    .extracting(e -> ((ExchangeException) e).getErrorCode())
                    .isEqualTo(ExchangeErrorCode.TARGET_UPDATE_NOT_ALLOWED);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 교환 대상 변경하면 예외가 발생한다")
        void updateTargetFromCompleted_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.completedExchangeClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.updateTarget(
                                            ExchangeFixtures.defaultExchangeTarget(),
                                            ExchangeFixtures.defaultAmountAdjustment(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("updateReason() - 교환 사유 변경")
    class UpdateReasonTest {

        @Test
        @DisplayName("REQUESTED 상태에서 교환 사유를 변경한다")
        void updateReasonFromRequested() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            ExchangeReason newReason =
                    ExchangeFixtures.exchangeReason(
                            ExchangeReasonType.DEFECTIVE, "상품 불량으로 교환 요청합니다");
            Instant now = CommonVoFixtures.now();

            // when
            claim.updateReason(newReason, now);

            // then
            assertThat(claim.reason()).isEqualTo(newReason);
            assertThat(claim.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("REQUESTED가 아닌 상태에서 사유 변경하면 예외가 발생한다")
        void updateReasonFromNonRequested_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.collectingExchangeClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.updateReason(
                                            ExchangeFixtures.defaultExchangeReason(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class)
                    .extracting(e -> ((ExchangeException) e).getErrorCode())
                    .isEqualTo(ExchangeErrorCode.REASON_UPDATE_NOT_ALLOWED);
        }

        @Test
        @DisplayName("REJECTED 상태에서 사유 변경하면 예외가 발생한다")
        void updateReasonFromRejected_ThrowsException() {
            // given
            ExchangeClaim claim = ExchangeFixtures.rejectedExchangeClaim();

            // when & then
            assertThatThrownBy(
                            () ->
                                    claim.updateReason(
                                            ExchangeFixtures.defaultExchangeReason(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ExchangeException.class);
        }
    }

    @Nested
    @DisplayName("pollEvents() - 도메인 이벤트 수집")
    class PollEventsTest {

        @Test
        @DisplayName("pollEvents 호출 후 이벤트가 비워진다")
        void eventsAreClearedAfterPoll() {
            // given
            ExchangeClaim claim = ExchangeFixtures.newExchangeClaim();

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
            ExchangeClaim claim = ExchangeFixtures.newExchangeClaim();

            // when
            List<DomainEvent> events = claim.pollEvents();

            // then
            assertThatThrownBy(() -> events.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("상태 전이 후 복수의 이벤트가 등록된다")
        void multipleEventsRegisteredAfterTransition() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // when
            claim.startCollecting("admin@marketplace.com", CommonVoFixtures.now());

            // then
            List<DomainEvent> events = claim.pollEvents();
            assertThat(events).hasSize(2);
        }
    }

    @Nested
    @DisplayName("exchangeItems() - 교환 아이템 목록 조회")
    class ExchangeItemsTest {

        @Test
        @DisplayName("교환 아이템 목록은 불변 리스트이다")
        void exchangeItemsReturnsUnmodifiableList() {
            // given
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            // when
            List<ExchangeItem> items = claim.exchangeItems();

            // then
            assertThatThrownBy(() -> items.add(ExchangeFixtures.defaultExchangeItem()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
