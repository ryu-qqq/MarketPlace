package com.ryuqq.marketplace.adapter.in.rest.seller.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.seller.SellerAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.seller.SellerApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.mapper.SellerQueryApiMapper;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerFullCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.port.in.query.GetSellerForAdminUseCase;
import com.ryuqq.marketplace.application.seller.port.in.query.SearchSellerByOffsetUseCase;
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
@WebMvcTest(SellerQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerQueryController REST Docs 테스트")
class SellerQueryControllerRestDocsTest {

    private static final String BASE_URL = SellerAdminEndpoints.SELLERS;
    private static final Long SELLER_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetSellerForAdminUseCase getSellerForAdminUseCase;
    @MockitoBean private SearchSellerByOffsetUseCase searchSellerByOffsetUseCase;
    @MockitoBean private SellerQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("셀러 상세 조회 API")
    class GetSellerTest {

        @Test
        @DisplayName("셀러 상세 조회 성공")
        void getSeller_Success() throws Exception {
            // given
            SellerFullCompositeResult fullResult = SellerApiFixtures.fullCompositeResult(SELLER_ID);
            SellerDetailApiResponse response = SellerApiFixtures.detailApiResponse(SELLER_ID);

            given(getSellerForAdminUseCase.execute(SELLER_ID)).willReturn(fullResult);
            given(mapper.toDetailResponse(any(SellerFullCompositeResult.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + SellerAdminEndpoints.SELLER_ID, SELLER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.seller.id").value(SELLER_ID))
                    .andExpect(jsonPath("$.data.seller.sellerName").value("테스트셀러"))
                    .andExpect(jsonPath("$.data.businessInfo").exists())
                    .andExpect(jsonPath("$.data.csInfo").exists())
                    .andExpect(jsonPath("$.data.contractInfo").exists())
                    .andExpect(jsonPath("$.data.settlementInfo").exists())
                    .andDo(
                            document(
                                    "seller/get",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    responseFields(
                                            // Seller Info
                                            fieldWithPath("data.seller.id").type(JsonFieldType.NUMBER).description("셀러 ID"),
                                            fieldWithPath("data.seller.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.seller.displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("data.seller.logoUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로고 URL"),
                                            fieldWithPath("data.seller.description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath("data.seller.active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("활성화 여부"),
                                            fieldWithPath("data.seller.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.seller.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),

                                            // Business Info
                                            fieldWithPath("data.businessInfo.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("사업자 정보 ID"),
                                            fieldWithPath("data.businessInfo.registrationNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사업자등록번호"),
                                            fieldWithPath("data.businessInfo.companyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("회사명"),
                                            fieldWithPath("data.businessInfo.representative")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표자명"),
                                            fieldWithPath("data.businessInfo.saleReportNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("통신판매업 신고번호"),
                                            fieldWithPath("data.businessInfo.businessZipcode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사업장 우편번호"),
                                            fieldWithPath("data.businessInfo.businessAddress")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사업장 주소"),
                                            fieldWithPath("data.businessInfo.businessAddressDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사업장 상세주소"),

                                            // CS Info
                                            fieldWithPath("data.csInfo.id").type(JsonFieldType.NUMBER).description("CS 정보 ID"),
                                            fieldWithPath("data.csInfo.csPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("CS 전화번호"),
                                            fieldWithPath("data.csInfo.csMobile")
                                                    .type(JsonFieldType.STRING)
                                                    .description("CS 휴대폰"),
                                            fieldWithPath("data.csInfo.csEmail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("CS 이메일"),
                                            fieldWithPath("data.csInfo.operatingStartTime")
                                                    .type(JsonFieldType.STRING)
                                                    .description("운영 시작 시간"),
                                            fieldWithPath("data.csInfo.operatingEndTime")
                                                    .type(JsonFieldType.STRING)
                                                    .description("운영 종료 시간"),
                                            fieldWithPath("data.csInfo.operatingDays")
                                                    .type(JsonFieldType.STRING)
                                                    .description("운영 요일"),
                                            fieldWithPath("data.csInfo.kakaoChannelUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카카오 채널 URL"),

                                            // Contract Info
                                            fieldWithPath("data.contractInfo.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("계약 정보 ID"),
                                            fieldWithPath("data.contractInfo.commissionRate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수수료율"),
                                            fieldWithPath("data.contractInfo.contractStartDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계약 시작일"),
                                            fieldWithPath("data.contractInfo.contractEndDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계약 종료일"),
                                            fieldWithPath("data.contractInfo.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계약 상태"),
                                            fieldWithPath("data.contractInfo.specialTerms")
                                                    .type(JsonFieldType.STRING)
                                                    .description("특약사항"),
                                            fieldWithPath("data.contractInfo.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.contractInfo.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),

                                            // Settlement Info
                                            fieldWithPath("data.settlementInfo.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정산 정보 ID"),
                                            fieldWithPath("data.settlementInfo.bankCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("은행 코드"),
                                            fieldWithPath("data.settlementInfo.bankName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("은행명"),
                                            fieldWithPath("data.settlementInfo.accountNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계좌번호"),
                                            fieldWithPath("data.settlementInfo.accountHolderName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("예금주명"),
                                            fieldWithPath("data.settlementInfo.settlementCycle")
                                                    .type(JsonFieldType.STRING)
                                                    .description("정산 주기"),
                                            fieldWithPath("data.settlementInfo.settlementDay")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정산일"),
                                            fieldWithPath("data.settlementInfo.verified")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("계좌 인증 여부"),
                                            fieldWithPath("data.settlementInfo.verifiedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("인증일시"),
                                            fieldWithPath("data.settlementInfo.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.settlementInfo.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),

                                            // Common fields
                                            fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                            fieldWithPath("requestId").type(JsonFieldType.STRING).description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("셀러 목록 검색 API")
    class SearchSellersTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchSellersByOffset_ValidRequest_Returns200WithPage() throws Exception {
            // given
            SellerPageResult pageResult = SellerApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<SellerApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    SellerApiFixtures.apiResponse(1L),
                                    SellerApiFixtures.apiResponse(2L),
                                    SellerApiFixtures.apiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchSellerByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(SellerPageResult.class))).willReturn(pageResponse);

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
                                    "seller/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("active")
                                                    .description("활성화 여부")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (SELLER_NAME,"
                                                                    + " REGISTRATION_NUMBER,"
                                                                    + " COMPANY_NAME,"
                                                                    + " REPRESENTATIVE_NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 기준 (createdAt, sellerName,"
                                                                    + " displayName)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("셀러 목록"),
                                            fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("셀러 ID"),
                                            fieldWithPath("data.content[].sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.content[].displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("data.content[].logoUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로고 URL"),
                                            fieldWithPath("data.content[].description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath("data.content[].active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("활성화 여부"),
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
        @DisplayName("활성화 필터와 검색어를 함께 사용할 수 있다")
        void searchSellersByOffset_WithFilters_Returns200() throws Exception {
            // given
            SellerPageResult pageResult = SellerApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<SellerApiResponse> pageResponse =
                    PageApiResponse.of(List.of(SellerApiFixtures.apiResponse(1L)), 0, 20, 1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchSellerByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(SellerPageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("active", "true")
                                    .param("searchField", "sellerName")
                                    .param("searchWord", "테스트")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchSellersByOffset_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            SellerPageResult emptyResult = SellerApiFixtures.emptyPageResult();
            PageApiResponse<SellerApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchSellerByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(SellerPageResult.class))).willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
