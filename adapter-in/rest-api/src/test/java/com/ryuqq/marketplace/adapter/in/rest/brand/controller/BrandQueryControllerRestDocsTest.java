package com.ryuqq.marketplace.adapter.in.rest.brand.controller;

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

import com.ryuqq.marketplace.adapter.in.rest.brand.BrandAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.brand.BrandApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.mapper.BrandQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.port.in.query.SearchBrandByOffsetUseCase;
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
@WebMvcTest(BrandQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("BrandQueryController REST Docs 테스트")
class BrandQueryControllerRestDocsTest {

    private static final String BASE_URL = BrandAdminEndpoints.BRANDS;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchBrandByOffsetUseCase searchBrandByOffsetUseCase;
    @MockitoBean private BrandQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("브랜드 목록 검색 API")
    class SearchBrandsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchBrandsByOffset_ValidRequest_Returns200WithPage() throws Exception {
            // given
            BrandPageResult pageResult = BrandApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<BrandApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    BrandApiFixtures.apiResponse(1L),
                                    BrandApiFixtures.apiResponse(2L),
                                    BrandApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchBrandByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(BrandPageResult.class))).willReturn(pageResponse);

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
                                    "brand/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("statuses")
                                                    .description("상태 필터 (ACTIVE, INACTIVE)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (CODE, NAME_KO, NAME_EN)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT, NAME_KO,"
                                                                + " UPDATED_AT). 기본값: CREATED_AT")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("브랜드 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.content[].code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드 코드"),
                                            fieldWithPath("data.content[].nameKo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("한글명"),
                                            fieldWithPath("data.content[].nameEn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("영문명"),
                                            fieldWithPath("data.content[].shortName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("약칭"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태 (ACTIVE, INACTIVE)"),
                                            fieldWithPath("data.content[].logoUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로고 URL"),
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
        @DisplayName("상태 필터와 검색어를 함께 사용할 수 있다")
        void searchBrandsByOffset_WithFilters_Returns200() throws Exception {
            // given
            BrandPageResult pageResult = BrandApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<BrandApiResponse> pageResponse =
                    PageApiResponse.of(List.of(BrandApiFixtures.apiResponse(1L)), 0, 20, 1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchBrandByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(BrandPageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("statuses", "ACTIVE")
                                    .param("searchField", "NAME_KO")
                                    .param("searchWord", "테스트")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchBrandsByOffset_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            BrandPageResult emptyResult = BrandApiFixtures.emptyPageResult();
            PageApiResponse<BrandApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchBrandByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(BrandPageResult.class))).willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
