package com.ryuqq.marketplace.adapter.in.rest.exchange.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.exchange.ExchangeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.exchange.ExchangeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.mapper.ExchangeApiMapper;
import com.ryuqq.marketplace.application.exchange.port.in.query.GetExchangeDetailUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.query.GetExchangeListUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.query.GetExchangeSummaryUseCase;
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
@WebMvcTest(ExchangeQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ExchangeQueryController REST Docs 테스트")
class ExchangeQueryControllerRestDocsTest {

    private static final String EXCHANGES_URL = ExchangeAdminEndpoints.EXCHANGES;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GetExchangeSummaryUseCase getExchangeSummaryUseCase;
    @MockitoBean private GetExchangeListUseCase getExchangeListUseCase;
    @MockitoBean private GetExchangeDetailUseCase getExchangeDetailUseCase;
    @MockitoBean private ExchangeApiMapper mapper;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.common.mapper.ClaimOrderEnricher
            claimOrderEnricher;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("교환 상태별 요약 조회 API")
    class GetSummaryTest {

        @Test
        @DisplayName("교환 상태별 요약 조회 성공")
        void getSummary_Success() throws Exception {
            // given
            ExchangeSummaryApiResponse summaryResponse = ExchangeApiFixtures.summaryApiResponse();

            given(getExchangeSummaryUseCase.execute())
                    .willReturn(ExchangeApiFixtures.summaryResult());
            given(mapper.toSummaryResponse(any())).willReturn(summaryResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    EXCHANGES_URL + ExchangeAdminEndpoints.SUMMARY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.requested").value(5))
                    .andExpect(jsonPath("$.data.collecting").value(3))
                    .andExpect(jsonPath("$.data.collected").value(2))
                    .andExpect(jsonPath("$.data.preparing").value(4))
                    .andExpect(jsonPath("$.data.shipping").value(1))
                    .andExpect(jsonPath("$.data.completed").value(10))
                    .andExpect(jsonPath("$.data.rejected").value(2))
                    .andExpect(jsonPath("$.data.cancelled").value(1))
                    .andDo(
                            document(
                                    "exchange/get-summary",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("data.requested")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 요청 건수"),
                                            fieldWithPath("data.collecting")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수거 중 건수"),
                                            fieldWithPath("data.collected")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수거 완료 건수"),
                                            fieldWithPath("data.preparing")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 준비 중 건수"),
                                            fieldWithPath("data.shipping")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재발송 중 건수"),
                                            fieldWithPath("data.completed")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 완료 건수"),
                                            fieldWithPath("data.rejected")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 거절 건수"),
                                            fieldWithPath("data.cancelled")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 취소 건수"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("교환 목록 조회 API")
    class GetListTest {

        @Test
        @DisplayName("교환 목록 조회 성공")
        void getList_Success() throws Exception {
            // given
            PageApiResponse<?> pageResponse = ExchangeApiFixtures.pageApiResponse(2);

            given(getExchangeListUseCase.execute(any()))
                    .willReturn(ExchangeApiFixtures.pageResult(2, 0, 20));
            given(mapper.toSearchParams(any())).willReturn(null);
            given(mapper.toPageResponseV4(any(), any())).willReturn((PageApiResponse) pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(EXCHANGES_URL)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(
                            jsonPath("$.data.content[0].exchangeClaimId")
                                    .value(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID))
                    .andExpect(
                            jsonPath("$.data.content[0].exchangeStatus")
                                    .value(ExchangeApiFixtures.DEFAULT_EXCHANGE_STATUS))
                    .andExpect(jsonPath("$.data.totalElements").value(2))
                    .andDo(
                            document(
                                    "exchange/get-list",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("page")
                                                    .description("페이지 번호 (기본값: 0)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20)")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description(
                                                            "교환 상태 목록 필터 (REQUESTED, COLLECTING,"
                                                                + " COLLECTED, PREPARING, SHIPPING,"
                                                                + " COMPLETED, REJECTED,"
                                                                + " CANCELLED)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (exchangeClaimId, claimNumber,"
                                                                    + " orderId 등)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("dateField")
                                                    .description(
                                                            "날짜 필드 (REQUESTED_AT, PROCESSED_AT 등)")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("검색 시작일 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("검색 종료일 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description("정렬 기준 필드")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("교환 목록"),
                                            fieldWithPath("data.content[].exchangeClaimId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 클레임 ID (UUIDv7)"),
                                            fieldWithPath("data.content[].claimNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 클레임 번호"),
                                            fieldWithPath("data.content[].orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "주문 ID (프론트: orderId = 내부"
                                                                    + " orderItemId)"),
                                            fieldWithPath("data.content[].exchangeQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 수량"),
                                            fieldWithPath("data.content[].exchangeStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 상태"),
                                            fieldWithPath("data.content[].reasonType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 사유 유형"),
                                            fieldWithPath("data.content[].reasonDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 상세 사유"),
                                            fieldWithPath("data.content[].targetSkuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 대상 SKU 코드"),
                                            fieldWithPath("data.content[].targetQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 대상 수량"),
                                            fieldWithPath("data.content[].linkedOrderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("연결 주문 ID"),
                                            fieldWithPath("data.content[].requestedBy")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청자"),
                                            fieldWithPath("data.content[].processedBy")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리자"),
                                            fieldWithPath("data.content[].requestedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청일시 (ISO 8601, KST)"),
                                            fieldWithPath("data.content[].processedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리일시 (ISO 8601, KST)"),
                                            fieldWithPath("data.content[].completedAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description(
                                                            "완료일시 (ISO 8601, KST) - 미완료 시 null")
                                                    .optional(),
                                            fieldWithPath("data.page")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 번호"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 항목 수"),
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
        @DisplayName("교환 목록 조회 - 결과 없음")
        void getList_Empty() throws Exception {
            // given
            PageApiResponse<?> emptyPageResponse =
                    PageApiResponse.of(java.util.List.of(), 0, 20, 0);

            given(getExchangeListUseCase.execute(any()))
                    .willReturn(ExchangeApiFixtures.emptyPageResult());
            given(mapper.toSearchParams(any())).willReturn(null);
            given(mapper.toPageResponseV4(any(), any()))
                    .willReturn((PageApiResponse) emptyPageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(EXCHANGES_URL)
                                    .param("statuses", "COMPLETED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("교환 상세 조회 API")
    class GetDetailTest {

        @Test
        @DisplayName("교환 상세 조회 성공")
        void getDetail_Success() throws Exception {
            // given
            String exchangeClaimId = ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID;
            ExchangeDetailApiResponse detailResponse = ExchangeApiFixtures.detailApiResponse();

            given(getExchangeDetailUseCase.execute(exchangeClaimId))
                    .willReturn(ExchangeApiFixtures.detailResult());
            given(mapper.toDetailResponse(any(), any(), any(), any(), any()))
                    .willReturn(detailResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    EXCHANGES_URL + ExchangeAdminEndpoints.EXCHANGE_CLAIM_ID,
                                    exchangeClaimId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.claimInfo.exchangeClaimId").value(exchangeClaimId))
                    .andExpect(
                            jsonPath("$.data.claimInfo.claimNumber")
                                    .value(ExchangeApiFixtures.DEFAULT_CLAIM_NUMBER))
                    .andExpect(
                            jsonPath("$.data.orderId")
                                    .value(ExchangeApiFixtures.DEFAULT_ORDER_ITEM_ID))
                    .andExpect(
                            jsonPath("$.data.claimInfo.status")
                                    .value(ExchangeApiFixtures.DEFAULT_EXCHANGE_STATUS))
                    .andExpect(jsonPath("$.data.claimInfo.exchangeOption").exists())
                    .andExpect(jsonPath("$.data.claimInfo.amountAdjustment").exists())
                    .andExpect(jsonPath("$.data.claimInfo.collectShipment").exists())
                    .andExpect(jsonPath("$.data.claimHistories").isArray())
                    .andDo(
                            document(
                                    "exchange/get-detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("exchangeClaimId")
                                                    .description("교환 클레임 ID (UUIDv7)")),
                                    responseFields(
                                            fieldWithPath("data.orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "주문 ID (프론트: orderId = 내부"
                                                                    + " orderItemId)"),
                                            fieldWithPath("data.orderProducts")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("주문 상품 정보 목록")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("교환 클레임 정보"),
                                            fieldWithPath("data.claimInfo.exchangeClaimId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 클레임 ID (UUIDv7)"),
                                            fieldWithPath("data.claimInfo.claimNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 클레임 번호"),
                                            fieldWithPath("data.claimInfo.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매자 ID"),
                                            fieldWithPath("data.claimInfo.exchangeQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 수량"),
                                            fieldWithPath("data.claimInfo.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 상태"),
                                            fieldWithPath("data.claimInfo.reason")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("교환 사유"),
                                            fieldWithPath("data.claimInfo.reason.reasonType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 사유 유형"),
                                            fieldWithPath("data.claimInfo.reason.reasonDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 상세 사유"),
                                            fieldWithPath("data.claimInfo.exchangeOption")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("교환 옵션 정보 (없을 경우 null)")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.originalProductId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("원 상품 ID")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.originalSkuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원 SKU 코드")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.targetProductGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 대상 상품 그룹 ID")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.targetProductId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 대상 상품 ID")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.targetSkuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 대상 SKU 코드")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.exchangeOption.quantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 대상 수량")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.originalOption")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("원 옵션 정보")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.originalOption.optionName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원 옵션명")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.originalOption.optionValues")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("원 옵션값 목록")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.originalOption.optionValues[].name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 속성명")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.originalOption.optionValues[].value")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 속성값")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.targetOption")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("교환 대상 옵션 정보")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.targetOption.optionName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 대상 옵션명")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.targetOption.optionValues")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("교환 대상 옵션값 목록")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.targetOption.optionValues[].name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 대상 옵션 속성명")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.exchangeOption.targetOption.optionValues[].value")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 대상 옵션 속성값")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.amountAdjustment")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("금액 조정 정보 (없을 경우 null)")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.amountAdjustment.originalPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("원 가격")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.amountAdjustment.targetPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 대상 가격")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.amountAdjustment.priceDifference")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("가격 차액")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.amountAdjustment.additionalPaymentRequired")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("추가 결제 필요 여부")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.amountAdjustment.partialRefundRequired")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("부분 환불 필요 여부")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.amountAdjustment.collectShippingFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수거 배송비")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.amountAdjustment.reshipShippingFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재발송 배송비")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.amountAdjustment.totalShippingFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 배송비")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.amountAdjustment.shippingFeePayer")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송비 부담자")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.collectShipment")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("수거 배송 정보")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.collectShipment.method")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("수거 방법 정보")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.collectShipment.method.type")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 방식 유형")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.collectShipment.method.courierCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사 코드")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.collectShipment.method.courierName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사명")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.collectShipment.feeInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("수거 배송비 정보")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.collectShipment.feeInfo.amount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송비 금액")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.collectShipment.feeInfo.payer")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송비 부담 주체")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.collectShipment.trackingNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수거 송장번호")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.collectShipment.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수거 상태")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.linkedOrderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("연결 주문 ID"),
                                            fieldWithPath("data.claimInfo.requestedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청일시 (ISO 8601, KST)"),
                                            fieldWithPath("data.claimInfo.completedAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description(
                                                            "완료일시 (ISO 8601, KST) - 미완료 시 null")
                                                    .optional(),
                                            fieldWithPath("data.buyerInfo")
                                                    .type(JsonFieldType.NULL)
                                                    .description("구매자 정보")
                                                    .optional(),
                                            fieldWithPath("data.payment")
                                                    .type(JsonFieldType.NULL)
                                                    .description("결제 정보")
                                                    .optional(),
                                            fieldWithPath("data.receiverInfo")
                                                    .type(JsonFieldType.NULL)
                                                    .description("수령인 정보")
                                                    .optional(),
                                            fieldWithPath("data.requestedBy")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청자"),
                                            fieldWithPath("data.processedBy")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리자"),
                                            fieldWithPath("data.processedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리일시 (ISO 8601, KST)"),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시 (ISO 8601, KST)"),
                                            fieldWithPath("data.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시 (ISO 8601, KST)"),
                                            fieldWithPath("data.claimHistories")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("클레임 이력 목록"),
                                            fieldWithPath("data.claimHistories[].historyId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이력 ID"),
                                            fieldWithPath("data.claimHistories[].type")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이력 유형 (STATUS_CHANGE, MEMO 등)"),
                                            fieldWithPath("data.claimHistories[].title")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이력 제목"),
                                            fieldWithPath("data.claimHistories[].message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이력 메시지"),
                                            fieldWithPath("data.claimHistories[].actor")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("처리 주체 정보"),
                                            fieldWithPath("data.claimHistories[].actor.actorType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "처리 주체 유형 (SELLER, ADMIN, SYSTEM)"),
                                            fieldWithPath("data.claimHistories[].actor.actorId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 주체 ID"),
                                            fieldWithPath("data.claimHistories[].actor.actorName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 주체 이름"),
                                            fieldWithPath("data.claimHistories[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이력 생성일시 (ISO 8601, KST)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
