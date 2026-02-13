package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.SalesChannelBrandApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.response.SalesChannelBrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.mapper.SalesChannelBrandQueryApiMapper;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;
import com.ryuqq.marketplace.application.saleschannelbrand.port.in.query.SearchSalesChannelBrandByOffsetUseCase;
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
@WebMvcTest(SalesChannelBrandQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SalesChannelBrandQueryController REST Docs 테스트")
class SalesChannelBrandQueryControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/market/sales-channels/{salesChannelId}/brands";
    private static final Long SALES_CHANNEL_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchSalesChannelBrandByOffsetUseCase searchUseCase;
    @MockitoBean private SalesChannelBrandQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("외부채널 브랜드 목록 조회 API")
    class SearchBrandsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchBrands_ValidRequest_Returns200WithPage() throws Exception {
            // given
            SalesChannelBrandPageResult pageResult =
                    SalesChannelBrandApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<SalesChannelBrandApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    SalesChannelBrandApiFixtures.apiResponse(1L),
                                    SalesChannelBrandApiFixtures.apiResponse(2L),
                                    SalesChannelBrandApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any(), any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(SalesChannelBrandPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL, SALES_CHANNEL_ID)
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
                                    "sales-channel-brand/search",
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
                                                            "검색 필드 (externalBrandCode,"
                                                                    + " externalBrandName)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (createdAt,"
                                                                    + " externalBrandName)")
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
                                            fieldWithPath("data.content[]").description("브랜드 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.content[].salesChannelId")
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.content[].externalBrandCode")
                                                    .description("외부 브랜드 코드"),
                                            fieldWithPath("data.content[].externalBrandName")
                                                    .description("외부 브랜드명"),
                                            fieldWithPath("data.content[].status")
                                                    .description("상태"),
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
        @DisplayName("상태 필터와 검색어를 함께 사용할 수 있다")
        void searchBrands_WithFilters_Returns200() throws Exception {
            // given
            SalesChannelBrandPageResult pageResult =
                    SalesChannelBrandApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<SalesChannelBrandApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(SalesChannelBrandApiFixtures.apiResponse(1L)), 0, 20, 1);

            given(mapper.toSearchParams(any(), any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(SalesChannelBrandPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL, SALES_CHANNEL_ID)
                                    .param("statuses", "ACTIVE")
                                    .param("searchField", "externalBrandName")
                                    .param("searchWord", "나이키")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchBrands_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            SalesChannelBrandPageResult emptyResult =
                    SalesChannelBrandApiFixtures.emptyPageResult();
            PageApiResponse<SalesChannelBrandApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any(), any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(SalesChannelBrandPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL, SALES_CHANNEL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
