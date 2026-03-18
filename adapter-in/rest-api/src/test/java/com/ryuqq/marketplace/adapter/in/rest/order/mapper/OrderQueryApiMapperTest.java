package com.ryuqq.marketplace.adapter.in.rest.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.query.SearchOrdersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderSummaryApiResponse;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderQueryApiMapper 단위 테스트")
class OrderQueryApiMapperTest {

    private OrderQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchOrdersApiRequest를 OrderSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchOrdersApiRequest request =
                    OrderApiFixtures.searchRequest("PREPARING", "ORDER_NUMBER", "ORD-001", 0, 20);

            // when
            OrderSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.searchField()).isEqualTo("ORDER_NUMBER");
            assertThat(result.searchWord()).isEqualTo("ORD-001");
            assertThat(result.searchParams().page()).isZero();
            assertThat(result.searchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("page/size가 null이면 기본값(0, 20)으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            SearchOrdersApiRequest request = OrderApiFixtures.searchRequest();

            // when
            OrderSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.searchParams().page()).isZero();
            assertThat(result.searchParams().size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toListResponse() - 목록 단건 변환 (payment 필드 매핑 검증)")
    class ToListResponseTest {

        @Test
        @DisplayName("ProductOrderListResult를 OrderListApiResponse로 변환한다")
        void toListResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response).isNotNull();
            assertThat(response.order()).isNotNull();
            assertThat(response.productOrder()).isNotNull();
            assertThat(response.payment()).isNotNull();
            assertThat(response.receiver()).isNotNull();
            assertThat(response.delivery()).isNotNull();
        }

        @Test
        @DisplayName("paymentId가 String으로 매핑된다")
        void toListResponse_PaymentIdMappedAsString() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.payment().paymentId())
                    .isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_ID);
            assertThat(response.payment().paymentId()).isInstanceOf(String.class);
        }

        @Test
        @DisplayName("paymentNumber가 올바르게 매핑된다")
        void toListResponse_PaymentNumberMapped() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.payment().paymentNumber())
                    .isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_NUMBER);
        }

        @Test
        @DisplayName("payment 전체 필드가 올바르게 매핑된다")
        void toListResponse_AllPaymentFieldsMapped() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponse response = mapper.toListResponse(result);

            // then
            OrderListApiResponse.PaymentInfoApiResponse payment = response.payment();
            assertThat(payment.paymentId()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_ID);
            assertThat(payment.paymentNumber()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_NUMBER);
            assertThat(payment.paymentStatus()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_STATUS);
            assertThat(payment.paymentMethod()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_METHOD);
            assertThat(payment.paymentAgencyId())
                    .isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_AGENCY_ID);
            assertThat(payment.paymentAmount()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_AMOUNT);
        }

        @Test
        @DisplayName("결제일시가 ISO 8601 형식으로 변환된다")
        void toListResponse_PaidAtFormattedAsIso8601() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.payment().paidAt()).contains("T");
            assertThat(response.payment().paidAt()).contains("+09:00");
        }

        @Test
        @DisplayName("canceledAt이 null이면 null을 반환한다")
        void toListResponse_NullCanceledAt_ReturnsNull() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.payment().canceledAt()).isNull();
        }

        @Test
        @DisplayName("order 정보가 올바르게 매핑된다")
        void toListResponse_OrderInfoMapped() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.order().orderId()).isEqualTo(OrderApiFixtures.DEFAULT_ORDER_ID);
            assertThat(response.order().orderNumber())
                    .isEqualTo(OrderApiFixtures.DEFAULT_ORDER_NUMBER);
            assertThat(response.order().status()).isEqualTo(OrderApiFixtures.DEFAULT_ORDER_STATUS);
            assertThat(response.order().buyerName()).isEqualTo(OrderApiFixtures.DEFAULT_BUYER_NAME);
        }

        @Test
        @DisplayName("productOrder 정보가 올바르게 매핑된다")
        void toListResponse_ProductOrderInfoMapped() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.productOrder().orderItemId())
                    .isEqualTo(OrderApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.productOrder().productGroupName())
                    .isEqualTo(OrderApiFixtures.DEFAULT_PRODUCT_GROUP_NAME);
            assertThat(response.productOrder().skuCode())
                    .isEqualTo(OrderApiFixtures.DEFAULT_SKU_CODE);
        }
    }

    @Nested
    @DisplayName("toDetailResponse() - 상세 응답 변환 (payment 필드 매핑 검증)")
    class ToDetailResponseTest {

        @Test
        @DisplayName("ProductOrderDetailResult를 OrderDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            ProductOrderDetailResult result = OrderApiFixtures.productOrderDetailResult();

            // when
            OrderDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response).isNotNull();
            assertThat(response.order()).isNotNull();
            assertThat(response.productOrder()).isNotNull();
            assertThat(response.payment()).isNotNull();
            assertThat(response.settlement()).isNotNull();
        }

        @Test
        @DisplayName("상세에서 paymentId가 String으로 매핑된다")
        void toDetailResponse_PaymentIdMappedAsString() {
            // given
            ProductOrderDetailResult result = OrderApiFixtures.productOrderDetailResult();

            // when
            OrderDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.payment().paymentId())
                    .isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_ID);
            assertThat(response.payment().paymentId()).isInstanceOf(String.class);
        }

        @Test
        @DisplayName("상세에서 paymentNumber가 올바르게 매핑된다")
        void toDetailResponse_PaymentNumberMapped() {
            // given
            ProductOrderDetailResult result = OrderApiFixtures.productOrderDetailResult();

            // when
            OrderDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.payment().paymentNumber())
                    .isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_NUMBER);
        }

        @Test
        @DisplayName("상세에서 payment 전체 필드가 올바르게 매핑된다")
        void toDetailResponse_AllPaymentFieldsMapped() {
            // given
            ProductOrderDetailResult result = OrderApiFixtures.productOrderDetailResult();

            // when
            OrderDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            OrderListApiResponse.PaymentInfoApiResponse payment = response.payment();
            assertThat(payment.paymentId()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_ID);
            assertThat(payment.paymentNumber()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_NUMBER);
            assertThat(payment.paymentStatus()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_STATUS);
            assertThat(payment.paymentMethod()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_METHOD);
            assertThat(payment.paymentAmount()).isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_AMOUNT);
        }

        @Test
        @DisplayName("취소 목록이 올바르게 매핑된다")
        void toDetailResponse_CancelsMapped() {
            // given
            ProductOrderDetailResult result = OrderApiFixtures.productOrderDetailResult();

            // when
            OrderDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.cancels()).hasSize(1);
            assertThat(response.cancels().get(0).cancelNumber())
                    .isEqualTo(OrderApiFixtures.DEFAULT_CANCEL_NUMBER);
            assertThat(response.cancels().get(0).cancelStatus()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("클레임 목록이 올바르게 매핑된다")
        void toDetailResponse_ClaimsMapped() {
            // given
            ProductOrderDetailResult result = OrderApiFixtures.productOrderDetailResult();

            // when
            OrderDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.claims()).hasSize(1);
            assertThat(response.claims().get(0).claimNumber())
                    .isEqualTo(OrderApiFixtures.DEFAULT_CLAIM_NUMBER);
            assertThat(response.claims().get(0).claimType()).isEqualTo("REFUND");
        }

        @Test
        @DisplayName("타임라인이 올바르게 매핑된다")
        void toDetailResponse_TimeLineMapped() {
            // given
            ProductOrderDetailResult result = OrderApiFixtures.productOrderDetailResult();

            // when
            OrderDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.timeLine()).hasSize(1);
            assertThat(response.timeLine().get(0).fromStatus()).isEqualTo("ORDERED");
            assertThat(response.timeLine().get(0).toStatus()).isEqualTo("PREPARING");
        }

        @Test
        @DisplayName("정산 정보가 올바르게 매핑된다")
        void toDetailResponse_SettlementMapped() {
            // given
            ProductOrderDetailResult result = OrderApiFixtures.productOrderDetailResult();

            // when
            OrderDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.settlement().fee()).isEqualTo(5000);
            assertThat(response.settlement().expectationSettlementAmount()).isEqualTo(45000);
        }

        @Test
        @DisplayName("ProductOrderDetailResult를 OrderDetailApiResponseV4로 변환한다")
        void toDetailResponseV4_ConvertsResult_ReturnsV4ApiResponse() {
            // given
            ProductOrderDetailResult result = OrderApiFixtures.productOrderDetailResult();

            // when
            OrderDetailApiResponseV4 response = mapper.toDetailResponseV4(result);

            // then
            assertThat(response).isNotNull();
            assertThat(response.orderId()).isEqualTo(OrderApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.buyerInfo()).isNotNull();
            assertThat(response.settlementInfo()).isNotNull();
            assertThat(response.orderHistories()).isNotEmpty();
            assertThat(response.cancelIds()).isNotEmpty();
            assertThat(response.cancels()).isNotEmpty();
            assertThat(response.claimIds()).isNotEmpty();
            assertThat(response.claims()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환 (payment 필드 매핑 검증)")
    class ToPageResponseTest {

        @Test
        @DisplayName("ProductOrderPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            ProductOrderPageResult pageResult = OrderApiFixtures.productOrderPageResult(3, 0, 20);

            // when
            PageApiResponse<OrderListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("페이지 내 각 항목의 paymentId가 String으로 매핑된다")
        void toPageResponse_EachItemPaymentIdMappedAsString() {
            // given
            ProductOrderPageResult pageResult = OrderApiFixtures.productOrderPageResult(2, 0, 20);

            // when
            PageApiResponse<OrderListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            response.content()
                    .forEach(
                            item -> {
                                assertThat(item.payment().paymentId()).isNotNull();
                                assertThat(item.payment().paymentId()).isInstanceOf(String.class);
                            });
        }

        @Test
        @DisplayName("페이지 내 각 항목의 paymentNumber가 올바르게 매핑된다")
        void toPageResponse_EachItemPaymentNumberMapped() {
            // given
            ProductOrderPageResult pageResult = OrderApiFixtures.productOrderPageResult(2, 0, 20);

            // when
            PageApiResponse<OrderListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            response.content()
                    .forEach(
                            item ->
                                    assertThat(item.payment().paymentNumber())
                                            .isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_NUMBER));
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            ProductOrderPageResult pageResult = OrderApiFixtures.emptyPageResult();

            // when
            PageApiResponse<OrderListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toListResponseV4() - V4 호환 목록 변환 (legacyOrderId, settlementInfo 제외, 기본값 0/\"\")")
    class ToListResponseV4Test {

        @Test
        @DisplayName("ProductOrderListResult를 OrderListApiResponseV4로 변환한다")
        void toListResponseV4_ConvertsResult_ReturnsV4ApiResponse() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponseV4 response = mapper.toListResponseV4(result);

            // then
            assertThat(response).isNotNull();
            assertThat(response.orderId()).isEqualTo(OrderApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.orderNumber()).isEqualTo("ORD-20250115-0001-001");
            assertThat(response.buyerInfo()).isNotNull();
            assertThat(response.buyerInfo().buyerName())
                    .isEqualTo(OrderApiFixtures.DEFAULT_BUYER_NAME);
            assertThat(response.payment()).isNotNull();
            assertThat(response.receiverInfo()).isNotNull();
            assertThat(response.paymentShipmentInfo()).isNotNull();
            assertThat(response.orderProduct()).isNotNull();
            assertThat(response.externalOrderInfo()).isNotNull();
        }

        @Test
        @DisplayName("미존재 필드는 0 또는 빈 문자열로 채운다")
        void toListResponseV4_MissingFields_FilledWithDefaults() {
            // given
            ProductOrderListResult result = OrderApiFixtures.productOrderListResult();

            // when
            OrderListApiResponseV4 response = mapper.toListResponseV4(result);

            // then
            assertThat(response.payment().paymentId())
                    .isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_ID);
            assertThat(response.payment().paymentNumber())
                    .isEqualTo(OrderApiFixtures.DEFAULT_PAYMENT_NUMBER);
            assertThat(response.payment().userId()).isZero();
            assertThat(response.payment().billAmount())
                    .isEqualTo(response.payment().paymentAmount());
            assertThat(response.payment().usedMileageAmount()).isZero();
            assertThat(response.orderProduct().totalExpectedRefundMileageAmount()).isZero();
            assertThat(response.orderProduct().deliveryArea()).isEmpty();
        }

        @Test
        @DisplayName("toPageResponseV4로 페이지 결과를 변환한다")
        void toPageResponseV4_ConvertsPageResult_ReturnsV4PageResponse() {
            // given
            ProductOrderPageResult pageResult = OrderApiFixtures.productOrderPageResult(2, 0, 20);

            // when
            PageApiResponse<OrderListApiResponseV4> response = mapper.toPageResponseV4(pageResult);

            // then
            assertThat(response.content()).hasSize(2);
            assertThat(response.totalElements()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("toSummaryResponse() - 요약 응답 변환")
    class ToSummaryResponseTest {

        @Test
        @DisplayName("OrderSummaryResult를 OrderSummaryApiResponse로 변환한다")
        void toSummaryResponse_ConvertsSummaryResult_ReturnsApiResponse() {
            // given
            OrderSummaryResult result = OrderApiFixtures.orderSummaryResult();

            // when
            OrderSummaryApiResponse response = mapper.toSummaryResponse(result);

            // then
            assertThat(response.ordered()).isEqualTo(10);
            assertThat(response.preparing()).isEqualTo(5);
            assertThat(response.shipped()).isEqualTo(30);
            assertThat(response.delivered()).isEqualTo(15);
            assertThat(response.confirmed()).isEqualTo(8);
            assertThat(response.cancelled()).isEqualTo(3);
            assertThat(response.claimInProgress()).isEqualTo(2);
            assertThat(response.refunded()).isEqualTo(4);
            assertThat(response.exchanged()).isEqualTo(1);
        }
    }
}
