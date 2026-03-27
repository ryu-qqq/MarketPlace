package com.ryuqq.marketplace.adapter.in.rest.refund.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.refund.RefundAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.refund.RefundApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.mapper.RefundApiMapper;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundDetailUseCase;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundListUseCase;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundSummaryUseCase;
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
@WebMvcTest(RefundQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("RefundQueryController REST Docs 테스트")
class RefundQueryControllerRestDocsTest {

    private static final String REFUNDS_URL = RefundAdminEndpoints.REFUNDS;
    private static final String DEFAULT_REFUND_CLAIM_ID = RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GetRefundSummaryUseCase getRefundSummaryUseCase;
    @MockitoBean private GetRefundListUseCase getRefundListUseCase;
    @MockitoBean private GetRefundDetailUseCase getRefundDetailUseCase;
    @MockitoBean private RefundApiMapper mapper;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.common.mapper.ClaimOrderEnricher
            claimOrderEnricher;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("환불 상태별 요약 조회 API")
    class GetSummaryTest {

        @Test
        @DisplayName("환불 상태별 요약 조회 성공")
        void getSummary_Success() throws Exception {
            // given
            RefundSummaryApiResponse summaryResponse = RefundApiFixtures.summaryApiResponse();

            given(getRefundSummaryUseCase.execute()).willReturn(RefundApiFixtures.summaryResult());
            given(mapper.toSummaryResponse(any())).willReturn(summaryResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    REFUNDS_URL + RefundAdminEndpoints.SUMMARY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.requested").value(10))
                    .andExpect(jsonPath("$.data.collecting").value(5))
                    .andExpect(jsonPath("$.data.collected").value(3))
                    .andExpect(jsonPath("$.data.completed").value(20))
                    .andExpect(jsonPath("$.data.rejected").value(2))
                    .andExpect(jsonPath("$.data.cancelled").value(1))
                    .andDo(
                            document(
                                    "refund/get-summary",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("data.requested")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 요청 건수"),
                                            fieldWithPath("data.collecting")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수거 중 건수"),
                                            fieldWithPath("data.collected")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수거 완료 건수"),
                                            fieldWithPath("data.completed")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 완료 건수"),
                                            fieldWithPath("data.rejected")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 거절 건수"),
                                            fieldWithPath("data.cancelled")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 취소 건수"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("환불 목록 조회 API")
    class GetListTest {

        @Test
        @DisplayName("환불 목록 조회 성공")
        void getList_Success() throws Exception {
            // given
            PageApiResponse<?> pageResponse = RefundApiFixtures.pageApiResponse(2);

            given(getRefundListUseCase.execute(any()))
                    .willReturn(RefundApiFixtures.pageResult(2, 0, 20));
            given(mapper.toSearchParams(any())).willReturn(null);
            given(mapper.toPageResponseV4(any(), any())).willReturn((PageApiResponse) pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(REFUNDS_URL)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].refundClaimId").exists())
                    .andExpect(
                            jsonPath("$.data.content[0].refundStatus")
                                    .value(RefundApiFixtures.DEFAULT_REFUND_STATUS))
                    .andExpect(jsonPath("$.data.totalElements").value(2))
                    .andDo(
                            document(
                                    "refund/get-list",
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
                                                            "환불 상태 목록 필터 (REQUESTED, COLLECTING,"
                                                                + " COLLECTED, COMPLETED, REJECTED,"
                                                                + " CANCELLED)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (refundClaimId, claimNumber,"
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
                                                    .description("환불 목록"),
                                            fieldWithPath("data.content[].refundClaimId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 클레임 ID (UUIDv7)"),
                                            fieldWithPath("data.content[].claimNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 클레임 번호"),
                                            fieldWithPath("data.content[].orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "주문 ID (프론트: orderId = 내부"
                                                                    + " orderItemId)"),
                                            fieldWithPath("data.content[].refundQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 수량"),
                                            fieldWithPath("data.content[].refundStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 상태"),
                                            fieldWithPath("data.content[].reasonType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 사유 유형"),
                                            fieldWithPath("data.content[].reasonDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 상세 사유"),
                                            fieldWithPath("data.content[].originalAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("원래 금액"),
                                            fieldWithPath("data.content[].finalAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최종 환불 금액"),
                                            fieldWithPath("data.content[].refundMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 방식"),
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
                                                    .type(JsonFieldType.NULL)
                                                    .description(
                                                            "처리일시 (ISO 8601, KST) - 미처리 시 null")
                                                    .optional(),
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
        @DisplayName("환불 목록 조회 - 결과 없음")
        void getList_Empty() throws Exception {
            // given
            PageApiResponse<?> emptyPageResponse =
                    PageApiResponse.of(java.util.List.of(), 0, 20, 0);

            given(getRefundListUseCase.execute(any()))
                    .willReturn(RefundApiFixtures.emptyPageResult());
            given(mapper.toSearchParams(any())).willReturn(null);
            given(mapper.toPageResponseV4(any(), any()))
                    .willReturn((PageApiResponse) emptyPageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(REFUNDS_URL)
                                    .param("statuses", "COMPLETED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("환불 상세 조회 API")
    class GetDetailTest {

        @Test
        @DisplayName("환불 상세 조회 성공")
        void getDetail_Success() throws Exception {
            // given
            RefundDetailApiResponse detailResponse =
                    RefundApiFixtures.detailApiResponse(DEFAULT_REFUND_CLAIM_ID);

            given(getRefundDetailUseCase.execute(DEFAULT_REFUND_CLAIM_ID))
                    .willReturn(RefundApiFixtures.detailResult(DEFAULT_REFUND_CLAIM_ID));
            given(mapper.toDetailResponse(any(), any(), any(), any(), any()))
                    .willReturn(detailResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    REFUNDS_URL + RefundAdminEndpoints.REFUND_CLAIM_ID,
                                    DEFAULT_REFUND_CLAIM_ID))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.claimInfo.refundClaimId")
                                    .value(DEFAULT_REFUND_CLAIM_ID))
                    .andExpect(
                            jsonPath("$.data.claimInfo.claimNumber")
                                    .value(RefundApiFixtures.DEFAULT_CLAIM_NUMBER))
                    .andExpect(
                            jsonPath("$.data.orderId")
                                    .value(RefundApiFixtures.DEFAULT_ORDER_ITEM_ID))
                    .andExpect(
                            jsonPath("$.data.claimInfo.status")
                                    .value(RefundApiFixtures.DEFAULT_REFUND_STATUS))
                    .andExpect(jsonPath("$.data.claimInfo.refundInfo").exists())
                    .andExpect(jsonPath("$.data.claimInfo.collectShipment").exists())
                    .andExpect(jsonPath("$.data.claimHistories").isArray())
                    .andDo(
                            document(
                                    "refund/get-detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("refundClaimId")
                                                    .description("환불 클레임 ID (UUIDv7)")),
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
                                                    .description("환불 클레임 정보"),
                                            fieldWithPath("data.claimInfo.refundClaimId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 클레임 ID (UUIDv7)"),
                                            fieldWithPath("data.claimInfo.claimNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 클레임 번호"),
                                            fieldWithPath("data.claimInfo.refundQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 수량"),
                                            fieldWithPath("data.claimInfo.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 상태"),
                                            fieldWithPath("data.claimInfo.reasonType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 사유 유형"),
                                            fieldWithPath("data.claimInfo.reasonDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 상세 사유"),
                                            fieldWithPath("data.claimInfo.refundInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("환불 금액 정보 (없을 경우 null)")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.refundInfo.originalAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("원래 금액")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.refundInfo.finalAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최종 환불 금액")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.refundInfo.deductionAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("차감 금액")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.claimInfo.refundInfo.deductionReason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("차감 사유")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.refundInfo.refundMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 방식")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.refundInfo.refundedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 완료일시 (ISO 8601, KST)")
                                                    .optional(),
                                            fieldWithPath("data.claimInfo.holdInfo")
                                                    .type(JsonFieldType.NULL)
                                                    .description("보류 정보 (없을 경우 null)")
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
                                                    .type(JsonFieldType.NULL)
                                                    .description(
                                                            "처리일시 (ISO 8601, KST) - 미처리 시 null")
                                                    .optional(),
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

        @Test
        @DisplayName("환불 상세 조회 성공 - 보류 상태")
        void getDetail_HoldStatus_Success() throws Exception {
            // given
            String holdRefundClaimId = "01940001-0000-7000-8000-000000000099";
            RefundDetailApiResponse.HoldInfoApiResponse holdInfo =
                    new RefundDetailApiResponse.HoldInfoApiResponse(
                            "추가 확인 필요", RefundApiFixtures.DEFAULT_FORMATTED_TIME);
            RefundDetailApiResponse.RefundClaimInfoApiResponse holdClaimInfo =
                    new RefundDetailApiResponse.RefundClaimInfoApiResponse(
                            holdRefundClaimId,
                            RefundApiFixtures.DEFAULT_CLAIM_NUMBER,
                            1,
                            "HOLD",
                            RefundApiFixtures.DEFAULT_REASON_TYPE,
                            RefundApiFixtures.DEFAULT_REASON_DETAIL,
                            null,
                            holdInfo,
                            null,
                            RefundApiFixtures.DEFAULT_FORMATTED_TIME,
                            null);
            RefundDetailApiResponse holdDetailResponse =
                    new RefundDetailApiResponse(
                            RefundApiFixtures.DEFAULT_ORDER_ITEM_ID,
                            java.util.List.of(),
                            holdClaimInfo,
                            null,
                            null,
                            null,
                            RefundApiFixtures.DEFAULT_REQUESTED_BY,
                            RefundApiFixtures.DEFAULT_PROCESSED_BY,
                            RefundApiFixtures.DEFAULT_FORMATTED_TIME,
                            RefundApiFixtures.DEFAULT_FORMATTED_TIME,
                            RefundApiFixtures.DEFAULT_FORMATTED_TIME,
                            java.util.List.of());

            given(getRefundDetailUseCase.execute(holdRefundClaimId))
                    .willReturn(RefundApiFixtures.detailResultWithHold(holdRefundClaimId));
            given(mapper.toDetailResponse(any(), any(), any(), any(), any()))
                    .willReturn(holdDetailResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    REFUNDS_URL + RefundAdminEndpoints.REFUND_CLAIM_ID,
                                    holdRefundClaimId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.claimInfo.status").value("HOLD"))
                    .andExpect(jsonPath("$.data.claimInfo.holdInfo").exists())
                    .andExpect(jsonPath("$.data.claimInfo.holdInfo.holdReason").value("추가 확인 필요"))
                    .andExpect(jsonPath("$.data.claimInfo.refundInfo").doesNotExist());
        }
    }
}
