package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.SalesChannelCategoryAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.SalesChannelCategoryApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.response.SalesChannelCategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.mapper.SalesChannelCategoryQueryApiMapper;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.port.in.query.SearchSalesChannelCategoryByOffsetUseCase;
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
@WebMvcTest(SalesChannelCategoryQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SalesChannelCategoryQueryController REST Docs 테스트")
class SalesChannelCategoryQueryControllerRestDocsTest {

    private static final Long SALES_CHANNEL_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchSalesChannelCategoryByOffsetUseCase searchUseCase;
    @MockitoBean private SalesChannelCategoryQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("외부채널 카테고리 목록 조회 API")
    class SearchCategoriesTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchCategories_ValidRequest_Returns200WithPage() throws Exception {
            // given
            SalesChannelCategoryPageResult pageResult =
                    SalesChannelCategoryApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<SalesChannelCategoryApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    SalesChannelCategoryApiFixtures.apiResponse(
                                            1L, "CAT001", "카테고리_1"),
                                    SalesChannelCategoryApiFixtures.apiResponse(
                                            2L, "CAT002", "카테고리_2"),
                                    SalesChannelCategoryApiFixtures.apiResponse(
                                            3L, "CAT003", "카테고리_3")),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any(), any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(SalesChannelCategoryPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                            SalesChannelCategoryAdminEndpoints.CATEGORIES,
                                            SALES_CHANNEL_ID)
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
                                    "sales-channel-category/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("salesChannelId")
                                                    .description("판매채널 ID")),
                                    queryParameters(
                                            parameterWithName("statuses")
                                                    .description("상태 필터 (ACTIVE, INACTIVE)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (externalCategoryCode,"
                                                                    + " externalCategoryName)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("depth")
                                                    .description("카테고리 깊이")
                                                    .optional(),
                                            parameterWithName("parentId")
                                                    .description("부모 카테고리 ID")
                                                    .optional(),
                                            parameterWithName("mapped")
                                                    .description("내부 카테고리 매핑 여부")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (createdAt,"
                                                                    + " externalCategoryName,"
                                                                    + " sortOrder)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터 시작)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("data.content[].salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.content[].externalCategoryCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 카테고리 코드"),
                                            fieldWithPath("data.content[].externalCategoryName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 카테고리명"),
                                            fieldWithPath("data.content[].parentId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("부모 카테고리 ID"),
                                            fieldWithPath("data.content[].depth")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 깊이"),
                                            fieldWithPath("data.content[].path")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 경로"),
                                            fieldWithPath("data.content[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.content[].leaf")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("리프 노드 여부"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태"),
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
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchCategories_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            SalesChannelCategoryPageResult emptyResult =
                    SalesChannelCategoryApiFixtures.emptyPageResult();
            PageApiResponse<SalesChannelCategoryApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any(), any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(SalesChannelCategoryPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    SalesChannelCategoryAdminEndpoints.CATEGORIES,
                                    SALES_CHANNEL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
