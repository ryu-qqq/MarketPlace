package com.ryuqq.marketplace.application.order.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.order.OrderQueryFixtures;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.PaymentInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.application.order.internal.ProductOrderDetailBundle;
import com.ryuqq.marketplace.application.order.internal.ProductOrderListBundle;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderAssembler 단위 테스트")
class OrderAssemblerTest {

    private OrderAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new OrderAssembler();
    }

    // ==================== toProductOrderPageResult ====================

    @Nested
    @DisplayName("toProductOrderPageResult() - 상품주문 페이지 결과 조립")
    class ToProductOrderPageResultTest {

        @Test
        @DisplayName("번들의 orderItems를 ProductOrderListResult 목록으로 변환한다")
        void toProductOrderPageResult_ValidBundle_ReturnsPageResult() {
            // given
            ProductOrderListBundle bundle = OrderQueryFixtures.productOrderListBundle();
            int page = 0;
            int size = 20;

            // when
            ProductOrderPageResult result = sut.toProductOrderPageResult(bundle, page, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productOrders()).hasSize(1);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
        }

        @Test
        @DisplayName("빈 번들은 빈 목록의 페이지 결과를 반환한다")
        void toProductOrderPageResult_EmptyBundle_ReturnsEmptyPageResult() {
            // given
            ProductOrderListBundle bundle = OrderQueryFixtures.emptyProductOrderListBundle();
            int page = 0;
            int size = 20;

            // when
            ProductOrderPageResult result = sut.toProductOrderPageResult(bundle, page, size);

            // then
            assertThat(result.productOrders()).isEmpty();
        }

        @Test
        @DisplayName("PaymentInfo에 paymentId와 paymentNumber가 Order 기본 정보에서 조립된다")
        void toProductOrderPageResult_PaymentInfoFromOrder_PaymentIdAndNumberFromOrder() {
            // given
            ProductOrderListBundle bundle = OrderQueryFixtures.productOrderListBundle();

            // when
            ProductOrderPageResult result = sut.toProductOrderPageResult(bundle, 0, 20);

            // then
            ProductOrderListResult item = result.productOrders().get(0);
            PaymentInfo paymentInfo = item.payment();
            assertThat(paymentInfo.paymentId()).isEqualTo(OrderQueryFixtures.DEFAULT_PAYMENT_ID);
            assertThat(paymentInfo.paymentNumber())
                    .isEqualTo(OrderQueryFixtures.DEFAULT_PAYMENT_NUMBER);
        }

        @Test
        @DisplayName("주문 정보가 없는 아이템의 PaymentInfo는 빈 상태로 조립된다")
        void toProductOrderPageResult_MissingOrder_PaymentInfoIsEmpty() {
            // given
            OrderItemResult item = OrderQueryFixtures.orderItemResult(9999L, "UNKNOWN-ORDER-ID");
            ProductOrderListBundle bundle =
                    new ProductOrderListBundle(List.of(item), Map.of(), Map.of(), Map.of(), 1L);

            // when
            ProductOrderPageResult result = sut.toProductOrderPageResult(bundle, 0, 20);

            // then
            ProductOrderListResult productOrder = result.productOrders().get(0);
            assertThat(productOrder.payment().paymentAmount()).isZero();
        }
    }

    // ==================== toProductOrderDetailResult ====================

    @Nested
    @DisplayName("toProductOrderDetailResult() - 상품주문 상세 결과 조립")
    class ToProductOrderDetailResultTest {

        @Test
        @DisplayName("PaymentResult의 paymentId가 PaymentInfo에 반영된다")
        void toProductOrderDetailResult_PaymentIdIsReflected() {
            // given
            ProductOrderDetailBundle bundle = OrderQueryFixtures.productOrderDetailBundle();

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            PaymentInfo paymentInfo = result.payment();
            assertThat(paymentInfo.paymentId()).isEqualTo(OrderQueryFixtures.DEFAULT_PAYMENT_ID);
        }

        @Test
        @DisplayName("PaymentResult의 paymentNumber가 PaymentInfo에 반영된다")
        void toProductOrderDetailResult_PaymentNumberIsReflected() {
            // given
            ProductOrderDetailBundle bundle = OrderQueryFixtures.productOrderDetailBundle();

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            PaymentInfo paymentInfo = result.payment();
            assertThat(paymentInfo.paymentNumber())
                    .isEqualTo(OrderQueryFixtures.DEFAULT_PAYMENT_NUMBER);
        }

        @Test
        @DisplayName("PaymentResult의 paymentStatus, paymentMethod, paymentAgencyId가 반영된다")
        void toProductOrderDetailResult_AllPaymentFieldsAreReflected() {
            // given
            PaymentResult payment = OrderQueryFixtures.paymentResult();
            ProductOrderDetailBundle bundle = OrderQueryFixtures.productOrderDetailBundle();

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            PaymentInfo paymentInfo = result.payment();
            assertThat(paymentInfo.paymentStatus()).isEqualTo(payment.paymentStatus());
            assertThat(paymentInfo.paymentMethod()).isEqualTo(payment.paymentMethod());
            assertThat(paymentInfo.paymentAgencyId()).isEqualTo(payment.paymentAgencyId());
            assertThat(paymentInfo.paymentAmount()).isEqualTo(payment.paymentAmount());
            assertThat(paymentInfo.paidAt()).isEqualTo(payment.paidAt());
            assertThat(paymentInfo.canceledAt()).isNull();
        }

        @Test
        @DisplayName("payment가 null이면 PaymentInfo의 모든 필드가 기본값으로 설정된다")
        void toProductOrderDetailResult_NullPayment_ReturnsEmptyPaymentInfo() {
            // given
            ProductOrderDetailBundle bundle =
                    OrderQueryFixtures.productOrderDetailBundleWithNullPayment();

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            PaymentInfo paymentInfo = result.payment();
            assertThat(paymentInfo.paymentId()).isNull();
            assertThat(paymentInfo.paymentNumber()).isNull();
            assertThat(paymentInfo.paymentStatus()).isNull();
            assertThat(paymentInfo.paymentMethod()).isNull();
            assertThat(paymentInfo.paymentAmount()).isZero();
        }

        @Test
        @DisplayName("취소된 결제의 canceledAt이 PaymentInfo에 반영된다")
        void toProductOrderDetailResult_CanceledPayment_CanceledAtIsReflected() {
            // given
            PaymentResult canceledPayment = OrderQueryFixtures.paymentResultWithCanceled();
            ProductOrderDetailBundle bundle =
                    new ProductOrderDetailBundle(
                            OrderQueryFixtures.orderItemResult(),
                            OrderQueryFixtures.orderListResult(),
                            canceledPayment,
                            List.of(),
                            List.of(),
                            List.of());

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            assertThat(result.payment().canceledAt()).isEqualTo(canceledPayment.canceledAt());
        }

        @Test
        @DisplayName("OrderInfo가 올바르게 조립된다")
        void toProductOrderDetailResult_OrderInfoIsAssembled() {
            // given
            ProductOrderDetailBundle bundle = OrderQueryFixtures.productOrderDetailBundle();

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            assertThat(result.order()).isNotNull();
            assertThat(result.order().orderId()).isEqualTo(OrderQueryFixtures.DEFAULT_ORDER_ID);
            assertThat(result.order().orderNumber())
                    .isEqualTo(OrderQueryFixtures.DEFAULT_ORDER_NUMBER);
        }

        @Test
        @DisplayName("취소/클레임 목록이 결과에 포함된다")
        void toProductOrderDetailResult_CancelsAndClaimsAreIncluded() {
            // given
            ProductOrderDetailBundle bundle =
                    OrderQueryFixtures.productOrderDetailBundleWithHistories();

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            assertThat(result.cancels()).hasSize(1);
            assertThat(result.claims()).hasSize(1);
            assertThat(result.timeLine()).hasSize(1);
        }
    }

    // ==================== CancelSummary 조립 ====================

    @Nested
    @DisplayName("CancelSummary - 취소 요약 조립")
    class CancelSummaryTest {

        @Test
        @DisplayName("취소 내역이 없으면 CancelSummary.none()으로 조립된다")
        void toProductOrderDetailResult_NoCancels_ReturnsCancelSummaryNone() {
            // given
            ProductOrderDetailBundle bundle = OrderQueryFixtures.productOrderDetailBundle();

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            CancelSummary cancel = result.cancel();
            assertThat(cancel.hasActiveCancel()).isFalse();
            assertThat(cancel.totalCancelledQty()).isZero();
            assertThat(cancel.latest()).isNull();
        }

        @Test
        @DisplayName("완료된 취소가 있으면 totalCancelledQty에 반영된다")
        void toProductOrderPageResult_CompletedCancel_TotalCancelledQtyIsReflected() {
            // given
            ProductOrderListBundle bundle = OrderQueryFixtures.productOrderListBundleWithCancels();

            // when
            ProductOrderPageResult result = sut.toProductOrderPageResult(bundle, 0, 20);

            // then
            CancelSummary cancel = result.productOrders().get(0).cancel();
            assertThat(cancel.totalCancelledQty()).isEqualTo(1);
            assertThat(cancel.latest()).isNotNull();
            assertThat(cancel.latest().status()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("활성 취소(REQUESTED)가 있으면 hasActiveCancel이 true이다")
        void toProductOrderDetailResult_ActiveCancel_HasActiveCancelIsTrue() {
            // given
            OrderCancelResult requestedCancel = OrderQueryFixtures.requestedCancelResult(1001L);
            ProductOrderDetailBundle bundle =
                    new ProductOrderDetailBundle(
                            OrderQueryFixtures.orderItemResult(),
                            OrderQueryFixtures.orderListResult(),
                            OrderQueryFixtures.paymentResult(),
                            List.of(requestedCancel),
                            List.of(),
                            List.of());

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            assertThat(result.cancel().hasActiveCancel()).isTrue();
        }
    }

    // ==================== ClaimSummary 조립 ====================

    @Nested
    @DisplayName("ClaimSummary - 클레임 요약 조립")
    class ClaimSummaryTest {

        @Test
        @DisplayName("클레임 내역이 없으면 ClaimSummary.none()으로 조립된다")
        void toProductOrderDetailResult_NoClaims_ReturnsClaimSummaryNone() {
            // given
            ProductOrderDetailBundle bundle = OrderQueryFixtures.productOrderDetailBundle();

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            ClaimSummary claim = result.claim();
            assertThat(claim.hasActiveClaim()).isFalse();
            assertThat(claim.totalClaimedQty()).isZero();
            assertThat(claim.latest()).isNull();
        }

        @Test
        @DisplayName("활성 클레임(REQUESTED)이 있으면 hasActiveClaim이 true이다")
        void toProductOrderDetailResult_ActiveClaim_HasActiveClaimIsTrue() {
            // given
            OrderClaimResult requestedClaim = OrderQueryFixtures.requestedClaimResult(1001L);
            ProductOrderDetailBundle bundle =
                    new ProductOrderDetailBundle(
                            OrderQueryFixtures.orderItemResult(),
                            OrderQueryFixtures.orderListResult(),
                            OrderQueryFixtures.paymentResult(),
                            List.of(),
                            List.of(requestedClaim),
                            List.of());

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            assertThat(result.claim().hasActiveClaim()).isTrue();
            assertThat(result.claim().activeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("완료된 클레임이 있으면 totalClaimedQty에 반영된다")
        void toProductOrderDetailResult_CompletedClaim_TotalClaimedQtyIsReflected() {
            // given
            OrderClaimResult completedClaim = OrderQueryFixtures.completedClaimResult(1001L);
            ProductOrderDetailBundle bundle =
                    new ProductOrderDetailBundle(
                            OrderQueryFixtures.orderItemResult(),
                            OrderQueryFixtures.orderListResult(),
                            OrderQueryFixtures.paymentResult(),
                            List.of(),
                            List.of(completedClaim),
                            List.of());

            // when
            ProductOrderDetailResult result = sut.toProductOrderDetailResult(bundle);

            // then
            assertThat(result.claim().totalClaimedQty()).isEqualTo(1);
            assertThat(result.claim().latest()).isNotNull();
            assertThat(result.claim().latest().status()).isEqualTo("COMPLETED");
        }
    }

    // ==================== toSummaryResult 조립 ====================

    @Nested
    @DisplayName("toSummaryResult() - 주문상품 상태별 요약 조립")
    class ToSummaryResultTest {

        @Test
        @DisplayName("상태별 카운트 맵을 OrderSummaryResult로 변환한다")
        void toSummaryResult_ValidStatusCounts_ReturnsSummaryResult() {
            // given
            Map<OrderItemStatus, Long> statusCounts = OrderQueryFixtures.orderItemStatusCounts();

            // when
            OrderSummaryResult result = sut.toSummaryResult(statusCounts);

            // then
            assertThat(result.ready()).isEqualTo(10L);
            assertThat(result.confirmed()).isEqualTo(5L);
            assertThat(result.cancelled()).isEqualTo(2L);
            assertThat(result.returnRequested()).isEqualTo(1L);
            assertThat(result.returned()).isZero();
        }

        @Test
        @DisplayName("빈 카운트 맵이면 모든 값이 0인 요약을 반환한다")
        void toSummaryResult_EmptyMap_ReturnsZeroSummary() {
            // given
            Map<OrderItemStatus, Long> emptyCounts = Map.of();

            // when
            OrderSummaryResult result = sut.toSummaryResult(emptyCounts);

            // then
            assertThat(result.ready()).isZero();
            assertThat(result.confirmed()).isZero();
            assertThat(result.cancelled()).isZero();
            assertThat(result.returnRequested()).isZero();
            assertThat(result.returned()).isZero();
        }

        @Test
        @DisplayName("일부 상태만 있는 카운트 맵이면 없는 상태는 0으로 처리된다")
        void toSummaryResult_PartialStatusCounts_MissingStatusIsZero() {
            // given
            Map<OrderItemStatus, Long> partialCounts =
                    Map.of(
                            OrderItemStatus.READY, 3L,
                            OrderItemStatus.CONFIRMED, 1L);

            // when
            OrderSummaryResult result = sut.toSummaryResult(partialCounts);

            // then
            assertThat(result.ready()).isEqualTo(3L);
            assertThat(result.confirmed()).isEqualTo(1L);
            assertThat(result.cancelled()).isZero();
            assertThat(result.returnRequested()).isZero();
            assertThat(result.returned()).isZero();
        }
    }
}
