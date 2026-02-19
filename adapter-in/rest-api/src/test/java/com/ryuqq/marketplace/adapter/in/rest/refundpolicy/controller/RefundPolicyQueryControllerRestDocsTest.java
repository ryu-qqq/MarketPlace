package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.controller;

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

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.RefundPolicyAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.RefundPolicyApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.RefundPolicyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.mapper.RefundPolicyQueryApiMapper;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;
import com.ryuqq.marketplace.application.refundpolicy.port.in.query.SearchRefundPolicyUseCase;
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
@WebMvcTest(RefundPolicyQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("RefundPolicyQueryController REST Docs 테스트")
class RefundPolicyQueryControllerRestDocsTest {

    private static final String BASE_URL = RefundPolicyAdminEndpoints.REFUND_POLICIES;
    private static final Long SELLER_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchRefundPolicyUseCase searchRefundPolicyUseCase;
    @MockitoBean private RefundPolicyQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("환불정책 목록 조회 API")
    class SearchTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void search_ValidRequest_Returns200WithPage() throws Exception {
            // given
            RefundPolicyPageResult pageResult = RefundPolicyApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<RefundPolicyApiResponse> pageResponse =
                    PageApiResponse.of(RefundPolicyApiFixtures.apiResponses(3), 0, 20, 3);

            given(mapper.toSearchParams(any(Long.class), any())).willReturn(null);
            given(searchRefundPolicyUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(RefundPolicyPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL, SELLER_ID)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andDo(
                            document(
                                    "refund-policy/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    queryParameters(
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 기준 (CREATED_AT, POLICY_NAME,"
                                                                    + " RETURN_PERIOD_DAYS, 기본값:"
                                                                    + " CREATED_AT)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC, 기본값: DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터, 기본값: 0)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20)")
                                                    .optional(),
                                            parameterWithName("active")
                                                    .description(
                                                            "활성화 여부 (true: 활성만, false: 비활성만,"
                                                                    + " 미입력: 전체)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("환불정책 목록"),
                                            fieldWithPath("data.content[].policyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정책 ID"),
                                            fieldWithPath("data.content[].sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.content[].policyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("정책명"),
                                            fieldWithPath("data.content[].defaultPolicy")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("기본 정책 여부"),
                                            fieldWithPath("data.content[].active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("활성화 상태"),
                                            fieldWithPath("data.content[].returnPeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 가능 기간 (일)"),
                                            fieldWithPath("data.content[].exchangePeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 가능 기간 (일)"),
                                            fieldWithPath(
                                                            "data.content[].nonReturnableConditions[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("반품 불가 조건 목록"),
                                            fieldWithPath(
                                                            "data.content[].nonReturnableConditions[].code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조건 코드"),
                                            fieldWithPath(
                                                            "data.content[].nonReturnableConditions[].displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조건 표시명"),
                                            fieldWithPath("data.content[].partialRefundEnabled")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("부분 환불 허용 여부"),
                                            fieldWithPath("data.content[].inspectionRequired")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("검수 필요 여부"),
                                            fieldWithPath("data.content[].inspectionPeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("검수 기간 (일)"),
                                            fieldWithPath("data.content[].additionalInfo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("추가 안내 문구"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시 (ISO 8601)"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시 (ISO 8601)"),
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
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void search_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            RefundPolicyPageResult emptyResult = RefundPolicyApiFixtures.emptyPageResult();
            PageApiResponse<RefundPolicyApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any(Long.class), any())).willReturn(null);
            given(searchRefundPolicyUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(RefundPolicyPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL, SELLER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
