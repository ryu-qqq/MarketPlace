package com.ryuqq.marketplace.adapter.in.rest.cancel.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.cancel.CancelAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.cancel.CancelApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.mapper.CancelApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelDetailUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelListUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelSummaryUseCase;
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
@WebMvcTest(CancelQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CancelQueryController REST Docs 테스트")
class CancelQueryControllerRestDocsTest {

    private static final String BASE_URL = CancelAdminEndpoints.CANCELS;
    private static final String DEFAULT_CANCEL_ID = CancelApiFixtures.DEFAULT_CANCEL_ID;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetCancelSummaryUseCase getCancelSummaryUseCase;
    @MockitoBean private GetCancelListUseCase getCancelListUseCase;
    @MockitoBean private GetCancelDetailUseCase getCancelDetailUseCase;
    @MockitoBean private CancelApiMapper mapper;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.common.mapper.ClaimOrderEnricher
            claimOrderEnricher;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("취소 상태별 요약 조회 API")
    class GetCancelSummaryTest {

        @Test
        @DisplayName("취소 요약 조회 성공 - 상태별 건수를 반환한다")
        void getSummary_Success() throws Exception {
            // given
            CancelSummaryResult summaryResult = CancelApiFixtures.summaryResult();
            CancelSummaryApiResponse summaryResponse = CancelApiFixtures.summaryApiResponse();

            given(getCancelSummaryUseCase.execute()).willReturn(summaryResult);
            given(mapper.toSummaryResponse(any(CancelSummaryResult.class)))
                    .willReturn(summaryResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + CancelAdminEndpoints.SUMMARY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.requested").value(10))
                    .andExpect(jsonPath("$.data.approved").value(5))
                    .andExpect(jsonPath("$.data.rejected").value(3))
                    .andExpect(jsonPath("$.data.completed").value(20))
                    .andDo(
                            document(
                                    "cancel/summary",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    relaxedResponseFields(
                                            fieldWithPath("data.requested")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 요청 건수"),
                                            fieldWithPath("data.approved")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 승인 건수"),
                                            fieldWithPath("data.rejected")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 거절 건수"),
                                            fieldWithPath("data.completed")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 완료 건수"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("취소 목록 조회 API")
    class GetCancelListTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void getList_ValidRequest_Returns200WithPage() throws Exception {
            // given
            CancelPageResult pageResult = CancelApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<?> pageResponse = CancelApiFixtures.pageApiResponse(3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getCancelListUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponseV4(any(), any())).willReturn((PageApiResponse) pageResponse);

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
                    .andDo(
                            document(
                                    "cancel/list",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("statuses")
                                                    .description(
                                                            "취소 상태 필터 (REQUESTED, APPROVED,"
                                                                    + " REJECTED, COMPLETED)")
                                                    .optional(),
                                            parameterWithName("types")
                                                    .description(
                                                            "취소 유형 필터 (BUYER_CANCEL,"
                                                                    + " SELLER_CANCEL)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (CANCEL_NUMBER, ORDER_NUMBER 등)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("dateField")
                                                    .description("날짜 검색 대상 (REQUESTED, COMPLETED)")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("시작일 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("종료일 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT, REQUESTED_AT,"
                                                                    + " COMPLETED_AT)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터). 기본값: 0")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기. 기본값: 20")
                                                    .optional()),
                                    relaxedResponseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("취소 목록"),
                                            fieldWithPath("data.content[].cancelId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 ID (UUIDv7)"),
                                            fieldWithPath("data.content[].cancelNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 번호"),
                                            fieldWithPath("data.content[].orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "주문 ID (V4: orderId = 내부 orderItemId)"),
                                            fieldWithPath("data.content[].cancelQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 수량"),
                                            fieldWithPath("data.content[].cancelType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "취소 유형 (BUYER_CANCEL, SELLER_CANCEL)"),
                                            fieldWithPath("data.content[].cancelStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 상태"),
                                            fieldWithPath("data.content[].reasonType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 사유 유형"),
                                            fieldWithPath("data.content[].reasonDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 상세 사유"),
                                            fieldWithPath("data.content[].refundAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 금액"),
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
                                                    .description("요청일시 (ISO 8601 +09:00)"),
                                            fieldWithPath("data.content[].processedAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description("처리일시 (ISO 8601 +09:00)")
                                                    .optional(),
                                            fieldWithPath("data.content[].completedAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description("완료일시 (ISO 8601 +09:00)")
                                                    .optional(),
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
        @DisplayName("상태 필터를 사용하여 조회할 수 있다")
        void getList_WithStatusFilter_Returns200() throws Exception {
            // given
            CancelPageResult pageResult = CancelApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<?> pageResponse = CancelApiFixtures.pageApiResponse(1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getCancelListUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponseV4(any(), any())).willReturn((PageApiResponse) pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("statuses", "REQUESTED")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void getList_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            CancelPageResult emptyResult = CancelApiFixtures.emptyPageResult();
            PageApiResponse<?> emptyResponse = PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getCancelListUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponseV4(any(), any()))
                    .willReturn((PageApiResponse) emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("취소 상세 조회 API")
    class GetCancelDetailTest {

        @Test
        @DisplayName("취소 상세 조회 성공 - 환불 정보와 이력을 포함한 상세를 반환한다")
        void getDetail_Success() throws Exception {
            // given
            CancelDetailResult detailResult = CancelApiFixtures.detailResult(DEFAULT_CANCEL_ID);
            CancelDetailApiResponse detailResponse =
                    CancelApiFixtures.detailApiResponse(DEFAULT_CANCEL_ID);

            given(getCancelDetailUseCase.execute(DEFAULT_CANCEL_ID)).willReturn(detailResult);
            given(
                            mapper.toDetailResponse(
                                    any(CancelDetailResult.class), any(), any(), any(), any()))
                    .willReturn(detailResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + CancelAdminEndpoints.CANCEL_ID, DEFAULT_CANCEL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.cancelInfo.cancelId").value(DEFAULT_CANCEL_ID))
                    .andExpect(
                            jsonPath("$.data.cancelInfo.cancelNumber")
                                    .value(CancelApiFixtures.DEFAULT_CANCEL_NUMBER))
                    .andExpect(
                            jsonPath("$.data.orderId")
                                    .value(CancelApiFixtures.DEFAULT_ORDER_ITEM_ID))
                    .andExpect(
                            jsonPath("$.data.cancelInfo.type")
                                    .value(CancelApiFixtures.DEFAULT_CANCEL_TYPE))
                    .andExpect(
                            jsonPath("$.data.cancelInfo.status")
                                    .value(CancelApiFixtures.DEFAULT_CANCEL_STATUS))
                    .andExpect(jsonPath("$.data.cancelInfo.refundInfo").exists())
                    .andExpect(jsonPath("$.data.cancelHistories").isArray())
                    .andDo(
                            document(
                                    "cancel/detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("cancelId")
                                                    .description("취소 ID (UUIDv7)")),
                                    relaxedResponseFields(
                                            fieldWithPath("data.orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "주문 ID (V4: orderId = 내부 orderItemId)"),
                                            fieldWithPath("data.cancelInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("취소 정보"),
                                            fieldWithPath("data.cancelInfo.cancelId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 ID (UUIDv7)"),
                                            fieldWithPath("data.cancelInfo.cancelNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 번호"),
                                            fieldWithPath("data.cancelInfo.type")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "취소 유형 (BUYER_CANCEL, SELLER_CANCEL)"),
                                            fieldWithPath("data.cancelInfo.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 상태"),
                                            fieldWithPath("data.cancelInfo.cancelQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 수량"),
                                            fieldWithPath("data.cancelInfo.reason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 사유"),
                                            fieldWithPath("data.cancelInfo.refundInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("환불 정보")
                                                    .optional(),
                                            fieldWithPath("data.cancelInfo.requestedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청일시 (ISO 8601 +09:00)"),
                                            fieldWithPath("data.cancelInfo.completedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("완료일시 (ISO 8601 +09:00)")
                                                    .optional(),
                                            fieldWithPath("data.requestedBy")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청자"),
                                            fieldWithPath("data.processedBy")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리자"),
                                            fieldWithPath("data.processedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리일시 (ISO 8601 +09:00)")
                                                    .optional(),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시 (ISO 8601 +09:00)"),
                                            fieldWithPath("data.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시 (ISO 8601 +09:00)"),
                                            fieldWithPath("data.cancelHistories")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("클레임 이력 목록"),
                                            fieldWithPath("data.cancelHistories[].historyId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이력 ID")
                                                    .optional(),
                                            fieldWithPath("data.cancelHistories[].type")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이력 유형")
                                                    .optional(),
                                            fieldWithPath("data.cancelHistories[].message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이력 내용")
                                                    .optional(),
                                            fieldWithPath("data.cancelHistories[].actor")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("처리자 정보")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("환불 정보 없이 취소 상세를 조회할 수 있다")
        void getDetail_WithoutRefundInfo_Returns200() throws Exception {
            // given
            CancelDetailResult detailResult =
                    CancelApiFixtures.detailResultWithoutRefundInfo(DEFAULT_CANCEL_ID);
            CancelDetailApiResponse detailResponse =
                    CancelApiFixtures.detailApiResponseWithoutRefund(DEFAULT_CANCEL_ID);

            given(getCancelDetailUseCase.execute(anyString())).willReturn(detailResult);
            given(
                            mapper.toDetailResponse(
                                    any(CancelDetailResult.class), any(), any(), any(), any()))
                    .willReturn(detailResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + CancelAdminEndpoints.CANCEL_ID, DEFAULT_CANCEL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.cancelInfo.cancelId").value(DEFAULT_CANCEL_ID))
                    .andExpect(jsonPath("$.data.cancelInfo.refundInfo").doesNotExist());
        }
    }
}
