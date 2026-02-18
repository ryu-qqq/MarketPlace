package com.ryuqq.marketplace.domain.cancel.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.event.CancelApprovedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelCompletedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelCreatedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelRejectedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelStatusChangedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelWithdrawnEvent;
import com.ryuqq.marketplace.domain.cancel.exception.CancelException;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Cancel Aggregate 단위 테스트")
class CancelTest {

    @Nested
    @DisplayName("forBuyerCancel() - 구매자 취소 생성")
    class ForBuyerCancelTest {

        @Test
        @DisplayName("구매자 취소를 생성하면 REQUESTED 상태이다")
        void createBuyerCancelWithRequestedStatus() {
            // when
            Cancel cancel = CancelFixtures.newBuyerCancel();

            // then
            assertThat(cancel.status()).isEqualTo(CancelStatus.REQUESTED);
            assertThat(cancel.type()).isEqualTo(CancelType.BUYER_CANCEL);
            assertThat(cancel.processedBy()).isNull();
            assertThat(cancel.processedAt()).isNull();
            assertThat(cancel.completedAt()).isNull();
            assertThat(cancel.refundInfo()).isNull();
        }

        @Test
        @DisplayName("구매자 취소 생성 시 CancelCreatedEvent가 등록된다")
        void buyerCancelCreatedEventRegistered() {
            // when
            Cancel cancel = CancelFixtures.newBuyerCancel();

            // then
            List<DomainEvent> events = cancel.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(CancelCreatedEvent.class);

            CancelCreatedEvent event = (CancelCreatedEvent) events.get(0);
            assertThat(event.cancelId()).isEqualTo(cancel.id());
            assertThat(event.orderId()).isEqualTo(cancel.orderId());
            assertThat(event.cancelType()).isEqualTo(CancelType.BUYER_CANCEL);
        }

        @Test
        @DisplayName("구매자 취소 생성 시 기본 필드가 올바르게 설정된다")
        void buyerCancelFieldsSetCorrectly() {
            // given
            CancelId id = CancelFixtures.defaultCancelId();
            CancelNumber number = CancelFixtures.defaultCancelNumber();
            Instant now = CommonVoFixtures.now();

            // when
            Cancel cancel =
                    Cancel.forBuyerCancel(
                            id,
                            number,
                            "ORD-20240101-0001",
                            CancelFixtures.defaultCancelItems(),
                            CancelFixtures.defaultCancelReason(),
                            "buyer@marketplace.com",
                            now);

            // then
            assertThat(cancel.id()).isEqualTo(id);
            assertThat(cancel.cancelNumber()).isEqualTo(number);
            assertThat(cancel.orderId()).isEqualTo("ORD-20240101-0001");
            assertThat(cancel.requestedBy()).isEqualTo("buyer@marketplace.com");
            assertThat(cancel.requestedAt()).isEqualTo(now);
            assertThat(cancel.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("취소 항목이 비어있으면 예외가 발생한다")
        void createBuyerCancelWithEmptyItems_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    Cancel.forBuyerCancel(
                                            CancelFixtures.defaultCancelId(),
                                            CancelFixtures.defaultCancelNumber(),
                                            "ORD-20240101-0001",
                                            List.of(),
                                            CancelFixtures.defaultCancelReason(),
                                            "buyer@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(CancelException.class)
                    .hasMessageContaining("최소 1개");
        }

        @Test
        @DisplayName("취소 항목이 null이면 예외가 발생한다")
        void createBuyerCancelWithNullItems_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    Cancel.forBuyerCancel(
                                            CancelFixtures.defaultCancelId(),
                                            CancelFixtures.defaultCancelNumber(),
                                            "ORD-20240101-0001",
                                            null,
                                            CancelFixtures.defaultCancelReason(),
                                            "buyer@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(CancelException.class);
        }
    }

    @Nested
    @DisplayName("forSellerCancel() - 판매자 취소 생성")
    class ForSellerCancelTest {

        @Test
        @DisplayName("판매자 취소를 생성하면 APPROVED 상태이다")
        void createSellerCancelWithApprovedStatus() {
            // when
            Cancel cancel = CancelFixtures.newSellerCancel();

            // then
            assertThat(cancel.status()).isEqualTo(CancelStatus.APPROVED);
            assertThat(cancel.type()).isEqualTo(CancelType.SELLER_CANCEL);
            assertThat(cancel.processedBy()).isNotNull();
            assertThat(cancel.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("판매자 취소 생성 시 CancelCreatedEvent와 CancelStatusChangedEvent가 등록된다")
        void sellerCancelCreatesMultipleEvents() {
            // when
            Cancel cancel = CancelFixtures.newSellerCancel();

            // then
            List<DomainEvent> events = cancel.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(CancelCreatedEvent.class);
            assertThat(events.get(1)).isInstanceOf(CancelStatusChangedEvent.class);

            CancelCreatedEvent createdEvent = (CancelCreatedEvent) events.get(0);
            assertThat(createdEvent.cancelType()).isEqualTo(CancelType.SELLER_CANCEL);

            CancelStatusChangedEvent statusEvent = (CancelStatusChangedEvent) events.get(1);
            assertThat(statusEvent.fromStatus()).isEqualTo(CancelStatus.REQUESTED);
            assertThat(statusEvent.toStatus()).isEqualTo(CancelStatus.APPROVED);
        }

        @Test
        @DisplayName("판매자 취소 생성 시 requestedBy와 processedBy가 동일하다")
        void sellerCancelRequestedByEqualsProcessedBy() {
            // given
            String seller = "seller@marketplace.com";
            Instant now = CommonVoFixtures.now();

            // when
            Cancel cancel =
                    Cancel.forSellerCancel(
                            CancelFixtures.defaultCancelId(),
                            CancelFixtures.defaultCancelNumber(),
                            "ORD-20240101-0001",
                            CancelFixtures.defaultCancelItems(),
                            CancelFixtures.cancelReason(CancelReasonType.OUT_OF_STOCK),
                            seller,
                            now);

            // then
            assertThat(cancel.requestedBy()).isEqualTo(seller);
            assertThat(cancel.processedBy()).isEqualTo(seller);
            assertThat(cancel.requestedAt()).isEqualTo(now);
            assertThat(cancel.processedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("approve() - 취소 승인")
    class ApproveTest {

        @Test
        @DisplayName("REQUESTED 상태의 취소를 승인한다")
        void approveRequestedCancel() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            String processedBy = "admin@marketplace.com";
            Instant now = CommonVoFixtures.now();

            // when
            cancel.approve(processedBy, now);

            // then
            assertThat(cancel.status()).isEqualTo(CancelStatus.APPROVED);
            assertThat(cancel.processedBy()).isEqualTo(processedBy);
            assertThat(cancel.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("승인 시 CancelApprovedEvent와 CancelStatusChangedEvent가 등록된다")
        void approveEventRegistered() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            Instant now = CommonVoFixtures.now();

            // when
            cancel.approve("admin@marketplace.com", now);

            // then
            List<DomainEvent> events = cancel.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(CancelApprovedEvent.class);
            assertThat(events.get(1)).isInstanceOf(CancelStatusChangedEvent.class);

            CancelApprovedEvent approvedEvent = (CancelApprovedEvent) events.get(0);
            assertThat(approvedEvent.cancelId()).isEqualTo(cancel.id());
            assertThat(approvedEvent.orderId()).isEqualTo(cancel.orderId());

            CancelStatusChangedEvent statusEvent = (CancelStatusChangedEvent) events.get(1);
            assertThat(statusEvent.fromStatus()).isEqualTo(CancelStatus.REQUESTED);
            assertThat(statusEvent.toStatus()).isEqualTo(CancelStatus.APPROVED);
        }

        @Test
        @DisplayName("APPROVED 상태에서 다시 승인하면 예외가 발생한다")
        void approveAlreadyApprovedCancel_ThrowsException() {
            // given
            Cancel cancel = CancelFixtures.approvedCancel();

            // when & then
            assertThatThrownBy(
                            () -> cancel.approve("admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(CancelException.class)
                    .hasMessageContaining("변경할 수 없습니다");
        }

        @Test
        @DisplayName("COMPLETED 상태에서 승인하면 예외가 발생한다")
        void approveCompletedCancel_ThrowsException() {
            // given
            Cancel cancel = CancelFixtures.completedCancel();

            // when & then
            assertThatThrownBy(
                            () -> cancel.approve("admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(CancelException.class);
        }
    }

    @Nested
    @DisplayName("reject() - 취소 거절")
    class RejectTest {

        @Test
        @DisplayName("REQUESTED 상태의 취소를 거절한다")
        void rejectRequestedCancel() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            String processedBy = "admin@marketplace.com";
            Instant now = CommonVoFixtures.now();

            // when
            cancel.reject(processedBy, now);

            // then
            assertThat(cancel.status()).isEqualTo(CancelStatus.REJECTED);
            assertThat(cancel.processedBy()).isEqualTo(processedBy);
            assertThat(cancel.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("거절 시 CancelRejectedEvent와 CancelStatusChangedEvent가 등록된다")
        void rejectEventRegistered() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            Instant now = CommonVoFixtures.now();

            // when
            cancel.reject("admin@marketplace.com", now);

            // then
            List<DomainEvent> events = cancel.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(CancelRejectedEvent.class);
            assertThat(events.get(1)).isInstanceOf(CancelStatusChangedEvent.class);

            CancelStatusChangedEvent statusEvent = (CancelStatusChangedEvent) events.get(1);
            assertThat(statusEvent.fromStatus()).isEqualTo(CancelStatus.REQUESTED);
            assertThat(statusEvent.toStatus()).isEqualTo(CancelStatus.REJECTED);
        }

        @Test
        @DisplayName("APPROVED 상태에서 거절하면 예외가 발생한다")
        void rejectApprovedCancel_ThrowsException() {
            // given
            Cancel cancel = CancelFixtures.approvedCancel();

            // when & then
            assertThatThrownBy(() -> cancel.reject("admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(CancelException.class);
        }
    }

    @Nested
    @DisplayName("complete() - 취소 완료 (환불 처리)")
    class CompleteTest {

        @Test
        @DisplayName("APPROVED 상태의 취소를 완료 처리한다")
        void completeApprovedCancel() {
            // given
            Cancel cancel = CancelFixtures.approvedCancel();
            String processedBy = "admin@marketplace.com";
            Instant now = CommonVoFixtures.now();

            // when
            cancel.complete(CancelFixtures.defaultCancelRefundInfo(), processedBy, now);

            // then
            assertThat(cancel.status()).isEqualTo(CancelStatus.COMPLETED);
            assertThat(cancel.refundInfo()).isNotNull();
            assertThat(cancel.completedAt()).isEqualTo(now);
            assertThat(cancel.processedBy()).isEqualTo(processedBy);
        }

        @Test
        @DisplayName("완료 시 CancelCompletedEvent와 CancelStatusChangedEvent가 등록된다")
        void completeEventRegistered() {
            // given
            Cancel cancel = CancelFixtures.approvedCancel();
            Instant now = CommonVoFixtures.now();

            // when
            cancel.complete(CancelFixtures.defaultCancelRefundInfo(), "admin@marketplace.com", now);

            // then
            List<DomainEvent> events = cancel.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(CancelCompletedEvent.class);
            assertThat(events.get(1)).isInstanceOf(CancelStatusChangedEvent.class);

            CancelStatusChangedEvent statusEvent = (CancelStatusChangedEvent) events.get(1);
            assertThat(statusEvent.fromStatus()).isEqualTo(CancelStatus.APPROVED);
            assertThat(statusEvent.toStatus()).isEqualTo(CancelStatus.COMPLETED);
        }

        @Test
        @DisplayName("REQUESTED 상태에서 완료 처리하면 예외가 발생한다")
        void completeRequestedCancel_ThrowsException() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();

            // when & then
            assertThatThrownBy(
                            () ->
                                    cancel.complete(
                                            CancelFixtures.defaultCancelRefundInfo(),
                                            "admin@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(CancelException.class);
        }
    }

    @Nested
    @DisplayName("withdraw() - 취소 철회")
    class WithdrawTest {

        @Test
        @DisplayName("REQUESTED 상태의 취소를 철회한다")
        void withdrawRequestedCancel() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            Instant now = CommonVoFixtures.now();

            // when
            cancel.withdraw(now);

            // then
            assertThat(cancel.status()).isEqualTo(CancelStatus.CANCELLED);
            assertThat(cancel.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("철회 시 CancelWithdrawnEvent와 CancelStatusChangedEvent가 등록된다")
        void withdrawEventRegistered() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            Instant now = CommonVoFixtures.now();

            // when
            cancel.withdraw(now);

            // then
            List<DomainEvent> events = cancel.pollEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(CancelWithdrawnEvent.class);
            assertThat(events.get(1)).isInstanceOf(CancelStatusChangedEvent.class);

            CancelWithdrawnEvent withdrawnEvent = (CancelWithdrawnEvent) events.get(0);
            assertThat(withdrawnEvent.cancelId()).isEqualTo(cancel.id());

            CancelStatusChangedEvent statusEvent = (CancelStatusChangedEvent) events.get(1);
            assertThat(statusEvent.fromStatus()).isEqualTo(CancelStatus.REQUESTED);
            assertThat(statusEvent.toStatus()).isEqualTo(CancelStatus.CANCELLED);
        }

        @Test
        @DisplayName("APPROVED 상태에서 철회하면 예외가 발생한다")
        void withdrawApprovedCancel_ThrowsException() {
            // given
            Cancel cancel = CancelFixtures.approvedCancel();

            // when & then
            assertThatThrownBy(() -> cancel.withdraw(CommonVoFixtures.now()))
                    .isInstanceOf(CancelException.class);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 철회하면 예외가 발생한다")
        void withdrawCompletedCancel_ThrowsException() {
            // given
            Cancel cancel = CancelFixtures.completedCancel();

            // when & then
            assertThatThrownBy(() -> cancel.withdraw(CommonVoFixtures.now()))
                    .isInstanceOf(CancelException.class);
        }
    }

    @Nested
    @DisplayName("pollEvents() - 이벤트 수집")
    class PollEventsTest {

        @Test
        @DisplayName("pollEvents 호출 후 이벤트가 비워진다")
        void eventsAreClearedAfterPoll() {
            // given
            Cancel cancel = CancelFixtures.newBuyerCancel();

            // when
            List<DomainEvent> firstPoll = cancel.pollEvents();
            List<DomainEvent> secondPoll = cancel.pollEvents();

            // then
            assertThat(firstPoll).hasSize(1);
            assertThat(secondPoll).isEmpty();
        }

        @Test
        @DisplayName("pollEvents 결과는 불변 리스트이다")
        void pollEventsReturnsUnmodifiableList() {
            // given
            Cancel cancel = CancelFixtures.newBuyerCancel();

            // when
            List<DomainEvent> events = cancel.pollEvents();

            // then
            assertThatThrownBy(() -> events.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("REQUESTED 상태로 재구성한다")
        void reconstituteRequestedCancel() {
            // when
            Cancel cancel = CancelFixtures.requestedCancel();

            // then
            assertThat(cancel.status()).isEqualTo(CancelStatus.REQUESTED);
            assertThat(cancel.refundInfo()).isNull();
            assertThat(cancel.processedBy()).isNull();
        }

        @Test
        @DisplayName("APPROVED 상태로 재구성한다")
        void reconstituteApprovedCancel() {
            // when
            Cancel cancel = CancelFixtures.approvedCancel();

            // then
            assertThat(cancel.status()).isEqualTo(CancelStatus.APPROVED);
            assertThat(cancel.processedBy()).isNotNull();
            assertThat(cancel.processedAt()).isNotNull();
            assertThat(cancel.completedAt()).isNull();
        }

        @Test
        @DisplayName("COMPLETED 상태로 재구성한다")
        void reconstituteCompletedCancel() {
            // when
            Cancel cancel = CancelFixtures.completedCancel();

            // then
            assertThat(cancel.status()).isEqualTo(CancelStatus.COMPLETED);
            assertThat(cancel.refundInfo()).isNotNull();
            assertThat(cancel.completedAt()).isNotNull();
        }

        @Test
        @DisplayName("재구성 시 이벤트가 발행되지 않는다")
        void reconstituteDoesNotRegisterEvents() {
            // when
            Cancel cancel = CancelFixtures.approvedCancel();

            // then
            assertThat(cancel.pollEvents()).isEmpty();
        }

        @Test
        @DisplayName("취소 항목이 함께 재구성된다")
        void reconstituteWithCancelItems() {
            // when
            Cancel cancel = CancelFixtures.requestedCancel();

            // then
            assertThat(cancel.cancelItems()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("cancelItems() - 취소 항목 불변성")
    class CancelItemsTest {

        @Test
        @DisplayName("cancelItems()는 불변 리스트를 반환한다")
        void cancelItemsReturnsUnmodifiableList() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();

            // when
            List<CancelItem> items = cancel.cancelItems();

            // then
            assertThatThrownBy(() -> items.add(CancelFixtures.defaultCancelItem()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
