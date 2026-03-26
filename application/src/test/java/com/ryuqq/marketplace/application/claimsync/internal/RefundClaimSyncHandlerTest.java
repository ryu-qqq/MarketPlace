package com.ryuqq.marketplace.application.claimsync.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.claim.manager.ClaimShipmentCommandManager;
import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.claimsync.ClaimSyncFixtures;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.refund.internal.RefundSettlementProcessor;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import com.ryuqq.marketplace.domain.claimsync.vo.InternalClaimType;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
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
@DisplayName("RefundClaimSyncHandler 단위 테스트")
class RefundClaimSyncHandlerTest {

    @InjectMocks private RefundClaimSyncHandler sut;

    @Mock private RefundReadManager refundReadManager;
    @Mock private RefundCommandManager refundCommandManager;
    @Mock private OrderItemReadManager orderItemReadManager;
    @Mock private OrderItemCommandManager orderItemCommandManager;
    @Mock private ClaimHistoryFactory historyFactory;
    @Mock private ClaimHistoryCommandManager historyCommandManager;
    @Mock private ClaimShipmentCommandManager claimShipmentCommandManager;
    @Mock private TimeProvider timeProvider;
    @Mock private RefundSettlementProcessor refundSettlementProcessor;

    @Nested
    @DisplayName("supportedType() 메서드 테스트")
    class SupportedTypeTest {

        @Test
        @DisplayName("REFUND 유형을 반환한다")
        void supportedType_ReturnsRefund() {
            assertThat(sut.supportedType()).isEqualTo(InternalClaimType.REFUND);
        }
    }

    @Nested
    @DisplayName("resolve() - RETURN 클레임 액션 결정")
    class ResolveReturnTest {

        @Test
        @DisplayName("RETURN_REQUEST 이고 기존 환불 없으면 REFUND_CREATED를 반환한다")
        void resolve_ReturnRequest_NoExisting_ReturnsRefundCreated() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_CREATED);
        }

        @Test
        @DisplayName("RETURN_REQUEST 이고 기존 환불이 있으면 SKIPPED를 반환한다")
        void resolve_ReturnRequest_ExistingRefund_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            RefundClaim existing = RefundFixtures.requestedRefundClaim();
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("COLLECTING 이고 기존 환불 없으면 REFUND_CREATED를 반환한다")
        void resolve_Collecting_NoExisting_ReturnsRefundCreated() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("COLLECTING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_CREATED);
        }

        @Test
        @DisplayName("COLLECTING 이고 기존 환불 상태가 REQUESTED이면 REFUND_COLLECTING을 반환한다")
        void resolve_Collecting_RequestedRefund_ReturnsRefundCollecting() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("COLLECTING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            RefundClaim existing = RefundFixtures.requestedRefundClaim();
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_COLLECTING);
        }

        @Test
        @DisplayName("COLLECTING 이고 기존 환불 상태가 REQUESTED가 아니면 SKIPPED를 반환한다")
        void resolve_Collecting_NonRequestedRefund_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("COLLECTING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            RefundClaim existing = RefundFixtures.collectingRefundClaim();
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("COLLECT_DONE 이고 기존 환불 없으면 REFUND_CREATED를 반환한다")
        void resolve_CollectDone_NoExisting_ReturnsRefundCreated() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("COLLECT_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_CREATED);
        }

        @Test
        @DisplayName("COLLECT_DONE 이고 기존 환불 상태가 COLLECTING이면 REFUND_COLLECTED를 반환한다")
        void resolve_CollectDone_CollectingRefund_ReturnsRefundCollected() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("COLLECT_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            RefundClaim existing = RefundFixtures.collectingRefundClaim();
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_COLLECTED);
        }

        @Test
        @DisplayName("RETURN_DONE 이고 기존 환불 없으면 REFUND_COMPLETED를 반환한다")
        void resolve_ReturnDone_NoExisting_ReturnsRefundCompleted() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_COMPLETED);
        }

        @Test
        @DisplayName("RETURN_DONE 이고 기존 환불 상태가 COLLECTED이면 REFUND_COMPLETED를 반환한다")
        void resolve_ReturnDone_CollectedRefund_ReturnsRefundCompleted() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            RefundClaim existing = RefundFixtures.collectedRefundClaim();
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_COMPLETED);
        }

        @Test
        @DisplayName("RETURN_DONE 이고 기존 환불 상태가 COLLECTED가 아니면 SKIPPED를 반환한다")
        void resolve_ReturnDone_NonCollectedRefund_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            RefundClaim existing = RefundFixtures.requestedRefundClaim();
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("RETURN_REJECT 이고 기존 환불 없으면 SKIPPED를 반환한다")
        void resolve_ReturnReject_NoExisting_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("RETURN_REJECT 이고 기존 환불이 있으면 REFUND_REJECTED를 반환한다")
        void resolve_ReturnReject_ExistingRefund_ReturnsRefundRejected() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            RefundClaim existing = RefundFixtures.requestedRefundClaim();
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_REJECTED);
        }

        @Test
        @DisplayName("알 수 없는 claimStatus이면 SKIPPED를 반환한다")
        void resolve_UnknownStatus_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("UNKNOWN_STATUS");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }
    }

    @Nested
    @DisplayName("execute() - REFUND_CREATED 액션 실행")
    class ExecuteRefundCreatedTest {

        @Test
        @DisplayName("REFUND_CREATED 실행 시 환불을 생성하고 OrderItem을 반품요청 처리한다")
        void execute_RefundCreated_CreatesRefundAndRequestsReturn() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            OrderItem confirmedOrderItem = OrderFixtures.confirmedOrderItem();

            given(timeProvider.now()).willReturn(now);
            given(orderItemReadManager.findById(orderItemId))
                    .willReturn(Optional.of(confirmedOrderItem));

            // when
            long result =
                    sut.execute(ClaimSyncAction.REFUND_CREATED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
            then(orderItemCommandManager).should().persistAll(any(List.class));
        }
    }

    @Nested
    @DisplayName("execute() - REFUND_COLLECTING 액션 실행")
    class ExecuteRefundCollectingTest {

        @Test
        @DisplayName("REFUND_COLLECTING 실행 시 수거를 시작하고 0을 반환한다")
        void execute_RefundCollecting_StartsCollecting() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("COLLECTING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            RefundClaim requestedRefund = RefundFixtures.requestedRefundClaim();

            given(timeProvider.now()).willReturn(now);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(requestedRefund));

            // when
            long result =
                    sut.execute(ClaimSyncAction.REFUND_COLLECTING, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
            then(claimShipmentCommandManager).should().persist(any(ClaimShipment.class));
        }

        @Test
        @DisplayName("REFUND_COLLECTING 실행 시 수거 배송 정보가 없으면 ClaimShipment를 생성하지 않는다")
        void execute_RefundCollecting_WithoutShipmentInfo_DoesNotCreateClaimShipment() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.returnPayloadWithReason("CHANGE_OF_MIND");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            RefundClaim requestedRefund = RefundFixtures.requestedRefundClaim();

            given(timeProvider.now()).willReturn(now);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(requestedRefund));

            // when
            long result =
                    sut.execute(ClaimSyncAction.REFUND_COLLECTING, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
            then(claimShipmentCommandManager).should(never()).persist(any());
        }
    }

    @Nested
    @DisplayName("execute() - REFUND_COLLECTED 액션 실행")
    class ExecuteRefundCollectedTest {

        @Test
        @DisplayName("REFUND_COLLECTED 실행 시 수거를 완료하고 0을 반환한다")
        void execute_RefundCollected_CompletesCollection() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("COLLECT_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            RefundClaim collectingRefund = RefundFixtures.collectingRefundClaim();

            given(timeProvider.now()).willReturn(now);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(collectingRefund));

            // when
            long result =
                    sut.execute(ClaimSyncAction.REFUND_COLLECTED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
        }
    }

    @Nested
    @DisplayName("execute() - REFUND_COMPLETED 액션 실행")
    class ExecuteRefundCompletedTest {

        @Test
        @DisplayName("기존 환불이 있으면 완료 처리하고 OrderItem을 반품완료 처리한다")
        void execute_RefundCompleted_ExistingRefund_CompletesAndReturnsOrderItem() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            RefundClaim collectedRefund = RefundFixtures.collectedRefundClaim();
            OrderItem orderItem =
                    OrderFixtures.reconstitutedOrderItem(
                            1L,
                            com.ryuqq.marketplace.domain.order.vo.OrderItemStatus.RETURN_REQUESTED);

            given(timeProvider.now()).willReturn(now);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(collectedRefund));
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.of(orderItem));

            // when
            long result =
                    sut.execute(ClaimSyncAction.REFUND_COMPLETED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
        }

        @Test
        @DisplayName("기존 환불이 없으면 새로 생성하여 완료까지 처리한다")
        void execute_RefundCompleted_NoExisting_CreatesAndCompletes() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.empty());
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.empty());

            // when
            long result =
                    sut.execute(ClaimSyncAction.REFUND_COMPLETED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
        }
    }

    @Nested
    @DisplayName("execute() - REFUND_REJECTED 액션 실행")
    class ExecuteRefundRejectedTest {

        @Test
        @DisplayName("REFUND_REJECTED 실행 시 환불을 거절하고 0을 반환한다")
        void execute_RefundRejected_RejectsRefund() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            RefundClaim requestedRefund = RefundFixtures.requestedRefundClaim();

            given(timeProvider.now()).willReturn(now);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(requestedRefund));

            // when
            long result =
                    sut.execute(ClaimSyncAction.REFUND_REJECTED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
            then(orderItemCommandManager).should(never()).persistAll(any());
        }
    }

    @Nested
    @DisplayName("resolve() - holdbackStatus 기반 HELD/HOLD_RELEASED 액션 결정")
    class ResolveHoldbackTest {

        @Test
        @DisplayName("holdbackStatus가 HOLDBACK이고 기존 환불이 있으면 REFUND_HELD를 반환한다")
        void resolve_HoldbackStatus_ExistingRefund_ReturnsRefundHeld() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.returnHoldbackPayload(
                            "RETURN_REQUEST", "HOLDBACK", "추가 확인 필요");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            RefundClaim existing = RefundFixtures.requestedRefundClaim();
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_HELD);
        }

        @Test
        @DisplayName("holdbackStatus가 HOLDBACK이고 기존 환불이 없으면 claimStatus로 분기한다")
        void resolve_HoldbackStatus_NoExisting_FallsBackToClaimStatus() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.returnHoldbackPayload(
                            "RETURN_REQUEST", "HOLDBACK", "추가 확인 필요");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_CREATED);
        }

        @Test
        @DisplayName("holdbackStatus가 RELEASED이고 기존 환불이 있으면 REFUND_HOLD_RELEASED를 반환한다")
        void resolve_ReleasedStatus_ExistingRefund_ReturnsRefundHoldReleased() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.returnHoldbackPayload("RETURN_REQUEST", "RELEASED", null);
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            RefundClaim existing = RefundFixtures.holdRefundClaim();
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_HOLD_RELEASED);
        }

        @Test
        @DisplayName("holdbackStatus가 null이면 claimStatus로 분기한다")
        void resolve_NullHoldbackStatus_FallsBackToClaimStatus() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayload("RETURN_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.REFUND_CREATED);
        }
    }

    @Nested
    @DisplayName("execute() - REFUND_HELD 액션 실행")
    class ExecuteRefundHeldTest {

        @Test
        @DisplayName("REFUND_HELD 실행 시 환불을 보류 처리하고 0을 반환한다")
        void execute_RefundHeld_HoldsRefund() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.returnHoldbackPayload(
                            "RETURN_REQUEST", "HOLDBACK", "추가 확인 필요");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            RefundClaim requestedRefund = RefundFixtures.requestedRefundClaim();

            given(timeProvider.now()).willReturn(now);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(requestedRefund));

            // when
            long result = sut.execute(ClaimSyncAction.REFUND_HELD, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
        }

        @Test
        @DisplayName("이미 보류 상태인 환불에 REFUND_HELD 실행 시 SKIPPED 처리하고 예외를 던지지 않는다")
        void execute_RefundHeld_AlreadyHeld_ReturnsZeroWithoutException() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.returnHoldbackPayload(
                            "RETURN_REQUEST", "HOLDBACK", "추가 확인 필요");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            RefundClaim holdRefund = RefundFixtures.holdRefundClaim();

            given(timeProvider.now()).willReturn(now);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(holdRefund));

            // when
            long result = sut.execute(ClaimSyncAction.REFUND_HELD, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should(never()).persist(any());
        }
    }

    @Nested
    @DisplayName("execute() - REFUND_HOLD_RELEASED 액션 실행")
    class ExecuteRefundHoldReleasedTest {

        @Test
        @DisplayName("REFUND_HOLD_RELEASED 실행 시 보류를 해제하고 0을 반환한다")
        void execute_RefundHoldReleased_ReleasesHold() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.returnHoldbackPayload("RETURN_REQUEST", "RELEASED", null);
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();
            RefundClaim holdRefund = RefundFixtures.holdRefundClaim();

            given(timeProvider.now()).willReturn(now);
            given(refundReadManager.findByOrderItemId(orderItemId.value()))
                    .willReturn(Optional.of(holdRefund));

            // when
            long result =
                    sut.execute(
                            ClaimSyncAction.REFUND_HOLD_RELEASED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
        }
    }

    @Nested
    @DisplayName("환불 사유 파싱 테스트")
    class ReasonParsingTest {

        @Test
        @DisplayName("알려진 환불 사유는 해당 타입으로 파싱된다")
        void execute_KnownRefundReason_ParsesCorrectly() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayloadWithReason("DEFECTIVE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.empty());

            // when
            long result =
                    sut.execute(ClaimSyncAction.REFUND_CREATED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
        }

        @Test
        @DisplayName("null 환불 사유는 OTHER로 파싱된다")
        void execute_NullRefundReason_ParsesAsOther() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.returnPayloadWithReason(null);
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 10L;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.empty());

            // when
            long result =
                    sut.execute(ClaimSyncAction.REFUND_CREATED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(refundCommandManager).should().persist(any(RefundClaim.class));
        }
    }
}
