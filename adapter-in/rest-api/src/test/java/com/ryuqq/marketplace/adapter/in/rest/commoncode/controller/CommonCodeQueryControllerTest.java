package com.ryuqq.marketplace.adapter.in.rest.commoncode.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.CommonCodeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.query.SearchCommonCodesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.response.CommonCodeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.mapper.CommonCodeQueryApiMapper;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.query.CommonCodeSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.port.in.query.SearchCommonCodeUseCase;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(CommonCodeQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CommonCodeQueryController 단위 테스트")
class CommonCodeQueryControllerTest {

    private static final String BASE_URL = "/api/v1/market/common-codes";

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchCommonCodeUseCase searchCommonCodeUseCase;
    @MockitoBean private CommonCodeQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v2/admin/common-codes - 공통 코드 조회")
    class SearchTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void search_ValidRequest_Returns200WithPage() throws Exception {
            // given
            CommonCodeSearchParams searchParams =
                    CommonCodeSearchParams.of(
                            1L,
                            null,
                            null,
                            CommonSearchParams.of(null, null, null, null, null, 0, 20));
            CommonCodePageResult pageResult = CommonCodeApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<CommonCodeApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    CommonCodeApiFixtures.apiResponse(1L),
                                    CommonCodeApiFixtures.apiResponse(2L),
                                    CommonCodeApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any(SearchCommonCodesPageApiRequest.class)))
                    .willReturn(searchParams);
            given(searchCommonCodeUseCase.execute(any(CommonCodeSearchParams.class)))
                    .willReturn(pageResult);
            given(mapper.toPageResponse(any(CommonCodePageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("commonCodeTypeId", "1")
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
                                    "common-code/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("commonCodeTypeId")
                                                    .description("공통 코드 타입 ID"),
                                            parameterWithName("page")
                                                    .description("페이지 번호")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]").description("공통 코드 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .description("공통 코드 ID"),
                                            fieldWithPath("data.content[].commonCodeTypeId")
                                                    .description("공통 코드 타입 ID"),
                                            fieldWithPath("data.content[].code").description("코드값"),
                                            fieldWithPath("data.content[].displayName")
                                                    .description("표시명"),
                                            fieldWithPath("data.content[].displayOrder")
                                                    .description("표시 순서"),
                                            fieldWithPath("data.content[].active")
                                                    .description("활성화 여부"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .description("생성일시"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .description("수정일시"),
                                            fieldWithPath("data.page").description("현재 페이지 번호"),
                                            fieldWithPath("data.size").description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.first").description("첫 페이지 여부"),
                                            fieldWithPath("data.last").description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void search_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            CommonCodeSearchParams searchParams =
                    CommonCodeSearchParams.of(
                            1L,
                            null,
                            null,
                            CommonSearchParams.of(null, null, null, null, null, 0, 20));
            CommonCodePageResult emptyResult = CommonCodeApiFixtures.emptyPageResult();
            PageApiResponse<CommonCodeApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any(SearchCommonCodesPageApiRequest.class)))
                    .willReturn(searchParams);
            given(searchCommonCodeUseCase.execute(any(CommonCodeSearchParams.class)))
                    .willReturn(emptyResult);
            given(mapper.toPageResponse(any(CommonCodePageResult.class))).willReturn(emptyResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("commonCodeTypeId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @DisplayName("commonCodeTypeId가 없으면 400을 반환한다")
        void search_MissingTypeId_Returns400() throws Exception {
            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("활성화 여부 필터와 코드 검색을 함께 사용할 수 있다")
        void search_WithFilters_Returns200() throws Exception {
            // given
            CommonCodeSearchParams searchParams =
                    CommonCodeSearchParams.of(
                            1L,
                            true,
                            "CARD",
                            CommonSearchParams.of(null, null, null, null, null, 0, 10));
            CommonCodePageResult pageResult = CommonCodeApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<CommonCodeApiResponse> pageResponse =
                    PageApiResponse.of(List.of(CommonCodeApiFixtures.apiResponse(1L)), 0, 20, 1);

            given(mapper.toSearchParams(any(SearchCommonCodesPageApiRequest.class)))
                    .willReturn(searchParams);
            given(searchCommonCodeUseCase.execute(any(CommonCodeSearchParams.class)))
                    .willReturn(pageResult);
            given(mapper.toPageResponse(any(CommonCodePageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("commonCodeTypeId", "1")
                                    .param("active", "true")
                                    .param("code", "CARD")
                                    .param("page", "0")
                                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }
    }
}
