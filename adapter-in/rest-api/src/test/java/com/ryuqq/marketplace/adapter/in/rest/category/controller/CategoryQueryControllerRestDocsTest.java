package com.ryuqq.marketplace.adapter.in.rest.category.controller;

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

import com.ryuqq.marketplace.adapter.in.rest.category.CategoryAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.category.CategoryApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.mapper.CategoryQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.category.dto.response.CategoryPageResult;
import com.ryuqq.marketplace.application.category.port.in.query.SearchCategoryByOffsetUseCase;
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
@WebMvcTest(CategoryQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CategoryQueryController REST Docs 테스트")
class CategoryQueryControllerRestDocsTest {

    private static final String BASE_URL = CategoryAdminEndpoints.CATEGORIES;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchCategoryByOffsetUseCase searchCategoryByOffsetUseCase;
    @MockitoBean private CategoryQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("카테고리 목록 검색 API")
    class SearchCategoriesTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchCategoriesByOffset_ValidRequest_Returns200WithPage() throws Exception {
            // given
            CategoryPageResult pageResult = CategoryApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<CategoryApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    CategoryApiFixtures.apiResponse(1L),
                                    CategoryApiFixtures.apiResponse(2L),
                                    CategoryApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchCategoryByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(CategoryPageResult.class))).willReturn(pageResponse);

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
                                    "category/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("parentId")
                                                    .description("부모 카테고리 ID")
                                                    .optional(),
                                            parameterWithName("depth")
                                                    .description("계층 깊이")
                                                    .optional(),
                                            parameterWithName("leaf")
                                                    .description("리프 노드 여부")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description("상태 필터 (ACTIVE, INACTIVE)")
                                                    .optional(),
                                            parameterWithName("departments")
                                                    .description(
                                                            "부문 필터 (FASHION, BEAUTY, LIVING 등)")
                                                    .optional(),
                                            parameterWithName("categoryGroups")
                                                    .description(
                                                            "카테고리 그룹 필터 (CLOTHING, SHOES, DIGITAL"
                                                                    + " 등)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (code, nameKo, nameEn)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (sortOrder, createdAt, nameKo,"
                                                                    + " code)")
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
                                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("data.content[].code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 코드"),
                                            fieldWithPath("data.content[].nameKo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("한글명"),
                                            fieldWithPath("data.content[].nameEn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("영문명"),
                                            fieldWithPath("data.content[].parentId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("부모 카테고리 ID")
                                                    .optional(),
                                            fieldWithPath("data.content[].depth")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("계층 깊이"),
                                            fieldWithPath("data.content[].path").type(JsonFieldType.STRING).description("경로"),
                                            fieldWithPath("data.content[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.content[].leaf")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("리프 노드 여부"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태"),
                                            fieldWithPath("data.content[].department")
                                                    .type(JsonFieldType.STRING)
                                                    .description("부문"),
                                            fieldWithPath("data.content[].categoryGroup")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 그룹 (고시정보 연결용)"),
                                            fieldWithPath("data.content[].displayPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시용 이름 경로")
                                                    .optional(),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                            fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                            fieldWithPath("requestId").type(JsonFieldType.STRING).description("요청 ID"))));
        }

        @Test
        @DisplayName("상태/부문 필터와 검색어를 함께 사용할 수 있다")
        void searchCategoriesByOffset_WithFilters_Returns200() throws Exception {
            // given
            CategoryPageResult pageResult = CategoryApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<CategoryApiResponse> pageResponse =
                    PageApiResponse.of(List.of(CategoryApiFixtures.apiResponse(1L)), 0, 20, 1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchCategoryByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(CategoryPageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("statuses", "ACTIVE")
                                    .param("departments", "FASHION")
                                    .param("searchField", "nameKo")
                                    .param("searchWord", "테스트")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchCategoriesByOffset_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            CategoryPageResult emptyResult = CategoryApiFixtures.emptyPageResult();
            PageApiResponse<CategoryApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchCategoryByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(CategoryPageResult.class))).willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
