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
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsPartnerApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper.OmsPartnerQueryApiMapper;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchOmsPartnersByOffsetUseCase;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
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
@WebMvcTest(OmsPartnerQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("OmsPartnerQueryController REST Docs 테스트")
class OmsPartnerQueryControllerRestDocsTest {

    private static final String PARTNERS_URL = OmsEndpoints.PARTNERS;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchOmsPartnersByOffsetUseCase searchOmsPartnersByOffsetUseCase;
    @MockitoBean private OmsPartnerQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("파트너(셀러) 목록 조회 API")
    class SearchPartnersTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchPartners_ValidRequest_Returns200WithPage() throws Exception {
            // given
            SellerPageResult pageResult = OmsApiFixtures.sellerPageResult(3, 0, 100);
            PageApiResponse<OmsPartnerApiResponse> pageResponse =
                    PageApiResponse.of(OmsApiFixtures.partnerApiResponses(3), 0, 100, 3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchOmsPartnersByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(SellerPageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(PARTNERS_URL)
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
                                    "oms-partner/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("keyword")
                                                    .description("검색어 (셀러명)")
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
                                                    .description("파트너 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.content[].partnerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("파트너명 (표시명)"),
                                            fieldWithPath("data.content[].partnerCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("파트너 코드 (셀러명)"),
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
        void searchPartners_WithKeyword_Returns200() throws Exception {
            // given
            SellerPageResult pageResult = OmsApiFixtures.sellerPageResult(1, 0, 100);
            PageApiResponse<OmsPartnerApiResponse> pageResponse =
                    PageApiResponse.of(List.of(OmsApiFixtures.partnerApiResponse(1L)), 0, 100, 1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchOmsPartnersByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(SellerPageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(PARTNERS_URL)
                                    .param("keyword", "나이키")
                                    .param("page", "0")
                                    .param("size", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchPartners_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            SellerPageResult emptyResult = OmsApiFixtures.emptySellerPageResult();
            PageApiResponse<OmsPartnerApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 100, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchOmsPartnersByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(SellerPageResult.class))).willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(PARTNERS_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
