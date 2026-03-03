package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsShopApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper.OmsShopQueryApiMapper;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchOmsShopsByOffsetUseCase;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
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
@WebMvcTest(OmsShopQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("OmsShopQueryController REST Docs 테스트")
class OmsShopQueryControllerRestDocsTest {

    private static final String SHOPS_URL = OmsEndpoints.SHOPS;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchOmsShopsByOffsetUseCase searchOmsShopsByOffsetUseCase;
    @MockitoBean private OmsShopQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("쇼핑몰 목록 조회 API")
    class SearchShopsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchShops_ValidRequest_Returns200WithPage() throws Exception {
            // given
            ShopPageResult pageResult = OmsApiFixtures.shopPageResult(3, 0, 100);
            PageApiResponse<OmsShopApiResponse> pageResponse =
                    PageApiResponse.of(OmsApiFixtures.shopApiResponses(3), 0, 100, 3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchOmsShopsByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ShopPageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(SHOPS_URL)
                                    .param("page", "0")
                                    .param("size", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(100))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andDo(
                            document(
                                    "oms-shop/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("keyword")
                                                    .description("검색어 (쇼핑몰명)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description("정렬 키 (CREATED_AT)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC/DESC)")
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
                                                    .description("쇼핑몰 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("쇼핑몰 ID"),
                                            fieldWithPath("data.content[].shopName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("쇼핑몰명"),
                                            fieldWithPath("data.content[].salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.content[].accountId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계정 ID"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태 (ACTIVE/INACTIVE)"),
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
        @DisplayName("키워드 검색을 사용할 수 있다")
        void searchShops_WithKeyword_Returns200() throws Exception {
            // given
            ShopPageResult pageResult = OmsApiFixtures.shopPageResult(1, 0, 100);
            PageApiResponse<OmsShopApiResponse> pageResponse =
                    PageApiResponse.of(List.of(OmsApiFixtures.shopApiResponse(1L)), 0, 100, 1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchOmsShopsByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ShopPageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(SHOPS_URL)
                                    .param("keyword", "스마트스토어")
                                    .param("page", "0")
                                    .param("size", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchShops_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            ShopPageResult emptyResult = OmsApiFixtures.emptyShopPageResult();
            PageApiResponse<OmsShopApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 100, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchOmsShopsByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(ShopPageResult.class))).willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(SHOPS_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
