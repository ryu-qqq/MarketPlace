package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.ShippingPolicyAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.ShippingPolicyApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response.ShippingPolicyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.mapper.ShippingPolicyQueryApiMapper;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.query.SearchShippingPolicyUseCase;
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
@WebMvcTest(ShippingPolicyQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ShippingPolicyQueryController REST Docs 테스트")
class ShippingPolicyQueryControllerRestDocsTest {

    private static final String BASE_URL = ShippingPolicyAdminEndpoints.SHIPPING_POLICIES;
    private static final Long SELLER_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchShippingPolicyUseCase searchShippingPolicyUseCase;
    @MockitoBean private ShippingPolicyQueryApiMapper mapper;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry
            errorMapperRegistry;

    @Nested
    @DisplayName("배송정책 목록 검색 API")
    class SearchShippingPoliciesTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void search_ValidRequest_Returns200WithPage() throws Exception {
            // given
            ShippingPolicyPageResult pageResult = ShippingPolicyApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<ShippingPolicyApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    ShippingPolicyApiFixtures.apiResponse(1L),
                                    ShippingPolicyApiFixtures.apiResponse(2L),
                                    ShippingPolicyApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any(), any())).willReturn(null);
            given(searchShippingPolicyUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ShippingPolicyPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL, SELLER_ID)
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
                                    "shipping-policy/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    queryParameters(
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT, POLICY_NAME,"
                                                                    + " BASE_FEE). 기본값: CREATED_AT")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC). 기본값: DESC")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터 시작). 기본값: 0")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기. 기본값: 20")
                                                    .optional(),
                                            parameterWithName("active")
                                                    .description(
                                                            "활성화 여부 (true: 활성만, false: 비활성만,"
                                                                    + " 미입력: 전체)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("배송정책 목록"),
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
                                            fieldWithPath("data.content[].shippingFeeType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "배송비 유형 코드 (FREE, PAID,"
                                                                    + " CONDITIONAL_FREE,"
                                                                    + " QUANTITY_BASED)"),
                                            fieldWithPath(
                                                            "data.content[].shippingFeeTypeDisplayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송비 유형 표시명"),
                                            fieldWithPath("data.content[].baseFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("기본 배송비"),
                                            fieldWithPath("data.content[].freeThreshold")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("무료배송 기준금액"),
                                            fieldWithPath("data.content[].jejuExtraFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("제주 추가배송비"),
                                            fieldWithPath("data.content[].islandExtraFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("도서산간 추가배송비"),
                                            fieldWithPath("data.content[].returnFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 배송비"),
                                            fieldWithPath("data.content[].exchangeFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 배송비"),
                                            fieldWithPath("data.content[].leadTimeMinDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최소 배송일"),
                                            fieldWithPath("data.content[].leadTimeMaxDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최대 배송일"),
                                            fieldWithPath("data.content[].leadTimeCutoffTime")
                                                    .type(JsonFieldType.STRING)
                                                    .description("당일 출고 마감시간 (HH:mm)"),
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
        @DisplayName("정렬 조건과 함께 조회할 수 있다")
        void search_WithSort_Returns200() throws Exception {
            // given
            ShippingPolicyPageResult pageResult = ShippingPolicyApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<ShippingPolicyApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(ShippingPolicyApiFixtures.apiResponse(1L)), 0, 20, 1);

            given(mapper.toSearchParams(any(), any())).willReturn(null);
            given(searchShippingPolicyUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ShippingPolicyPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL, SELLER_ID)
                                    .param("sortKey", "CREATED_AT")
                                    .param("sortDirection", "DESC")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void search_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            ShippingPolicyPageResult emptyResult = ShippingPolicyApiFixtures.emptyPageResult();
            PageApiResponse<ShippingPolicyApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any(), any())).willReturn(null);
            given(searchShippingPolicyUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(ShippingPolicyPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL, SELLER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
