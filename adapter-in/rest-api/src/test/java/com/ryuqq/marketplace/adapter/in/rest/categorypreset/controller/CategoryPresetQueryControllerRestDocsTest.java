package com.ryuqq.marketplace.adapter.in.rest.categorypreset.controller;

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

import com.ryuqq.marketplace.adapter.in.rest.categorypreset.CategoryPresetAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.CategoryPresetApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response.CategoryPresetApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response.CategoryPresetDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.mapper.CategoryPresetQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;
import com.ryuqq.marketplace.application.categorypreset.port.in.query.GetCategoryPresetDetailUseCase;
import com.ryuqq.marketplace.application.categorypreset.port.in.query.SearchCategoryPresetByOffsetUseCase;
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
@WebMvcTest(CategoryPresetQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CategoryPresetQueryController REST Docs 테스트")
class CategoryPresetQueryControllerRestDocsTest {

    private static final String BASE_URL = CategoryPresetAdminEndpoints.CATEGORY_PRESETS;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchCategoryPresetByOffsetUseCase searchCategoryPresetByOffsetUseCase;
    @MockitoBean private GetCategoryPresetDetailUseCase getCategoryPresetDetailUseCase;
    @MockitoBean private CategoryPresetQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("카테고리 프리셋 목록 검색 API")
    class SearchCategoryPresetsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchCategoryPresets_ValidRequest_Returns200WithPage() throws Exception {
            // given
            CategoryPresetPageResult pageResult = CategoryPresetApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<CategoryPresetApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    CategoryPresetApiFixtures.apiResponse(1L),
                                    CategoryPresetApiFixtures.apiResponse(2L),
                                    CategoryPresetApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchCategoryPresetByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(CategoryPresetPageResult.class)))
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
                                    "category-preset/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("salesChannelIds")
                                                    .description("판매채널 ID 목록")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description("상태 필터 (ACTIVE, INACTIVE)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (PRESET_NAME, SHOP_NAME,"
                                                                    + " ACCOUNT_ID, CATEGORY_CODE,"
                                                                    + " CATEGORY_PATH)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("등록일 시작 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("등록일 종료 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description("정렬 키 (createdAt)")
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
                                                    .description("카테고리 프리셋 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .description("프리셋 ID"),
                                            fieldWithPath("data.content[].shopId")
                                                    .description("Shop ID"),
                                            fieldWithPath("data.content[].shopName")
                                                    .description("쇼핑몰명"),
                                            fieldWithPath("data.content[].salesChannelId")
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.content[].salesChannelName")
                                                    .description("판매채널명"),
                                            fieldWithPath("data.content[].accountId")
                                                    .description("계정 ID"),
                                            fieldWithPath("data.content[].presetName")
                                                    .description("프리셋 이름"),
                                            fieldWithPath("data.content[].categoryPath")
                                                    .description("카테고리 경로"),
                                            fieldWithPath("data.content[].categoryCode")
                                                    .description("카테고리 코드"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .description("등록일"),
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
        void searchCategoryPresets_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            CategoryPresetPageResult emptyResult = CategoryPresetApiFixtures.emptyPageResult();
            PageApiResponse<CategoryPresetApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchCategoryPresetByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(CategoryPresetPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("카테고리 프리셋 상세 조회 API")
    class GetCategoryPresetDetailTest {

        @Test
        @DisplayName("유효한 ID로 조회하면 200과 상세 응답을 반환한다")
        void getCategoryPreset_ValidId_Returns200() throws Exception {
            // given
            Long categoryPresetId = 1L;
            CategoryPresetDetailResult detailResult =
                    CategoryPresetApiFixtures.categoryPresetDetailResult(categoryPresetId);
            CategoryPresetDetailApiResponse detailResponse =
                    createDetailApiResponse(categoryPresetId);

            given(getCategoryPresetDetailUseCase.execute(categoryPresetId))
                    .willReturn(detailResult);
            given(mapper.toDetailResponse(any(CategoryPresetDetailResult.class)))
                    .willReturn(detailResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + "/{categoryPresetId}", categoryPresetId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(categoryPresetId))
                    .andExpect(jsonPath("$.data.mappingCategory.categoryCode").exists())
                    .andExpect(jsonPath("$.data.internalCategories").isArray())
                    .andDo(
                            document(
                                    "category-preset/get-detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("categoryPresetId")
                                                    .description("카테고리 프리셋 ID")),
                                    responseFields(
                                            fieldWithPath("data.id").description("프리셋 ID"),
                                            fieldWithPath("data.shopId").description("Shop ID"),
                                            fieldWithPath("data.shopName").description("쇼핑몰명"),
                                            fieldWithPath("data.salesChannelId")
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.salesChannelName")
                                                    .description("판매채널명"),
                                            fieldWithPath("data.accountId").description("계정 ID"),
                                            fieldWithPath("data.presetName").description("프리셋 이름"),
                                            fieldWithPath("data.mappingCategory")
                                                    .description("매핑된 판매채널 카테고리"),
                                            fieldWithPath("data.mappingCategory.categoryCode")
                                                    .description("외부 카테고리 코드"),
                                            fieldWithPath("data.mappingCategory.categoryPath")
                                                    .description("카테고리 경로"),
                                            fieldWithPath("data.internalCategories[]")
                                                    .description("매핑된 내부 카테고리 목록"),
                                            fieldWithPath("data.internalCategories[].id")
                                                    .description("내부 카테고리 ID"),
                                            fieldWithPath("data.internalCategories[].categoryPath")
                                                    .description("카테고리 경로"),
                                            fieldWithPath("data.createdAt").description("등록일"),
                                            fieldWithPath("data.updatedAt").description("수정일"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    private CategoryPresetDetailApiResponse createDetailApiResponse(Long id) {
        CategoryPresetDetailApiResponse.MappingCategoryResponse mappingCategory =
                new CategoryPresetDetailApiResponse.MappingCategoryResponse(
                        "50000123", "식품 > 과자 > 스낵 > 젤리");
        List<CategoryPresetDetailApiResponse.InternalCategoryResponse> internalCategories =
                List.of(
                        new CategoryPresetDetailApiResponse.InternalCategoryResponse(
                                100L, "내부 카테고리 A 경로"),
                        new CategoryPresetDetailApiResponse.InternalCategoryResponse(
                                200L, "내부 카테고리 B 경로"));
        return new CategoryPresetDetailApiResponse(
                id,
                1L,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                "테스트 카테고리 프리셋",
                mappingCategory,
                internalCategories,
                "2025-02-10T10:30:00+09:00",
                "2025-02-10T10:30:00+09:00");
    }
}
