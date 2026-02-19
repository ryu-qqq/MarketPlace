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
import org.springframework.restdocs.payload.JsonFieldType;
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
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("입점 신청 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("신청 ID"),
                                            fieldWithPath("data.content[].sellerInfo.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.content[].sellerInfo.displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("data.content[].sellerInfo.logoUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로고 URL"),
                                            fieldWithPath("data.content[].sellerInfo.description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.registrationNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사업자등록번호"),
                                            fieldWithPath("data.content[].businessInfo.companyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("회사명"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.representative")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표자명"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.saleReportNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("통신판매업 신고번호"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.businessAddress.zipCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.businessAddress.line1")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소"),
                                            fieldWithPath(
                                                            "data.content[].businessInfo.businessAddress.line2")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세주소"),
                                            fieldWithPath("data.content[].csContact.phone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호"),
                                            fieldWithPath("data.content[].csContact.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일"),
                                            fieldWithPath("data.content[].csContact.mobile")
                                                    .type(JsonFieldType.STRING)
                                                    .description("휴대폰"),
                                            fieldWithPath("data.content[].contactInfo.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("담당자명")
                                                    .optional(),
                                            fieldWithPath("data.content[].contactInfo.phone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("담당자 연락처")
                                                    .optional(),
                                            fieldWithPath("data.content[].contactInfo.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("담당자 이메일")
                                                    .optional(),
                                            fieldWithPath("data.content[].agreement.agreedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("동의 일시"),
                                            fieldWithPath("data.content[].agreement.termsAgreed")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("이용약관 동의"),
                                            fieldWithPath("data.content[].agreement.privacyAgreed")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("개인정보처리방침 동의"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("신청 상태"),
                                            fieldWithPath("data.content[].appliedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("신청일시"),
                                            fieldWithPath("data.content[].processedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리일시")
                                                    .optional(),
                                            fieldWithPath("data.content[].processedBy")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리자")
                                                    .optional(),
                                            fieldWithPath("data.content[].rejectionReason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("거절 사유")
                                                    .optional(),
                                            fieldWithPath("data.content[].approvedSellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("승인된 셀러 ID")
                                                    .optional(),
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
                                            fieldWithPath("data.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("신청 ID"),
                                            fieldWithPath("data.sellerInfo.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.sellerInfo.displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("data.sellerInfo.logoUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로고 URL"),
                                            fieldWithPath("data.sellerInfo.description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
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
                                            fieldWithPath(
                                                            "data.businessInfo.businessAddress.zipCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호"),
                                            fieldWithPath("data.businessInfo.businessAddress.line1")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소"),
                                            fieldWithPath("data.businessInfo.businessAddress.line2")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세주소"),
                                            fieldWithPath("data.csContact.phone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호"),
                                            fieldWithPath("data.csContact.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일"),
                                            fieldWithPath("data.csContact.mobile")
                                                    .type(JsonFieldType.STRING)
                                                    .description("휴대폰"),
                                            fieldWithPath("data.contactInfo.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("담당자명")
                                                    .optional(),
                                            fieldWithPath("data.contactInfo.phone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("담당자 연락처")
                                                    .optional(),
                                            fieldWithPath("data.contactInfo.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("담당자 이메일")
                                                    .optional(),
                                            fieldWithPath("data.agreement.agreedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("동의 일시"),
                                            fieldWithPath("data.agreement.termsAgreed")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("이용약관 동의"),
                                            fieldWithPath("data.agreement.privacyAgreed")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("개인정보처리방침 동의"),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("신청 상태"),
                                            fieldWithPath("data.appliedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("신청일시"),
                                            fieldWithPath("data.processedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리일시")
                                                    .optional(),
                                            fieldWithPath("data.processedBy")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리자")
                                                    .optional(),
                                            fieldWithPath("data.rejectionReason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("거절 사유")
                                                    .optional(),
                                            fieldWithPath("data.approvedSellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("승인된 셀러 ID")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
