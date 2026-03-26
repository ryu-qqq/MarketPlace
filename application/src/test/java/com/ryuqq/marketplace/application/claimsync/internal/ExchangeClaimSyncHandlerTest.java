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
import com.ryuqq.marketplace.application.exchange.internal.ExchangeSettlementProcessor;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import com.ryuqq.marketplace.domain.claimsync.vo.InternalClaimType;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
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
@DisplayName("ExchangeClaimSyncHandler 단위 테스트")
class ExchangeClaimSyncHandlerTest {

    @InjectMocks private ExchangeClaimSyncHandler sut;

    @Mock private ExchangeReadManager exchangeReadManager;
    @Mock private ExchangeCommandManager exchangeCommandManager;
    @Mock private OrderItemReadManager orderItemReadManager;
    @Mock private OrderItemCommandManager orderItemCommandManager;
    @Mock private ClaimHistoryFactory historyFactory;
    @Mock private ClaimHistoryCommandManager historyCommandManager;
    @Mock private ClaimShipmentCommandManager claimShipmentCommandManager;
    @Mock private TimeProvider timeProvider;
    @Mock private ExchangeSettlementProcessor exchangeSettlementProcessor;

    @Nested
    @DisplayName("supportedType() 메서드 테스트")
    class SupportedTypeTest {

        @Test
        @DisplayName("EXCHANGE 유형을 반환한다")
        void supportedType_ReturnsExchange() {
            assertThat(sut.supportedType()).isEqualTo(InternalClaimType.EXCHANGE);
        }
    }

    @Nested
    @DisplayName("resolve() - EXCHANGE 클레임 액션 결정")
    class ResolveExchangeTest {

        @Test
        @DisplayName("EXCHANGE_REQUEST 이고 기존 교환 없으면 EXCHANGE_CREATED를 반환한다")
        void resolve_ExchangeRequest_NoExisting_ReturnsExchangeCreated() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(exchangeReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_CREATED);
        }

        @Test
        @DisplayName("EXCHANGE_REQUEST 이고 기존 교환이 있으면 SKIPPED를 반환한다")
        void resolve_ExchangeRequest_ExistingExchange_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            ExchangeClaim existing = ExchangeFixtures.requestedExchangeClaim();
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("COLLECTING 이고 기존 교환 없으면 EXCHANGE_CREATED를 반환한다")
        void resolve_Collecting_NoExisting_ReturnsExchangeCreated() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("COLLECTING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(exchangeReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_CREATED);
        }

        @Test
        @DisplayName("COLLECTING 이고 기존 교환 상태가 REQUESTED이면 EXCHANGE_COLLECTING을 반환한다")
        void resolve_Collecting_RequestedExchange_ReturnsExchangeCollecting() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("COLLECTING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            ExchangeClaim existing = ExchangeFixtures.requestedExchangeClaim();
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_COLLECTING);
        }

        @Test
        @DisplayName("COLLECTING 이고 기존 교환 상태가 REQUESTED가 아니면 SKIPPED를 반환한다")
        void resolve_Collecting_NonRequestedExchange_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("COLLECTING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            ExchangeClaim existing = ExchangeFixtures.collectingExchangeClaim();
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("COLLECT_DONE 이고 기존 교환 없으면 EXCHANGE_CREATED를 반환한다")
        void resolve_CollectDone_NoExisting_ReturnsExchangeCreated() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("COLLECT_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(exchangeReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_CREATED);
        }

        @Test
        @DisplayName("COLLECT_DONE 이고 기존 교환 상태가 COLLECTING이면 EXCHANGE_COLLECTED를 반환한다")
        void resolve_CollectDone_CollectingExchange_ReturnsExchangeCollected() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("COLLECT_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            ExchangeClaim existing = ExchangeFixtures.collectingExchangeClaim();
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_COLLECTED);
        }

        @Test
        @DisplayName("EXCHANGE_REDELIVERING 이고 기존 교환 없으면 EXCHANGE_CREATED를 반환한다")
        void resolve_ExchangeRedelivering_NoExisting_ReturnsExchangeCreated() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.exchangePayload("EXCHANGE_REDELIVERING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(exchangeReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_CREATED);
        }

        @Test
        @DisplayName("EXCHANGE_REDELIVERING 이고 기존 교환 상태가 COLLECTED이면 EXCHANGE_SHIPPING을 반환한다")
        void resolve_ExchangeRedelivering_CollectedExchange_ReturnsExchangeShipping() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.exchangePayload("EXCHANGE_REDELIVERING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            ExchangeClaim existing = ExchangeFixtures.collectedExchangeClaim();
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_SHIPPING);
        }

        @Test
        @DisplayName("EXCHANGE_REDELIVERING 이고 기존 교환 상태가 PREPARING이면 EXCHANGE_SHIPPING을 반환한다")
        void resolve_ExchangeRedelivering_PreparingExchange_ReturnsExchangeShipping() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.exchangePayload("EXCHANGE_REDELIVERING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            ExchangeClaim existing = ExchangeFixtures.preparingExchangeClaim();
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_SHIPPING);
        }

        @Test
        @DisplayName("EXCHANGE_DONE 이고 기존 교환 없으면 EXCHANGE_COMPLETED를 반환한다")
        void resolve_ExchangeDone_NoExisting_ReturnsExchangeCompleted() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(exchangeReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_COMPLETED);
        }

        @Test
        @DisplayName("EXCHANGE_DONE 이고 기존 교환 상태가 SHIPPING이면 EXCHANGE_COMPLETED를 반환한다")
        void resolve_ExchangeDone_ShippingExchange_ReturnsExchangeCompleted() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            ExchangeClaim existing = ExchangeFixtures.shippingExchangeClaim();
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_COMPLETED);
        }

        @Test
        @DisplayName("EXCHANGE_DONE 이고 기존 교환 상태가 COMPLETED이면 SKIPPED를 반환한다")
        void resolve_ExchangeDone_CompletedExchange_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            ExchangeClaim existing = ExchangeFixtures.completedExchangeClaim();
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("EXCHANGE_REJECT 이고 기존 교환 없으면 SKIPPED를 반환한다")
        void resolve_ExchangeReject_NoExisting_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(exchangeReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }

        @Test
        @DisplayName("EXCHANGE_REJECT 이고 기존 교환이 있으면 EXCHANGE_REJECTED를 반환한다")
        void resolve_ExchangeReject_ExistingExchange_ReturnsExchangeRejected() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            ExchangeClaim existing = ExchangeFixtures.requestedExchangeClaim();
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(existing));

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.EXCHANGE_REJECTED);
        }

        @Test
        @DisplayName("알 수 없는 claimStatus이면 SKIPPED를 반환한다")
        void resolve_UnknownStatus_ReturnsSkipped() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("UNKNOWN_STATUS");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            given(exchangeReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            ClaimSyncAction action = sut.resolve(payload, orderItemId);

            // then
            assertThat(action).isEqualTo(ClaimSyncAction.SKIPPED);
        }
    }

    @Nested
    @DisplayName("execute() - EXCHANGE_CREATED 액션 실행")
    class ExecuteExchangeCreatedTest {

        @Test
        @DisplayName("EXCHANGE_CREATED 실행 시 교환을 생성하고 OrderItem을 반품요청 처리한다")
        void execute_ExchangeCreated_CreatesExchangeAndRequestsReturn() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_REQUEST");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();
            OrderItem confirmedOrderItem = OrderFixtures.confirmedOrderItem();

            given(timeProvider.now()).willReturn(now);
            given(orderItemReadManager.findById(orderItemId))
                    .willReturn(Optional.of(confirmedOrderItem));

            // when
            long result =
                    sut.execute(ClaimSyncAction.EXCHANGE_CREATED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
            then(orderItemCommandManager).should().persistAll(any(List.class));
        }
    }

    @Nested
    @DisplayName("execute() - EXCHANGE_COLLECTING 액션 실행")
    class ExecuteExchangeCollectingTest {

        @Test
        @DisplayName("EXCHANGE_COLLECTING 실행 시 수거를 시작하고 0을 반환한다")
        void execute_ExchangeCollecting_StartsCollecting() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("COLLECTING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();
            ExchangeClaim requestedExchange = ExchangeFixtures.requestedExchangeClaim();

            given(timeProvider.now()).willReturn(now);
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(requestedExchange));

            // when
            long result =
                    sut.execute(
                            ClaimSyncAction.EXCHANGE_COLLECTING, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
            then(claimShipmentCommandManager).should().persist(any(ClaimShipment.class));
        }

        @Test
        @DisplayName("EXCHANGE_COLLECTING 실행 시 수거 배송 정보가 없으면 ClaimShipment를 생성하지 않는다")
        void execute_ExchangeCollecting_WithoutShipmentInfo_DoesNotCreateClaimShipment() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.exchangePayloadWithReason("SIZE_CHANGE", "사이즈 교환");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();
            ExchangeClaim requestedExchange = ExchangeFixtures.requestedExchangeClaim();

            given(timeProvider.now()).willReturn(now);
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(requestedExchange));

            // when
            long result =
                    sut.execute(
                            ClaimSyncAction.EXCHANGE_COLLECTING, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
            then(claimShipmentCommandManager).should(never()).persist(any());
        }
    }

    @Nested
    @DisplayName("execute() - EXCHANGE_COLLECTED 액션 실행")
    class ExecuteExchangeCollectedTest {

        @Test
        @DisplayName("EXCHANGE_COLLECTED 실행 시 수거를 완료하고 0을 반환한다")
        void execute_ExchangeCollected_CompletesCollection() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("COLLECT_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();
            ExchangeClaim collectingExchange = ExchangeFixtures.collectingExchangeClaim();

            given(timeProvider.now()).willReturn(now);
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(collectingExchange));

            // when
            long result =
                    sut.execute(ClaimSyncAction.EXCHANGE_COLLECTED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
        }
    }

    @Nested
    @DisplayName("execute() - EXCHANGE_SHIPPING 액션 실행")
    class ExecuteExchangeShippingTest {

        @Test
        @DisplayName("COLLECTED 상태에서 EXCHANGE_SHIPPING 실행 시 준비→배송 처리한다")
        void execute_ExchangeShipping_FromCollected_PreparesAndShips() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.exchangePayload("EXCHANGE_REDELIVERING");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();
            ExchangeClaim collectedExchange = ExchangeFixtures.collectedExchangeClaim();

            given(timeProvider.now()).willReturn(now);
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(collectedExchange));

            // when
            long result =
                    sut.execute(ClaimSyncAction.EXCHANGE_SHIPPING, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
        }
    }

    @Nested
    @DisplayName("execute() - EXCHANGE_COMPLETED 액션 실행")
    class ExecuteExchangeCompletedTest {

        @Test
        @DisplayName("기존 교환이 있으면 완료 처리하고 OrderItem을 반품완료 처리한다")
        void execute_ExchangeCompleted_ExistingExchange_CompletesAndReturnsOrderItem() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();
            ExchangeClaim shippingExchange = ExchangeFixtures.shippingExchangeClaim();
            OrderItem orderItem =
                    OrderFixtures.reconstitutedOrderItem(
                            1L,
                            com.ryuqq.marketplace.domain.order.vo.OrderItemStatus.RETURN_REQUESTED);

            given(timeProvider.now()).willReturn(now);
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(shippingExchange));
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.of(orderItem));

            // when
            long result =
                    sut.execute(ClaimSyncAction.EXCHANGE_COMPLETED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
        }

        @Test
        @DisplayName("기존 교환이 없으면 전체 상태 순차 처리 후 완료한다")
        void execute_ExchangeCompleted_NoExisting_CreatesAndCompletes() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_DONE");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);
            given(exchangeReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.empty());

            // when
            long result =
                    sut.execute(ClaimSyncAction.EXCHANGE_COMPLETED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
        }
    }

    @Nested
    @DisplayName("execute() - EXCHANGE_REJECTED 액션 실행")
    class ExecuteExchangeRejectedTest {

        @Test
        @DisplayName("EXCHANGE_REJECTED 실행 시 교환을 거절하고 0을 반환한다")
        void execute_ExchangeRejected_RejectsExchange() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayload("EXCHANGE_REJECT");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();
            ExchangeClaim requestedExchange = ExchangeFixtures.requestedExchangeClaim();

            given(timeProvider.now()).willReturn(now);
            given(exchangeReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(requestedExchange));

            // when
            long result =
                    sut.execute(ClaimSyncAction.EXCHANGE_REJECTED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
            then(orderItemCommandManager).should(never()).persistAll(any());
        }
    }

    @Nested
    @DisplayName("교환 사유 파싱 테스트")
    class ReasonParsingTest {

        @Test
        @DisplayName("알려진 교환 사유는 해당 타입으로 파싱된다")
        void execute_KnownExchangeReason_ParsesCorrectly() {
            // given
            ExternalClaimPayload payload =
                    ClaimSyncFixtures.exchangePayloadWithReason("DEFECTIVE", "상품 불량입니다");
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.empty());

            // when
            long result =
                    sut.execute(ClaimSyncAction.EXCHANGE_CREATED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
        }

        @Test
        @DisplayName("null 교환 사유는 OTHER로 파싱된다")
        void execute_NullExchangeReason_ParsesAsOther() {
            // given
            ExternalClaimPayload payload = ClaimSyncFixtures.exchangePayloadWithReason(null, null);
            OrderItemId orderItemId = OrderItemId.of(ClaimSyncFixtures.DEFAULT_ORDER_ITEM_ID);
            long sellerId = 100L;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);
            given(orderItemReadManager.findById(orderItemId)).willReturn(Optional.empty());

            // when
            long result =
                    sut.execute(ClaimSyncAction.EXCHANGE_CREATED, payload, orderItemId, sellerId);

            // then
            assertThat(result).isZero();
            then(exchangeCommandManager).should().persist(any(ExchangeClaim.class));
        }
    }
}
