package com.ryuqq.marketplace.adapter.in.rest.notice.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.notice.NoticeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.notice.NoticeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.response.NoticeCategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.notice.mapper.NoticeCategoryQueryApiMapper;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.port.in.query.GetNoticeCategoryUseCase;
import com.ryuqq.marketplace.application.notice.port.in.query.SearchNoticeCategoryByOffsetUseCase;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
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
@WebMvcTest(NoticeCategoryQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("NoticeCategoryQueryController REST Docs 테스트")
class NoticeCategoryQueryControllerRestDocsTest {

    private static final String BASE_URL = NoticeAdminEndpoints.NOTICE_CATEGORIES;
    private static final Long NOTICE_CATEGORY_ID = 1L;
    private static final String CATEGORY_GROUP = "CLOTHING";

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetNoticeCategoryUseCase getNoticeCategoryUseCase;
    @MockitoBean private SearchNoticeCategoryByOffsetUseCase searchNoticeCategoryByOffsetUseCase;
    @MockitoBean private NoticeCategoryQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("고시정보 카테고리 목록 검색 API")
    class SearchNoticeCategoriesTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchNoticeCategoriesByOffset_ValidRequest_Returns200WithPage() throws Exception {
            // given
            NoticeCategoryPageResult pageResult = NoticeApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<NoticeCategoryApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    NoticeApiFixtures.apiResponse(1L),
                                    NoticeApiFixtures.apiResponse(2L),
                                    NoticeApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchNoticeCategoryByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(NoticeCategoryPageResult.class)))
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
                                    "notice-category/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("active")
                                                    .description("활성 상태 필터")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (CODE, NAME_KO, NAME_EN)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description("정렬 키 (createdAt, code)")
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
                                                    .description("고시정보 카테고리 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .description("카테고리 ID"),
                                            fieldWithPath("data.content[].code")
                                                    .description("카테고리 코드"),
                                            fieldWithPath("data.content[].nameKo")
                                                    .description("한글명"),
                                            fieldWithPath("data.content[].nameEn")
                                                    .description("영문명"),
                                            fieldWithPath("data.content[].targetCategoryGroup")
                                                    .description("대상 카테고리 그룹"),
                                            fieldWithPath("data.content[].active")
                                                    .description("활성 상태"),
                                            fieldWithPath("data.content[].fields")
                                                    .description("고시정보 필드 목록"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .description("생성일시"),
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
        @DisplayName("활성화 필터와 검색어를 함께 사용할 수 있다")
        void searchNoticeCategoriesByOffset_WithFilters_Returns200() throws Exception {
            // given
            NoticeCategoryPageResult pageResult = NoticeApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<NoticeCategoryApiResponse> pageResponse =
                    PageApiResponse.of(List.of(NoticeApiFixtures.apiResponse(1L)), 0, 20, 1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchNoticeCategoryByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(NoticeCategoryPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("active", "true")
                                    .param("searchField", "CODE")
                                    .param("searchWord", "CLOTHING")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchNoticeCategoriesByOffset_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            NoticeCategoryPageResult emptyResult = NoticeApiFixtures.emptyPageResult();
            PageApiResponse<NoticeCategoryApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchNoticeCategoryByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(NoticeCategoryPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("카테고리 그룹별 고시정보 조회 API")
    class GetNoticeCategoryByCategoryGroupTest {

        @Test
        @DisplayName("카테고리 그룹으로 고시정보 조회 성공")
        void getNoticeCategoryByCategoryGroup_Success() throws Exception {
            // given
            NoticeCategoryResult result = NoticeApiFixtures.noticeCategoryResultWithFields(1L);
            NoticeCategoryApiResponse response = NoticeApiFixtures.apiResponseWithFields(1L);

            given(getNoticeCategoryUseCase.execute(CategoryGroup.CLOTHING)).willReturn(result);
            given(mapper.toResponse(any(NoticeCategoryResult.class))).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + NoticeAdminEndpoints.CATEGORY_GROUP, CATEGORY_GROUP))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1L))
                    .andExpect(jsonPath("$.data.code").value("CLOTHING"))
                    .andExpect(jsonPath("$.data.nameKo").value("의류"))
                    .andExpect(jsonPath("$.data.fields").isArray())
                    .andExpect(jsonPath("$.data.fields.length()").value(2))
                    .andDo(
                            document(
                                    "notice-category/get-by-category-group",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("categoryGroup")
                                                    .description("카테고리 그룹 (예: CLOTHING)")),
                                    responseFields(
                                            fieldWithPath("data.id").description("카테고리 ID"),
                                            fieldWithPath("data.code").description("카테고리 코드"),
                                            fieldWithPath("data.nameKo").description("한글명"),
                                            fieldWithPath("data.nameEn").description("영문명"),
                                            fieldWithPath("data.targetCategoryGroup")
                                                    .description("대상 카테고리 그룹"),
                                            fieldWithPath("data.active").description("활성 상태"),
                                            fieldWithPath("data.fields[]")
                                                    .description("고시정보 필드 목록"),
                                            fieldWithPath("data.fields[].id").description("필드 ID"),
                                            fieldWithPath("data.fields[].fieldCode")
                                                    .description("필드 코드"),
                                            fieldWithPath("data.fields[].fieldName")
                                                    .description("필드명"),
                                            fieldWithPath("data.fields[].required")
                                                    .description("필수 여부"),
                                            fieldWithPath("data.fields[].sortOrder")
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.createdAt").description("생성일시"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }

        @Test
        @DisplayName("여러 필드를 포함한 고시정보를 조회한다")
        void getNoticeCategoryByCategoryGroup_WithMultipleFields_ReturnsAllFields()
                throws Exception {
            // given
            NoticeCategoryResult result = NoticeApiFixtures.noticeCategoryResultWithFields(1L);
            NoticeCategoryApiResponse response = NoticeApiFixtures.apiResponseWithFields(1L);

            given(getNoticeCategoryUseCase.execute(CategoryGroup.CLOTHING)).willReturn(result);
            given(mapper.toResponse(any(NoticeCategoryResult.class))).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + NoticeAdminEndpoints.CATEGORY_GROUP, CATEGORY_GROUP))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.fields").isArray())
                    .andExpect(jsonPath("$.data.fields.length()").value(2))
                    .andExpect(jsonPath("$.data.fields[0].fieldCode").value("MATERIAL"))
                    .andExpect(jsonPath("$.data.fields[0].fieldName").value("소재"))
                    .andExpect(jsonPath("$.data.fields[0].required").value(true))
                    .andExpect(jsonPath("$.data.fields[1].fieldCode").value("ORIGIN"))
                    .andExpect(jsonPath("$.data.fields[1].fieldName").value("원산지"))
                    .andExpect(jsonPath("$.data.fields[1].required").value(true));
        }
    }
}
