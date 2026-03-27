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
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.order.mapper.OrderQueryApiMapper;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryPageResult;
import com.ryuqq.marketplace.application.claimhistory.port.in.query.GetOrderClaimHistoriesUseCase;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
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
    private static final String ORDER_ITEM_ID = OrderApiFixtures.DEFAULT_ORDER_ITEM_ID;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetProductOrderListUseCase getProductOrderListUseCase;
    @MockitoBean private GetOrderDetailUseCase getOrderDetailUseCase;
    @MockitoBean private GetOrderClaimHistoriesUseCase getOrderClaimHistoriesUseCase;
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
            PageApiResponse<OrderListApiResponseV4> pageResponse =
                    OrderApiFixtures.pageApiResponseV4(3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getProductOrderListUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponseV4(any(ProductOrderPageResult.class)))
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
                                    relaxedResponseFields(
                                            // 목록
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품주문 목록 (V4 스펙)"),
                                            // orderId / orderNumber (V4 top-level)
                                            fieldWithPath("data.content[].orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품주문 ID (UUIDv7)"),
                                            fieldWithPath("data.content[].orderNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품주문번호"),
                                            // buyerInfo
                                            fieldWithPath("data.content[].buyerInfo.buyerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자명"),
                                            fieldWithPath("data.content[].buyerInfo.buyerEmail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 이메일"),
                                            fieldWithPath(
                                                            "data.content[].buyerInfo"
                                                                    + ".buyerPhoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 연락처"),
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
                                            fieldWithPath("data.content[].payment.billAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("청구 금액 (원)"),
                                            fieldWithPath("data.content[].payment.paymentAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실결제 금액 (원)"),
                                            // cancel summary
                                            fieldWithPath("data.content[].cancel.hasActiveCancel")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("진행 중인 취소 존재 여부"),
                                            fieldWithPath(
                                                            "data.content[].cancel"
                                                                    + ".totalCancelledQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 취소 수량"),
                                            // claim summary
                                            fieldWithPath("data.content[].claim.hasActiveClaim")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("진행 중인 클레임 존재 여부"),
                                            fieldWithPath("data.content[].claim.activeCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("진행 중인 클레임 수"),
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
            PageApiResponse<OrderListApiResponseV4> pageResponse =
                    OrderApiFixtures.pageApiResponseV4(1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getProductOrderListUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponseV4(any(ProductOrderPageResult.class)))
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
            PageApiResponse<OrderListApiResponseV4> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getProductOrderListUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponseV4(any(ProductOrderPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("주문 클레임 이력 조회 API")
    class GetClaimHistoriesTest {

        @Test
        @DisplayName("주문 클레임 이력 조회 성공 - 200과 페이지 응답을 반환한다")
        void getClaimHistories_Success() throws Exception {
            // given
            ClaimHistoryPageResult pageResult = OrderApiFixtures.claimHistoryPageResult(2, 0, 20);
            PageApiResponse<ClaimHistoryApiResponse> pageResponse =
                    OrderApiFixtures.claimHistoryPageApiResponse(2);

            given(mapper.toClaimHistoryCriteria(any(), any())).willReturn(null);
            given(getOrderClaimHistoriesUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toClaimHistoryPageResponse(any(ClaimHistoryPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                            BASE_URL + OrderAdminEndpoints.HISTORIES, ORDER_ITEM_ID)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(2))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(2))
                    .andDo(
                            document(
                                    "order/claim-histories",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("orderItemId")
                                                    .description("상품주문 ID (UUIDv7)")),
                                    queryParameters(
                                            parameterWithName("claimType")
                                                    .description(
                                                            "클레임 타입 필터. ORDER: 주문 메모,"
                                                                + " CANCEL: 취소, REFUND: 환불,"
                                                                + " EXCHANGE: 교환. 미전송 시 전체 조회")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터). 기본값: 0")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기. 기본값: 20")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("클레임 이력 목록"),
                                            fieldWithPath("data.content[].historyId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이력 ID"),
                                            fieldWithPath("data.content[].type")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "이력 유형 (STATUS_CHANGE: 상태 변경,"
                                                                    + " MANUAL: 수기 메모)"),
                                            fieldWithPath("data.content[].title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("제목"),
                                            fieldWithPath("data.content[].message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("내용"),
                                            fieldWithPath("data.content[].actor")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("처리자 정보"),
                                            fieldWithPath("data.content[].actor.actorType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "처리자 유형 (SELLER, ADMIN, SYSTEM 등)"),
                                            fieldWithPath("data.content[].actor.actorId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리자 ID"),
                                            fieldWithPath("data.content[].actor.actorName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리자 이름"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
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
        @DisplayName("claimType 필터를 함께 사용할 수 있다")
        void getClaimHistories_WithClaimTypeFilter_Returns200() throws Exception {
            // given
            ClaimHistoryPageResult pageResult = OrderApiFixtures.claimHistoryPageResult(1, 0, 20);
            PageApiResponse<ClaimHistoryApiResponse> pageResponse =
                    OrderApiFixtures.claimHistoryPageApiResponse(1);

            given(mapper.toClaimHistoryCriteria(any(), any())).willReturn(null);
            given(getOrderClaimHistoriesUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toClaimHistoryPageResponse(any(ClaimHistoryPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                            BASE_URL + OrderAdminEndpoints.HISTORIES, ORDER_ITEM_ID)
                                    .param("claimType", "CANCEL")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void getClaimHistories_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            ClaimHistoryPageResult emptyResult = OrderApiFixtures.emptyClaimHistoryPageResult();
            PageApiResponse<ClaimHistoryApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toClaimHistoryCriteria(any(), any())).willReturn(null);
            given(getOrderClaimHistoriesUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toClaimHistoryPageResponse(any(ClaimHistoryPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + OrderAdminEndpoints.HISTORIES, ORDER_ITEM_ID))
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
                    .andExpect(
                            jsonPath("$.data.orderId")
                                    .value(OrderApiFixtures.DEFAULT_ORDER_ITEM_ID))
                    .andExpect(jsonPath("$.data.buyerInfo").exists())
                    .andExpect(jsonPath("$.data.orderProducts").isArray())
                    .andExpect(jsonPath("$.data.histories").isArray())
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
                                            fieldWithPath("data.orderProducts")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("주문 상품 정보 목록"),
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
                                            fieldWithPath("data.histories")
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
}
