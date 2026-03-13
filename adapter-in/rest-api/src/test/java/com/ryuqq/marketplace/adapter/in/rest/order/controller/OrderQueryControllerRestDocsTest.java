package com.ryuqq.marketplace.adapter.in.rest.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.mapper.OrderQueryApiMapper;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderSummaryUseCase;
import com.ryuqq.marketplace.application.order.port.in.query.GetProductOrderListUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(OrderQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("OrderQueryController REST Docs 테스트")
class OrderQueryControllerRestDocsTest {

    private static final String BASE_URL = OrderAdminEndpoints.ORDERS;
    private static final long ORDER_ITEM_ID = OrderApiFixtures.DEFAULT_ORDER_ITEM_ID;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetProductOrderListUseCase getProductOrderListUseCase;
    @MockitoBean private GetOrderDetailUseCase getOrderDetailUseCase;
    @MockitoBean private GetOrderSummaryUseCase getOrderSummaryUseCase;
    @MockitoBean private OrderQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("상품주문 목록 조회 API")
    class SearchOrdersTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchOrders_ValidRequest_Returns200WithPage() throws Exception {
            // given
            ProductOrderPageResult pageResult = OrderApiFixtures.productOrderPageResult(3, 0, 20);
            PageApiResponse<OrderListApiResponse> pageResponse =
                    OrderApiFixtures.pageApiResponse(3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getProductOrderListUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ProductOrderPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andExpect(
                            jsonPath("$.data.content[0].payment.paymentId")
                                    .value(OrderApiFixtures.DEFAULT_PAYMENT_ID))
                    .andExpect(
                            jsonPath("$.data.content[0].payment.paymentNumber")
                                    .value(OrderApiFixtures.DEFAULT_PAYMENT_NUMBER))
                    .andDo(
                            document(
                                    "order/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("dateField")
                                                    .description(
                                                            "날짜 검색 대상 (ORDERED, SHIPPED,"
                                                                    + " DELIVERED)")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("시작일 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("종료일 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("status")
                                                    .description("주문 상태 필터")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (ORDER_ID, ORDER_NUMBER,"
                                                                + " CUSTOMER_NAME, PRODUCT_NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT, ORDERED_AT,"
                                                                + " UPDATED_AT). 기본값: CREATED_AT")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC). 기본값: DESC")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터). 기본값: 0")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기. 기본값: 20")
                                                    .optional()),
                                    responseFields(
                                            // 목록
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품주문 목록"),
                                            // order
                                            fieldWithPath("data.content[].order.orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문 ID (UUIDv7)"),
                                            fieldWithPath("data.content[].order.orderNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문번호"),
                                            fieldWithPath("data.content[].order.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문 상태"),
                                            fieldWithPath("data.content[].order.salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.content[].order.shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("샵 ID"),
                                            fieldWithPath("data.content[].order.shopCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("샵 코드"),
                                            fieldWithPath("data.content[].order.shopName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("샵 이름"),
                                            fieldWithPath("data.content[].order.externalOrderNo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부몰 주문번호"),
                                            fieldWithPath("data.content[].order.externalOrderedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부몰 주문일시 (ISO 8601)"),
                                            fieldWithPath("data.content[].order.buyerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자명"),
                                            fieldWithPath("data.content[].order.buyerEmail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 이메일"),
                                            fieldWithPath("data.content[].order.buyerPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 연락처"),
                                            fieldWithPath("data.content[].order.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문 생성일시 (ISO 8601)"),
                                            fieldWithPath("data.content[].order.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문 수정일시 (ISO 8601)"),
                                            // productOrder
                                            fieldWithPath("data.content[].productOrder.orderItemId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품주문 ID"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품그룹 ID"),
                                            fieldWithPath("data.content[].productOrder.productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID (SKU)"),
                                            fieldWithPath("data.content[].productOrder.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매자 ID"),
                                            fieldWithPath("data.content[].productOrder.brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.content[].productOrder.skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품명"),
                                            fieldWithPath("data.content[].productOrder.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.content[].productOrder.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("판매자명"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".mainImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표 이미지 URL"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".externalProductId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품 ID"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".externalOptionId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 옵션 ID"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".externalProductName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품명"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".externalOptionName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 옵션명"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".externalImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 이미지 URL"),
                                            fieldWithPath("data.content[].productOrder.unitPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("개당 판매가 (원)"),
                                            fieldWithPath("data.content[].productOrder.quantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("주문 수량"),
                                            fieldWithPath("data.content[].productOrder.totalAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 금액"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".discountAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인 금액"),
                                            fieldWithPath(
                                                            "data.content[].productOrder"
                                                                    + ".paymentAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실결제 금액"),
                                            // payment
                                            fieldWithPath("data.content[].payment.paymentId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제 ID (UUIDv7)"),
                                            fieldWithPath("data.content[].payment.paymentNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제 번호 (PAY-YYYYMMDD-XXXX)"),
                                            fieldWithPath("data.content[].payment.paymentStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제 상태"),
                                            fieldWithPath("data.content[].payment.paymentMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제 수단"),
                                            fieldWithPath("data.content[].payment.paymentAgencyId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("PG사 거래 ID"),
                                            fieldWithPath("data.content[].payment.paymentAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("결제 금액 (원)"),
                                            fieldWithPath("data.content[].payment.paidAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제일시 (ISO 8601)"),
                                            fieldWithPath("data.content[].payment.canceledAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description("취소일시 (ISO 8601)")
                                                    .optional(),
                                            // receiver
                                            fieldWithPath("data.content[].receiver.receiverName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인명"),
                                            fieldWithPath("data.content[].receiver.receiverPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 연락처"),
                                            fieldWithPath("data.content[].receiver.receiverZipcode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호"),
                                            fieldWithPath("data.content[].receiver.receiverAddress")
                                                    .type(JsonFieldType.STRING)
                                                    .description("기본 주소"),
                                            fieldWithPath(
                                                            "data.content[].receiver"
                                                                    + ".receiverAddressDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세 주소"),
                                            fieldWithPath("data.content[].receiver.deliveryRequest")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 요청사항"),
                                            // delivery
                                            fieldWithPath("data.content[].delivery.deliveryStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 상태"),
                                            fieldWithPath(
                                                            "data.content[].delivery"
                                                                    + ".shipmentCompanyCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("택배사 코드")
                                                    .optional(),
                                            fieldWithPath("data.content[].delivery.invoice")
                                                    .type(JsonFieldType.NULL)
                                                    .description("송장번호")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.content[].delivery"
                                                                    + ".shipmentCompletedDate")
                                                    .type(JsonFieldType.NULL)
                                                    .description("출고완료일시 (ISO 8601)")
                                                    .optional(),
                                            // cancel
                                            fieldWithPath("data.content[].cancel.hasActiveCancel")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("진행 중인 취소 존재 여부"),
                                            fieldWithPath(
                                                            "data.content[].cancel"
                                                                    + ".totalCancelledQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 취소 수량"),
                                            fieldWithPath("data.content[].cancel.cancelableQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("추가 취소 가능 수량"),
                                            fieldWithPath("data.content[].cancel.latest")
                                                    .type(JsonFieldType.NULL)
                                                    .description("가장 최근 취소 정보")
                                                    .optional(),
                                            // claim
                                            fieldWithPath("data.content[].claim.hasActiveClaim")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("진행 중인 클레임 존재 여부"),
                                            fieldWithPath("data.content[].claim.activeCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("진행 중인 클레임 수"),
                                            fieldWithPath("data.content[].claim.totalClaimedQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 클레임 수량"),
                                            fieldWithPath("data.content[].claim.claimableQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("추가 클레임 가능 수량"),
                                            fieldWithPath("data.content[].claim.latest")
                                                    .type(JsonFieldType.NULL)
                                                    .description("가장 최근 클레임 정보")
                                                    .optional(),
                                            // page meta
                                            fieldWithPath("data.page")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 번호"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("상태 필터와 검색어를 함께 사용할 수 있다")
        void searchOrders_WithFilters_Returns200() throws Exception {
            // given
            ProductOrderPageResult pageResult = OrderApiFixtures.productOrderPageResult(1, 0, 20);
            PageApiResponse<OrderListApiResponse> pageResponse =
                    OrderApiFixtures.pageApiResponse(1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getProductOrderListUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ProductOrderPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("status", "PREPARING")
                                    .param("searchField", "ORDER_NUMBER")
                                    .param("searchWord", "ORD-001")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchOrders_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            ProductOrderPageResult emptyResult = OrderApiFixtures.emptyPageResult();
            PageApiResponse<OrderListApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getProductOrderListUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(ProductOrderPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("상품주문 상세 조회 API")
    class GetOrderDetailTest {

        @Test
        @DisplayName("상품주문 상세 조회 성공 (V4 스펙)")
        void getOrderDetail_Success() throws Exception {
            // given
            ProductOrderDetailResult detailResult = OrderApiFixtures.productOrderDetailResult();

            given(getOrderDetailUseCase.execute(ORDER_ITEM_ID)).willReturn(detailResult);
            given(mapper.toDetailResponseV4(any(ProductOrderDetailResult.class)))
                    .willAnswer(
                            inv -> {
                                OrderQueryApiMapper realMapper = new OrderQueryApiMapper();
                                return realMapper.toDetailResponseV4(inv.getArgument(0));
                            });

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + OrderAdminEndpoints.ORDER_ITEM_ID, ORDER_ITEM_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.orderId").value(OrderApiFixtures.DEFAULT_ORDER_ID))
                    .andExpect(jsonPath("$.data.buyerInfo").exists())
                    .andExpect(jsonPath("$.data.settlementInfo").exists())
                    .andExpect(jsonPath("$.data.orderProduct").exists())
                    .andExpect(jsonPath("$.data.orderHistories").isArray())
                    .andExpect(jsonPath("$.data.cancelIds").isArray())
                    .andExpect(jsonPath("$.data.cancels").isArray())
                    .andExpect(jsonPath("$.data.claimIds").isArray())
                    .andExpect(jsonPath("$.data.claims").isArray())
                    .andDo(
                            document(
                                    "order/detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("orderItemId")
                                                    .description("상품주문 ID")),
                                    relaxedResponseFields(
                                            // V4 구조 (주요 필드만 문서화)
                                            fieldWithPath("data.orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문 ID (UUIDv7)"),
                                            fieldWithPath("data.orderNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문번호"),
                                            fieldWithPath("data.buyerInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("구매자 정보"),
                                            fieldWithPath("data.payment")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("결제 정보"),
                                            fieldWithPath("data.receiverInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("수령인 정보"),
                                            fieldWithPath("data.paymentShipmentInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("배송 정보"),
                                            fieldWithPath("data.settlementInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("정산 정보"),
                                            fieldWithPath("data.orderProduct")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("주문 상품 정보"),
                                            fieldWithPath("data.externalOrderInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("외부몰 주문 정보")
                                                    .optional(),
                                            fieldWithPath("data.cancel")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("취소 요약")
                                                    .optional(),
                                            fieldWithPath("data.claim")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("클레임 요약")
                                                    .optional(),
                                            fieldWithPath("data.orderHistories")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("주문 상태 변경 이력"),
                                            fieldWithPath("data.cancelIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("취소 ID 목록"),
                                            fieldWithPath("data.cancels")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("취소 상세 목록 (최근 3개)"),
                                            fieldWithPath("data.claimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("클레임 ID 목록"),
                                            fieldWithPath("data.claims")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("클레임 상세 목록 (최근 3개)"),
                                            // common
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("주문 상태별 요약 조회 API")
    class GetOrderSummaryTest {

        @Test
        @DisplayName("주문 상태별 요약 조회 성공")
        void getSummary_Success() throws Exception {
            // given
            OrderSummaryResult summaryResult = OrderApiFixtures.orderSummaryResult();
            OrderSummaryApiResponse summaryResponse = OrderApiFixtures.orderSummaryApiResponse();

            given(getOrderSummaryUseCase.execute()).willReturn(summaryResult);
            given(mapper.toSummaryResponse(any(OrderSummaryResult.class)))
                    .willReturn(summaryResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + OrderAdminEndpoints.SUMMARY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.ordered").value(10))
                    .andExpect(jsonPath("$.data.preparing").value(5))
                    .andExpect(jsonPath("$.data.shipped").value(30))
                    .andExpect(jsonPath("$.data.delivered").value(15))
                    .andExpect(jsonPath("$.data.confirmed").value(8))
                    .andExpect(jsonPath("$.data.cancelled").value(3))
                    .andExpect(jsonPath("$.data.claimInProgress").value(2))
                    .andExpect(jsonPath("$.data.refunded").value(4))
                    .andExpect(jsonPath("$.data.exchanged").value(1))
                    .andDo(
                            document(
                                    "order/summary",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("data.ordered")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("주문접수 건수"),
                                            fieldWithPath("data.preparing")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("발주확인 건수"),
                                            fieldWithPath("data.shipped")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("출고완료 건수"),
                                            fieldWithPath("data.delivered")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송완료 건수"),
                                            fieldWithPath("data.confirmed")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("구매확정 건수"),
                                            fieldWithPath("data.cancelled")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 건수"),
                                            fieldWithPath("data.claimInProgress")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("클레임 진행중 건수"),
                                            fieldWithPath("data.refunded")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불완료 건수"),
                                            fieldWithPath("data.exchanged")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환완료 건수"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
