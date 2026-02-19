package com.ryuqq.marketplace.adapter.in.rest.externalsource.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.externalsource.ExternalSourceAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.ExternalSourceApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.response.ExternalSourceApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.mapper.ExternalSourceQueryApiMapper;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourcePageResult;
import com.ryuqq.marketplace.application.externalsource.port.in.query.SearchExternalSourceUseCase;
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
@WebMvcTest(ExternalSourceQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ExternalSourceQueryController REST Docs 테스트")
class ExternalSourceQueryControllerRestDocsTest {

    private static final String BASE_URL = ExternalSourceAdminEndpoints.EXTERNAL_SOURCES;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchExternalSourceUseCase searchExternalSourceUseCase;
    @MockitoBean private ExternalSourceQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("외부 소스 목록 검색 API")
    class SearchTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void search_ValidRequest_Returns200WithPage() throws Exception {
            // given
            ExternalSourcePageResult pageResult = ExternalSourceApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<ExternalSourceApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    ExternalSourceApiFixtures.apiResponse(1L),
                                    ExternalSourceApiFixtures.apiResponse(2L),
                                    ExternalSourceApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchExternalSourceUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ExternalSourcePageResult.class)))
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
                    .andDo(
                            document(
                                    "external-source/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("types")
                                                    .description(
                                                            "유형 필터 (CRAWLING, LEGACY, PARTNER,"
                                                                    + " SALES_CHANNEL)")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description("상태 필터 (ACTIVE, INACTIVE)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (CODE, NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT). 기본값: CREATED_AT")
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
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("외부 소스 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("외부 소스 ID"),
                                            fieldWithPath("data.content[].code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 소스 코드"),
                                            fieldWithPath("data.content[].name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 소스명"),
                                            fieldWithPath("data.content[].type")
                                                    .type(JsonFieldType.STRING)
                                                    .description("유형"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태"),
                                            fieldWithPath("data.content[].description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
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
        @DisplayName("필터와 검색어를 함께 사용할 수 있다")
        void search_WithFilters_Returns200() throws Exception {
            // given
            ExternalSourcePageResult pageResult = ExternalSourceApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<ExternalSourceApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(ExternalSourceApiFixtures.apiResponse(1L)), 0, 20, 1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchExternalSourceUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ExternalSourcePageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("types", "SALES_CHANNEL")
                                    .param("statuses", "ACTIVE")
                                    .param("searchField", "NAME")
                                    .param("searchWord", "네이버")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void search_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            ExternalSourcePageResult emptyResult = ExternalSourceApiFixtures.emptyPageResult();
            PageApiResponse<ExternalSourceApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchExternalSourceUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(ExternalSourcePageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
