package com.ryuqq.marketplace.application.claimsync.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.cancel.internal.CancelSettlementProcessor;
import com.ryuqq.marketplace.application.cancel.manager.CancelCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.claimsync.ClaimSyncFixtures;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import com.ryuqq.marketplace.domain.claimsync.vo.InternalClaimType;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.id.OrderItemNumber;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CancelClaimSyncHandler 단위 테스트")
class CancelClaimSyncHandlerTest {

    @InjectMocks private CancelClaimSyncHandler sut;

    @Mock private CancelReadManager cancelReadManager;
    @Mock private CancelCommandManager cancelCommandManager;
    @Mock private OrderItemReadManager orderItemReadManager;
    @Mock private OrderItemCommandManager orderItemCommandManager;
    @Mock private ShipmentReadManager shipmentReadManager;
    @Mock private ShipmentCommandManager shipmentCommandManager;
    @Mock private ClaimHistoryFactory historyFactory;
    @Mock private ClaimHistoryCommandManager historyCommandManager;
    @Mock private TimeProvider timeProvider;
    @Mock private CancelSettlementProcessor cancelSettlementProcessor;

    @Nested
    @DisplayName("supportedType() 메서드 테스트")
    class SupportedTypeTest {

        @Test
        @DisplayName("CANCEL 유형을 반환한다")
        void supportedType_ReturnsCancel() {
            assertThat(sut.supportedType()).isEqualTo(InternalClaimType.CANCEL);
        }
    }

    @Nested
    @DisplayName("resolve() - CANCEL 클레임 액션 결정")
    class ResolveCancelTest {

        @Test
        @DisplayName("CANCEL_REQUEST 이고 잔여 취소 가능 수량이 있으면 CANCEL_CREATED를 반환한다")
        void resolve_CancelRequest_NoExisting_ReturnsCancelCreated() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            OrderItem orderItem = OrderFixtures.confirmedOrderItem();
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.of(orderItem));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_CREATED);
        }

        @Test
        @DisplayName("CANCEL_REQUEST 이고 잔여 취소 가능 수량이 없으면 SKIPPED를 반환한다")
        void resolve_CancelRequest_ExistingCancel_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            Cancel existing = CancelFixtures.requestedCancel();
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(existing));
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("CANCELING 이고 기존 취소 없으면 CANCEL_CREATED를 반환한다")
        void resolve_Canceling_NoExisting_ReturnsCancelCreated() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCELING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_CREATED);
        }

        @Test
        @DisplayName("CANCELING 이고 기존 취소 상태가 REQUESTED이면 CANCEL_APPROVED를 반환한다")
        void resolve_Canceling_RequestedCancel_ReturnsCancelApproved() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCELING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            Cancel existing = CancelFixtures.requestedCancel();
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_APPROVED);
        }

        @Test
        @DisplayName("CANCELING 이고 기존 취소 상태가 REQUESTED가 아니면 SKIPPED를 반환한다")
        void resolve_Canceling_NonRequestedCancel_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCELING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            Cancel existing = CancelFixtures.approvedCancel();
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("CANCEL_DONE 이고 기존 취소 없으면 CANCEL_COMPLETED를 반환한다")
        void resolve_CancelDone_NoExisting_ReturnsCancelCompleted() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_COMPLETED);
        }

        @Test
        @DisplayName("CANCEL_DONE 이고 기존 취소 상태가 APPROVED이면 CANCEL_COMPLETED를 반환한다")
        void resolve_CancelDone_ApprovedCancel_ReturnsCancelCompleted() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            Cancel existing = CancelFixtures.approvedCancel();
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_COMPLETED);
        }

        @Test
        @DisplayName("CANCEL_DONE 이고 기존 취소 상태가 COMPLETED이면 SKIPPED를 반환한다")
        void resolve_CancelDone_CompletedCancel_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            Cancel existing = CancelFixtures.completedCancel();
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("CANCEL_REJECT 이고 기존 취소 없으면 SKIPPED를 반환한다")
        void resolve_CancelReject_NoExisting_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("CANCEL_REJECT 이고 기존 취소 상태가 REQUESTED이면 CANCEL_WITHDRAWN을 반환한다")
        void resolve_CancelReject_RequestedCancel_ReturnsCancelWithdrawn() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            Cancel existing = CancelFixtures.requestedCancel();
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_WITHDRAWN);
        }

        @Test
        @DisplayName("알 수 없는 claimStatus이면 SKIPPED를 반환한다")
        void resolve_UnknownStatus_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("UNKNOWN_STATUS");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }
    }

    @Nested
    @DisplayName("resolve() - ADMIN_CANCEL 클레임 액션 결정")
    class ResolveAdminCancelTest {

        @Test
        @DisplayName("ADMIN_CANCELING 이고 기존 취소 없으면 CANCEL_CREATED를 반환한다")
        void resolve_AdminCanceling_NoExisting_ReturnsCancelCreated() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.adminCancelPayload("ADMIN_CANCELING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_CREATED);
        }

        @Test
        @DisplayName("ADMIN_CANCELING 이고 기존 취소가 있으면 SKIPPED를 반환한다")
        void resolve_AdminCanceling_ExistingCancel_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.adminCancelPayload("ADMIN_CANCELING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            Cancel existing = CancelFixtures.requestedCancel();
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("ADMIN_CANCEL_DONE 이고 기존 취소 없으면 CANCEL_COMPLETED를 반환한다")
        void resolve_AdminCancelDone_NoExisting_ReturnsCancelCompleted() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.adminCancelPayload("ADMIN_CANCEL_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_COMPLETED);
        }

        @Test
        @DisplayName("ADMIN_CANCEL_DONE 이고 기존 취소 상태가 APPROVED이면 CANCEL_COMPLETED를 반환한다")
        void resolve_AdminCancelDone_ApprovedCancel_ReturnsCancelCompleted() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.adminCancelPayload("ADMIN_CANCEL_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            Cancel existing = CancelFixtures.approvedCancel();
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_COMPLETED);
        }

        @Test
        @DisplayName("ADMIN_CANCEL_REJECT 이고 기존 취소 없으면 SKIPPED를 반환한다")
        void resolve_AdminCancelReject_NoExisting_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.adminCancelPayload("ADMIN_CANCEL_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("ADMIN_CANCEL_REJECT 이고 기존 취소 상태가 REQUESTED이면 CANCEL_WITHDRAWN을 반환한다")
        void resolve_AdminCancelReject_RequestedCancel_ReturnsCancelWithdrawn() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.adminCancelPayload("ADMIN_CANCEL_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            Cancel existing = CancelFixtures.requestedCancel();
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_WITHDRAWN);
        }
    }

    @Nested
    @DisplayName("execute() - CANCEL_CREATED 액션 실행")
    class ExecuteCancelCreatedTest {

        @Test
        @DisplayName("CANCEL 유형이면 구매자 취소를 생성하고 0을 반환한다")
        void execute_CancelCreated_BuyerCancel_CreatesCancelAndReturnsZero() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            long result =
                    sut.execute(ClaimSyncAction.CANCEL_CREATED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).should().persist(any(Cancel.class));
        }

        @Test
        @DisplayName("ADMIN_CANCEL 유형이면 판매자 취소를 생성하고 OrderItem도 취소 처리한다")
        void execute_CancelCreated_AdminCancel_CreatesSellerCancelAndCancelsOrderItem() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.adminCancelPayload("ADMIN_CANCELING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            OrderItem orderItem = OrderFixtures.confirmedOrderItem();

            given(timeProvider.now()).willReturn(now);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.of(orderItem));

            // when
            long result =
                    sut.execute(ClaimSyncAction.CANCEL_CREATED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).should().persist(any(Cancel.class));
            then(orderItemCommandManager).should().persistAll(any(List.class));
        }
    }

    @Nested
    @DisplayName("execute() - CANCEL_APPROVED 액션 실행")
    class ExecuteCancelApprovedTest {

        @Test
        @DisplayName("CANCEL_APPROVED 실행 시 취소를 승인하고 OrderItem을 취소 처리한다")
        void execute_CancelApproved_ApprovesAndCancelsOrderItem() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCELING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            Cancel requestedCancel = CancelFixtures.requestedCancel();
            OrderItem confirmedOrderItem = OrderFixtures.confirmedOrderItem();

            given(timeProvider.now()).willReturn(now);
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(requestedCancel));
            given(orderItemReadManager.findById(orderItemId))
                    .willReturn(Optional.of(confirmedOrderItem));

            // when
            long result =
                    sut.execute(ClaimSyncAction.CANCEL_APPROVED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).should().persist(any(Cancel.class));
            then(orderItemCommandManager).should().persistAll(any(List.class));
        }
    }

    @Nested
    @DisplayName("execute() - CANCEL_COMPLETED 액션 실행")
    class ExecuteCancelCompletedTest {

        @Test
        @DisplayName("기존 취소가 있으면 완료 처리하고 OrderItem을 취소 처리한다")
        void execute_CancelCompleted_ExistingCancel_CompletesAndCancelsOrderItem() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            Cancel approvedCancel = CancelFixtures.approvedCancel();
            OrderItem confirmedOrderItem = OrderFixtures.confirmedOrderItem();

            given(timeProvider.now()).willReturn(now);
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(approvedCancel));
            given(orderItemReadManager.findById(orderItemId))
                    .willReturn(Optional.of(confirmedOrderItem));

            // when
            long result =
                    sut.execute(ClaimSyncAction.CANCEL_COMPLETED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).should().persist(any(Cancel.class));
        }

        @Test
        @DisplayName("기존 취소가 없으면 새로 생성하여 완료 처리한다")
        void execute_CancelCompleted_NoExisting_CreatesAndCompletes() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            OrderItem confirmedOrderItem = OrderFixtures.confirmedOrderItem();

            given(timeProvider.now()).willReturn(now);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());
            given(orderItemReadManager.findById(orderItemId))
                    .willReturn(Optional.of(confirmedOrderItem));

            // when
            long result =
                    sut.execute(ClaimSyncAction.CANCEL_COMPLETED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).should().persist(any(Cancel.class));
        }
    }

    @Nested
    @DisplayName("execute() - CANCEL_WITHDRAWN 액션 실행")
    class ExecuteCancelWithdrawnTest {

        @Test
        @DisplayName("CANCEL_WITHDRAWN 실행 시 취소를 철회한다")
        void execute_CancelWithdrawn_WithdrawsCancel() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            Cancel requestedCancel = CancelFixtures.requestedCancel();

            given(timeProvider.now()).willReturn(now);
            given(cancelReadManager.findAllByOrderItemId(orderItemId))
                    .willReturn(List.of(requestedCancel));

            // when
            long result =
                    sut.execute(ClaimSyncAction.CANCEL_WITHDRAWN, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).should().persist(any(Cancel.class));
            then(orderItemCommandManager).should(never()).persistAll(any());
        }
    }

    @Nested
    @DisplayName("execute() - SKIPPED 액션 실행")
    class ExecuteSkippedTest {

        @Test
        @DisplayName("SKIPPED 액션은 아무것도 실행하지 않고 0을 반환한다")
        void execute_Skipped_ReturnsZeroWithoutAnyAction() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("UNKNOWN");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;

            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            long result = sut.execute(ClaimSyncAction.SKIPPED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("OrderItem 취소 전환 조건 테스트")
    class OrderItemCancelTransitionTest {

        @Test
        @DisplayName("구매자 취소 생성 시 OrderItem 취소 전환을 시도하지 않는다")
        void cancelOrderItem_BuyerCancelCreated_DoesNotAttemptOrderItemTransition() {
            // given
            // CANCEL(구매자 취소) 유형은 CANCEL_CREATED 액션 시 partialCancelOrderItem을 호출하지 않음
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);
            given(cancelReadManager.findAllByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            sut.execute(ClaimSyncAction.CANCEL_CREATED, payload, orderItemId, sellerId);

            // then
            then(orderItemReadManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("부분취소 시나리오 테스트")
    class PartialCancelScenarioTest {

        private OrderItemId orderItemId() {
            return OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
        }

        /** qty=3, paymentAmount=30000인 OrderItem을 cancelledQty만큼 이미 취소된 상태로 생성 */
        private OrderItem orderItemWithQty3(int cancelledQty) {
            ExternalOrderItemPrice price =
                    ExternalOrderItemPrice.of(
                            Money.of(10000),
                            3,
                            Money.of(30000),
                            Money.zero(),
                            Money.zero(),
                            Money.of(30000));
            return OrderItem.reconstitute(
                    orderItemId(),
                    OrderItemNumber.of("ORD-20240101-0001-001"),
                    OrderFixtures.defaultInternalProductReference(),
                    OrderFixtures.defaultExternalProductSnapshot(),
                    price,
                    OrderFixtures.defaultReceiverInfo(),
                    OrderItemStatus.CONFIRMED,
                    null,
                    cancelledQty,
                    0,
                    List.of());
        }

        @Test
        @DisplayName("resolve - CANCEL_REQUEST 시 잔여 수량이 없으면 SKIPPED를 반환한다")
        void resolve_CancelRequest_NoRemainingQty_ReturnsSkipped() {
            // given — qty=2, cancelledQty=2 → remainingCancelableQty=0
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REQUEST");
            OrderItemId id = orderItemId();
            Cancel completedCancel = CancelFixtures.completedCancel();
            OrderItem fullyUsedItem =
                    OrderItem.reconstitute(
                            id,
                            OrderFixtures.defaultOrderItemNumber(),
                            OrderFixtures.defaultInternalProductReference(),
                            OrderFixtures.defaultExternalProductSnapshot(),
                            OrderFixtures.defaultExternalOrderItemPrice(),
                            OrderFixtures.defaultReceiverInfo(),
                            OrderItemStatus.CANCELLED,
                            null,
                            2,
                            0,
                            List.of());

            given(cancelReadManager.findAllByOrderItemId(id)).willReturn(List.of(completedCancel));
            given(orderItemReadManager.findById(id)).willReturn(Optional.of(fullyUsedItem));

            // when
            ClaimSyncAction action = sut.resolve(payload, id);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("resolve - CANCEL_REQUEST 시 잔여 수량이 있으면 CANCEL_CREATED를 반환한다")
        void resolve_CancelRequest_HasRemainingQty_ReturnsCancelCreated() {
            // given — qty=3, cancelledQty=1 → remainingCancelableQty=2
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REQUEST");
            OrderItemId id = orderItemId();
            Cancel completedCancel = CancelFixtures.completedCancel();
            OrderItem partiallyUsedItem = orderItemWithQty3(1);

            given(cancelReadManager.findAllByOrderItemId(id)).willReturn(List.of(completedCancel));
            given(orderItemReadManager.findById(id)).willReturn(Optional.of(partiallyUsedItem));

            // when
            ClaimSyncAction action = sut.resolve(payload, id);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.CANCEL_CREATED);
        }

        @Test
        @DisplayName("execute - 부분취소 시 OrderItem 상태가 CANCELLED로 전환되지 않고 Shipment 취소도 안 된다")
        void execute_PartialCancel_OrderItemRemainsReady_NoShipmentCancel() {
            // given — cancelQty=1, OrderItem qty=3, cancelledQty=0
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayloadWithQty("CANCELING", 1);
            OrderItemId id = orderItemId();
            long sellerId = 10L;
            Instant now = Instant.now();
            Cancel requestedCancel = CancelFixtures.requestedCancel();
            OrderItem item = orderItemWithQty3(0);

            given(timeProvider.now()).willReturn(now);
            given(cancelReadManager.findAllByOrderItemId(id)).willReturn(List.of(requestedCancel));
            given(orderItemReadManager.findById(id)).willReturn(Optional.of(item));

            // when
            long result = sut.execute(ClaimSyncAction.CANCEL_APPROVED, payload, id, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).should().persist(any(Cancel.class));
            then(orderItemCommandManager).should().persistAll(any(List.class));
            // partialCancel(1)로 cancelledQty=1, isFullyCancelled=false → Shipment 취소 안 됨
            then(shipmentReadManager).should(never()).findByOrderItemId(any());
        }

        @Test
        @DisplayName("execute - 전량취소 시 OrderItem CANCELLED 전환 + Shipment 취소 처리")
        void execute_FullCancel_OrderItemCancelled_ShipmentCancelled() {
            // given — requestedCancel.cancelQty=2, OrderItem qty=2 → 전량취소
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayloadWithQty("CANCELING", 2);
            OrderItemId id = orderItemId();
            long sellerId = 10L;
            Instant now = Instant.now();
            Cancel requestedCancel = CancelFixtures.requestedCancel();
            // confirmedOrderItem: qty=2, cancelledQty=0 → cancelQty(2) 적용 시 isFullyCancelled=true
            OrderItem item = OrderFixtures.confirmedOrderItem();
            Shipment preparingShipment = ShipmentFixtures.preparingShipment();

            given(timeProvider.now()).willReturn(now);
            given(cancelReadManager.findAllByOrderItemId(id)).willReturn(List.of(requestedCancel));
            given(orderItemReadManager.findById(id)).willReturn(Optional.of(item));
            given(shipmentReadManager.findByOrderItemId(id))
                    .willReturn(Optional.of(preparingShipment));

            // when
            long result = sut.execute(ClaimSyncAction.CANCEL_APPROVED, payload, id, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).should().persist(any(Cancel.class));
            then(orderItemCommandManager).should().persistAll(any(List.class));
            then(shipmentCommandManager).should().persist(any(Shipment.class));
        }

        @Test
        @DisplayName("execute - refundAmount가 단가 × cancelQty로 계산된다")
        void execute_RefundAmount_CalculatedByUnitPriceTimesCancelQty() {
            // given — OrderItem qty=3, paymentAmount=30000, cancelQty=2 → refund = (30000/3)*2 =
            // 20000
            ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayloadWithQty("CANCEL_DONE", 2);
            OrderItemId id = orderItemId();
            long sellerId = 10L;
            Instant now = Instant.now();
            Cancel approvedCancel = CancelFixtures.approvedCancel();
            OrderItem item = orderItemWithQty3(0);

            given(timeProvider.now()).willReturn(now);
            // completeCancel은 cancelReadManager를 두 번 호출: execute에서 + completeCancel 내부
            given(cancelReadManager.findAllByOrderItemId(id)).willReturn(List.of(approvedCancel));
            given(orderItemReadManager.findById(id)).willReturn(Optional.of(item));

            // when
            long result = sut.execute(ClaimSyncAction.CANCEL_COMPLETED, payload, id, sellerId);

            // then
            assertThat(result).isZero();
            then(cancelCommandManager).should().persist(any(Cancel.class));
            // refundAmount 검증은 Cancel.complete() 내부에서 CancelRefundInfo가 설정되므로
            // persist에 전달된 Cancel 객체의 refundInfo를 통해 간접 검증됨
        }

        @Test
        @DisplayName("execute - 모든 액션에서 historyCommandManager.persist가 호출된다")
        void execute_AllActions_RecordHistory() {
            OrderItemId id = orderItemId();
            long sellerId = 10L;
            Instant now = Instant.now();

            // CANCEL_CREATED
            {
                ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REQUEST");
                given(timeProvider.now()).willReturn(now);
                given(cancelReadManager.findAllByOrderItemId(id)).willReturn(List.of());
                sut.execute(ClaimSyncAction.CANCEL_CREATED, payload, id, sellerId);
                then(historyCommandManager).should().persist(any());
            }

            // CANCEL_APPROVED
            {
                ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCELING");
                Cancel requestedCancel = CancelFixtures.requestedCancel();
                OrderItem item = orderItemWithQty3(0);
                given(timeProvider.now()).willReturn(now);
                given(cancelReadManager.findAllByOrderItemId(id))
                        .willReturn(List.of(requestedCancel));
                given(orderItemReadManager.findById(id)).willReturn(Optional.of(item));
                sut.execute(ClaimSyncAction.CANCEL_APPROVED, payload, id, sellerId);
            }

            // CANCEL_COMPLETED
            {
                ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_DONE");
                Cancel approvedCancel = CancelFixtures.approvedCancel();
                OrderItem item = orderItemWithQty3(0);
                given(timeProvider.now()).willReturn(now);
                given(cancelReadManager.findAllByOrderItemId(id))
                        .willReturn(List.of(approvedCancel));
                given(orderItemReadManager.findById(id)).willReturn(Optional.of(item));
                sut.execute(ClaimSyncAction.CANCEL_COMPLETED, payload, id, sellerId);
            }

            // CANCEL_WITHDRAWN
            {
                ExternalClaimPayload payload = ClaimSyncFixtures.cancelPayload("CANCEL_REJECT");
                Cancel requestedCancel = CancelFixtures.requestedCancel();
                given(timeProvider.now()).willReturn(now);
                given(cancelReadManager.findAllByOrderItemId(id))
                        .willReturn(List.of(requestedCancel));
                sut.execute(ClaimSyncAction.CANCEL_WITHDRAWN, payload, id, sellerId);
            }

            // 총 4번 호출 검증 (CREATED + APPROVED + COMPLETED + WITHDRAWN)
            then(historyCommandManager).should(org.mockito.Mockito.times(4)).persist(any());
        }
    }
}
