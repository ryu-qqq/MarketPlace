package com.ryuqq.marketplace.adapter.in.rest.shipment.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.ShipmentApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipmentSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentSummaryApiResponse;
import com.ryuqq.marketplace.application.shipment.dto.query.ShipmentSearchParams;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentQueryApiMapper 단위 테스트")
class ShipmentQueryApiMapperTest {

    private ShipmentQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShipmentQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("ShipmentSearchApiRequest를 ShipmentSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            ShipmentSearchApiRequest request =
                    ShipmentApiFixtures.searchRequest(
                            List.of("READY", "PREPARING"),
                            "shipmentNumber",
                            "SN-001",
                            "createdAt",
                            "DESC",
                            0,
                            20);

            // when
            ShipmentSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactly("READY", "PREPARING");
            assertThat(result.searchField()).isEqualTo("shipmentNumber");
            assertThat(result.searchWord()).isEqualTo("SN-001");
            assertThat(result.searchParams().page()).isZero();
            assertThat(result.searchParams().size()).isEqualTo(20);
            assertThat(result.searchParams().sortKey()).isEqualTo("createdAt");
            assertThat(result.searchParams().sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값(0, 20)으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            ShipmentSearchApiRequest request = ShipmentApiFixtures.searchRequest();

            // when
            ShipmentSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.searchParams().page()).isZero();
            assertThat(result.searchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("statuses가 null이면 빈 리스트로 변환된다")
        void toSearchParams_NullStatuses_ReturnsEmptyStatuses() {
            // given
            ShipmentSearchApiRequest request = ShipmentApiFixtures.searchRequest();

            // when
            ShipmentSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).isEmpty();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
        }

        @Test
        @DisplayName("배송 상태 필터를 지정하면 해당 상태만 조회한다")
        void toSearchParams_WithStatuses_ReturnsFiltedStatuses() {
            // given
            List<String> statuses = List.of("SHIPPED", "IN_TRANSIT", "DELIVERED");
            ShipmentSearchApiRequest request =
                    ShipmentApiFixtures.searchRequestWithStatuses(statuses);

            // when
            ShipmentSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactly("SHIPPED", "IN_TRANSIT", "DELIVERED");
        }
    }

    @Nested
    @DisplayName("toResponse() - 목록 단건 변환")
    class ToResponseTest {

        @Test
        @DisplayName("ShipmentListResult를 ShipmentListApiResponse로 변환한다")
        void toResponse_ConvertsListResult_ReturnsApiResponse() {
            // given
            ShipmentListResult result = ShipmentApiFixtures.listResult("SHIP-001");

            // when
            ShipmentListApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.shipment().shipmentId()).isEqualTo("SHIP-001");
            assertThat(response.shipment().shipmentNumber())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_SHIPMENT_NUMBER);
            assertThat(response.productOrder().orderItemId())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.shipment().status()).isEqualTo(ShipmentApiFixtures.DEFAULT_STATUS);
            assertThat(response.shipment().trackingNumber())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_TRACKING_NUMBER);
            assertThat(response.shipment().courierName())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_COURIER_NAME);
            assertThat(response.shipment().shippedAt()).isNotNull();
            assertThat(response.shipment().createdAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            ShipmentListResult result = ShipmentApiFixtures.listResult("SHIP-001");

            // when
            ShipmentListApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.shipment().shippedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            assertThat(response.shipment().createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }

        @Test
        @DisplayName("deliveredAt이 null이면 null을 반환한다")
        void toResponse_NullDeliveredAt_ReturnsNullDeliveredAt() {
            // given
            ShipmentListResult result = ShipmentApiFixtures.listResult("SHIP-001");

            // when
            ShipmentListApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.shipment().deliveredAt()).isNull();
        }

        @Test
        @DisplayName("주문 정보가 올바르게 변환된다")
        void toResponse_ConvertsOrderInfo_ReturnsCorrectOrder() {
            // given
            ShipmentListResult result = ShipmentApiFixtures.listResult("SHIP-001");

            // when
            ShipmentListApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.order().orderId()).isEqualTo(ShipmentApiFixtures.DEFAULT_ORDER_ID);
            assertThat(response.order().orderNumber())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_ORDER_NUMBER);
            assertThat(response.order().buyerName()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("수령인 정보가 올바르게 변환된다")
        void toResponse_ConvertsReceiverInfo_ReturnsCorrectReceiver() {
            // given
            ShipmentListResult result = ShipmentApiFixtures.listResult("SHIP-001");

            // when
            ShipmentListApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.receiver().receiverName()).isEqualTo("김수령");
            assertThat(response.receiver().receiverPhone()).isEqualTo("010-9876-5432");
            assertThat(response.receiver().deliveryRequest()).isEqualTo("문 앞에 놓아주세요");
        }
    }

    @Nested
    @DisplayName("toDetailResponse() - 상세 응답 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("ShipmentDetailResult를 ShipmentDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsDetailResult_ReturnsApiResponse() {
            // given
            ShipmentDetailResult result = ShipmentApiFixtures.detailResult("SHIP-001");

            // when
            ShipmentDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.shipment().shipmentId()).isEqualTo("SHIP-001");
            assertThat(response.shipment().shipmentNumber())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_SHIPMENT_NUMBER);
            assertThat(response.productOrder().orderItemId())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.shipment().status()).isEqualTo(ShipmentApiFixtures.DEFAULT_STATUS);
            assertThat(response.shipment().trackingNumber())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_TRACKING_NUMBER);
        }

        @Test
        @DisplayName("결제 정보가 올바르게 변환된다")
        void toDetailResponse_WithPayment_ReturnsPaymentResponse() {
            // given
            ShipmentDetailResult result = ShipmentApiFixtures.detailResult("SHIP-001");

            // when
            ShipmentDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.payment()).isNotNull();
            assertThat(response.payment().paymentId()).isEqualTo("PAY-001");
            assertThat(response.payment().paymentStatus()).isEqualTo("COMPLETED");
            assertThat(response.payment().paymentMethod()).isEqualTo("CARD");
            assertThat(response.payment().paymentAmount()).isEqualTo(10000);
        }

        @Test
        @DisplayName("결제 정보가 null이면 payment 필드도 null을 반환한다")
        void toDetailResponse_NullPayment_ReturnsNullPayment() {
            // given
            ShipmentDetailResult result =
                    ShipmentApiFixtures.detailResultWithoutPayment("SHIP-001");

            // when
            ShipmentDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.payment()).isNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toDetailResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            ShipmentDetailResult result = ShipmentApiFixtures.detailResult("SHIP-001");

            // when
            ShipmentDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.shipment().orderConfirmedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            assertThat(response.shipment().createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }
    }

    @Nested
    @DisplayName("toSummaryResponse() - 요약 응답 변환")
    class ToSummaryResponseTest {

        @Test
        @DisplayName("ShipmentSummaryResult를 ShipmentSummaryApiResponse로 변환한다")
        void toSummaryResponse_ConvertsSummaryResult_ReturnsApiResponse() {
            // given
            ShipmentSummaryResult result = ShipmentApiFixtures.summaryResult();

            // when
            ShipmentSummaryApiResponse response = mapper.toSummaryResponse(result);

            // then
            assertThat(response.ready()).isEqualTo(10);
            assertThat(response.preparing()).isEqualTo(5);
            assertThat(response.shipped()).isEqualTo(30);
            assertThat(response.inTransit()).isEqualTo(15);
            assertThat(response.delivered()).isEqualTo(100);
            assertThat(response.failed()).isEqualTo(2);
            assertThat(response.cancelled()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("ShipmentPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            ShipmentPageResult pageResult = ShipmentApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<ShipmentListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            ShipmentPageResult pageResult = ShipmentApiFixtures.emptyPageResult();

            // when
            PageApiResponse<ShipmentListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("목록의 각 항목이 올바르게 변환된다")
        void toPageResponse_ConvertsEachItem_ReturnsCorrectItems() {
            // given
            ShipmentPageResult pageResult = ShipmentApiFixtures.pageResult(2, 0, 20);

            // when
            PageApiResponse<ShipmentListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            List<ShipmentListApiResponse> content = response.content();
            assertThat(content.get(0).shipment().shipmentId()).isEqualTo("SHIP-001");
            assertThat(content.get(1).shipment().shipmentId()).isEqualTo("SHIP-002");
        }
    }
}
