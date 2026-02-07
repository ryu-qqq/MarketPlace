package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.SellerApplicationAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.SellerApplicationApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response.SellerApplicationApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.mapper.SellerApplicationQueryApiMapper;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationPageResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult;
import com.ryuqq.marketplace.application.sellerapplication.port.in.query.GetSellerApplicationUseCase;
import com.ryuqq.marketplace.application.sellerapplication.port.in.query.SearchSellerApplicationByOffsetUseCase;
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
@WebMvcTest(SellerApplicationQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerApplicationQueryController REST Docs 테스트")
class SellerApplicationQueryControllerRestDocsTest {

    private static final String BASE_URL = SellerApplicationAdminEndpoints.SELLER_APPLICATIONS;
    private static final Long APPLICATION_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchSellerApplicationByOffsetUseCase searchUseCase;
    @MockitoBean private GetSellerApplicationUseCase getUseCase;
    @MockitoBean private SellerApplicationQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("입점 신청 목록 검색 API")
    class SearchTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void search_ValidRequest_Returns200WithPage() throws Exception {
            // given
            SellerApplicationPageResult pageResult =
                    SellerApplicationApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<SellerApplicationApiResponse> pageResponse =
                    PageApiResponse.of(SellerApplicationApiFixtures.apiResponses(3), 0, 20, 3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(SellerApplicationPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andDo(
                            document(
                                    "seller-application/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("status")
                                                    .description(
                                                            "신청 상태 필터 (PENDING, APPROVED,"
                                                                    + " REJECTED)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (COMPANY_NAME,"
                                                                    + " REPRESENTATIVE_NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description("정렬 기준 (appliedAt)")
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
                                            fieldWithPath("data.content[]").description("입점 신청 목록"),
                                            fieldWithPath("data.content[].id").description("신청 ID"),
                                            fieldWithPath("data.content[].sellerInfo.sellerName")
                                                    .description("셀러명"),
                                            fieldWithPath("data.content[].sellerInfo.displayName")
                                                    .description("표시명"),
                                            fieldWithPath("data.content[].sellerInfo.logoUrl")
                                                    .description("로고 URL"),
                                            fieldWithPath("data.content[].sellerInfo.description")
                                                    .description("설명"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.registrationNumber")
                                                    .description("사업자등록번호"),
                                            fieldWithPath("data.content[].businessInfo.companyName")
                                                    .description("회사명"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.representative")
                                                    .description("대표자명"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.saleReportNumber")
                                                    .description("통신판매업 신고번호"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.businessAddress.zipCode")
                                                    .description("우편번호"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.businessAddress.line1")
                                                    .description("주소"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.businessAddress.line2")
                                                    .description("상세주소"),
                                            fieldWithPath("data.content[].csContact.phone")
                                                    .description("전화번호"),
                                            fieldWithPath("data.content[].csContact.email")
                                                    .description("이메일"),
                                            fieldWithPath("data.content[].csContact.mobile")
                                                    .description("휴대폰"),
                                            fieldWithPath("data.content[].contactInfo.name")
                                                    .description("담당자명")
                                                    .optional(),
                                            fieldWithPath("data.content[].contactInfo.phone")
                                                    .description("담당자 연락처")
                                                    .optional(),
                                            fieldWithPath("data.content[].contactInfo.email")
                                                    .description("담당자 이메일")
                                                    .optional(),
                                            fieldWithPath("data.content[].agreement.agreedAt")
                                                    .description("동의 일시"),
                                            fieldWithPath("data.content[].agreement.termsAgreed")
                                                    .description("이용약관 동의"),
                                            fieldWithPath("data.content[].agreement.privacyAgreed")
                                                    .description("개인정보처리방침 동의"),
                                            fieldWithPath("data.content[].status")
                                                    .description("신청 상태"),
                                            fieldWithPath("data.content[].appliedAt")
                                                    .description("신청일시"),
                                            fieldWithPath("data.content[].processedAt")
                                                    .description("처리일시")
                                                    .optional(),
                                            fieldWithPath("data.content[].processedBy")
                                                    .description("처리자")
                                                    .optional(),
                                            fieldWithPath("data.content[].rejectionReason")
                                                    .description("거절 사유")
                                                    .optional(),
                                            fieldWithPath("data.content[].approvedSellerId")
                                                    .description("승인된 셀러 ID")
                                                    .optional(),
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
        void search_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            SellerApplicationPageResult emptyResult =
                    SellerApplicationApiFixtures.emptyPageResult();
            PageApiResponse<SellerApplicationApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(SellerApplicationPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("입점 신청 상세 조회 API")
    class GetTest {

        @Test
        @DisplayName("상세 조회 성공")
        void get_Success() throws Exception {
            // given
            SellerApplicationResult result =
                    SellerApplicationApiFixtures.applicationResult(APPLICATION_ID);
            SellerApplicationApiResponse response =
                    SellerApplicationApiFixtures.apiResponse(APPLICATION_ID);

            given(getUseCase.execute(APPLICATION_ID)).willReturn(result);
            given(mapper.toResponse(any(SellerApplicationResult.class))).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + SellerApplicationAdminEndpoints.ID, APPLICATION_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(APPLICATION_ID))
                    .andExpect(jsonPath("$.data.sellerInfo.sellerName").value("테스트셀러"))
                    .andExpect(jsonPath("$.data.status").value("PENDING"))
                    .andDo(
                            document(
                                    "seller-application/get",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("applicationId")
                                                    .description("입점 신청 ID")),
                                    responseFields(
                                            fieldWithPath("data.id").description("신청 ID"),
                                            fieldWithPath("data.sellerInfo.sellerName")
                                                    .description("셀러명"),
                                            fieldWithPath("data.sellerInfo.displayName")
                                                    .description("표시명"),
                                            fieldWithPath("data.sellerInfo.logoUrl")
                                                    .description("로고 URL"),
                                            fieldWithPath("data.sellerInfo.description")
                                                    .description("설명"),
                                            fieldWithPath("data.businessInfo.registrationNumber")
                                                    .description("사업자등록번호"),
                                            fieldWithPath("data.businessInfo.companyName")
                                                    .description("회사명"),
                                            fieldWithPath("data.businessInfo.representative")
                                                    .description("대표자명"),
                                            fieldWithPath("data.businessInfo.saleReportNumber")
                                                    .description("통신판매업 신고번호"),
                                            fieldWithPath(
                                                            "data.businessInfo.businessAddress.zipCode")
                                                    .description("우편번호"),
                                            fieldWithPath("data.businessInfo.businessAddress.line1")
                                                    .description("주소"),
                                            fieldWithPath("data.businessInfo.businessAddress.line2")
                                                    .description("상세주소"),
                                            fieldWithPath("data.csContact.phone")
                                                    .description("전화번호"),
                                            fieldWithPath("data.csContact.email")
                                                    .description("이메일"),
                                            fieldWithPath("data.csContact.mobile")
                                                    .description("휴대폰"),
                                            fieldWithPath("data.contactInfo.name")
                                                    .description("담당자명")
                                                    .optional(),
                                            fieldWithPath("data.contactInfo.phone")
                                                    .description("담당자 연락처")
                                                    .optional(),
                                            fieldWithPath("data.contactInfo.email")
                                                    .description("담당자 이메일")
                                                    .optional(),
                                            fieldWithPath("data.agreement.agreedAt")
                                                    .description("동의 일시"),
                                            fieldWithPath("data.agreement.termsAgreed")
                                                    .description("이용약관 동의"),
                                            fieldWithPath("data.agreement.privacyAgreed")
                                                    .description("개인정보처리방침 동의"),
                                            fieldWithPath("data.status").description("신청 상태"),
                                            fieldWithPath("data.appliedAt").description("신청일시"),
                                            fieldWithPath("data.processedAt")
                                                    .description("처리일시")
                                                    .optional(),
                                            fieldWithPath("data.processedBy")
                                                    .description("처리자")
                                                    .optional(),
                                            fieldWithPath("data.rejectionReason")
                                                    .description("거절 사유")
                                                    .optional(),
                                            fieldWithPath("data.approvedSellerId")
                                                    .description("승인된 셀러 ID")
                                                    .optional(),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
