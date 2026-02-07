package com.ryuqq.marketplace.adapter.in.rest.seller.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.seller.SellerAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.seller.SellerApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.RegisterSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerFullApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.mapper.SellerCommandApiMapper;
import com.ryuqq.marketplace.application.seller.port.in.command.RegisterSellerUseCase;
import com.ryuqq.marketplace.application.seller.port.in.command.UpdateSellerFullUseCase;
import com.ryuqq.marketplace.application.seller.port.in.command.UpdateSellerUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(SellerCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerCommandController REST Docs 테스트")
class SellerCommandControllerRestDocsTest {

    private static final String BASE_URL = SellerAdminEndpoints.SELLERS;
    private static final Long SELLER_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterSellerUseCase registerSellerUseCase;
    @MockitoBean private UpdateSellerUseCase updateSellerUseCase;
    @MockitoBean private UpdateSellerFullUseCase updateSellerFullUseCase;
    @MockitoBean private SellerCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("셀러 등록 API")
    class RegisterSellerTest {

        @Test
        @DisplayName("셀러 등록 성공")
        void registerSeller_Success() throws Exception {
            // given
            RegisterSellerApiRequest request = SellerApiFixtures.registerRequest();

            given(mapper.toCommand(any(RegisterSellerApiRequest.class))).willReturn(null);
            given(registerSellerUseCase.execute(any())).willReturn(SELLER_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.sellerId").value(SELLER_ID))
                    .andDo(
                            document(
                                    "seller/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            // Seller Info
                                            fieldWithPath("seller").description("셀러 기본 정보"),
                                            fieldWithPath("seller.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("seller.displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("seller.logoUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로고 URL"),
                                            fieldWithPath("seller.description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),

                                            // Business Info
                                            fieldWithPath("businessInfo").description("사업자 정보"),
                                            fieldWithPath("businessInfo.registrationNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사업자등록번호"),
                                            fieldWithPath("businessInfo.companyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("회사명"),
                                            fieldWithPath("businessInfo.representative")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표자명"),
                                            fieldWithPath("businessInfo.saleReportNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("통신판매업 신고번호"),
                                            fieldWithPath("businessInfo.businessAddress")
                                                    .description("사업장 주소"),
                                            fieldWithPath("businessInfo.businessAddress.zipCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호"),
                                            fieldWithPath("businessInfo.businessAddress.line1")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소"),
                                            fieldWithPath("businessInfo.businessAddress.line2")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세주소"),
                                            fieldWithPath("businessInfo.csContact")
                                                    .description("CS 연락처"),
                                            fieldWithPath("businessInfo.csContact.phone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호"),
                                            fieldWithPath("businessInfo.csContact.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일"),
                                            fieldWithPath("businessInfo.csContact.mobile")
                                                    .type(JsonFieldType.STRING)
                                                    .description("휴대폰")),
                                    responseFields(
                                            fieldWithPath("data.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 셀러 ID"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("셀러 전체정보 수정 API")
    class UpdateSellerFullTest {

        @Test
        @DisplayName("셀러 전체정보 수정 성공")
        void updateSellerFull_Success() throws Exception {
            // given
            UpdateSellerFullApiRequest request = SellerApiFixtures.updateFullRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateSellerFullApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateSellerFullUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + SellerAdminEndpoints.SELLER_ID, SELLER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller/update-full",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    requestFields(
                                            // Seller Info
                                            fieldWithPath("seller").description("셀러 기본 정보"),
                                            fieldWithPath("seller.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("seller.displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("seller.logoUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로고 URL"),
                                            fieldWithPath("seller.description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),

                                            // Business Info
                                            fieldWithPath("businessInfo").description("사업자 정보"),
                                            fieldWithPath("businessInfo.registrationNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사업자등록번호"),
                                            fieldWithPath("businessInfo.companyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("회사명"),
                                            fieldWithPath("businessInfo.representative")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표자명"),
                                            fieldWithPath("businessInfo.saleReportNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("통신판매업 신고번호"),
                                            fieldWithPath("businessInfo.businessAddress")
                                                    .description("사업장 주소"),
                                            fieldWithPath("businessInfo.businessAddress.zipCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호"),
                                            fieldWithPath("businessInfo.businessAddress.line1")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소"),
                                            fieldWithPath("businessInfo.businessAddress.line2")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세주소"),

                                            // CS Info
                                            fieldWithPath("csInfo").description("CS 정보"),
                                            fieldWithPath("csInfo.phone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("CS 전화번호"),
                                            fieldWithPath("csInfo.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("CS 이메일"),
                                            fieldWithPath("csInfo.mobile")
                                                    .type(JsonFieldType.STRING)
                                                    .description("CS 휴대폰"),
                                            fieldWithPath("csInfo.operatingStartTime")
                                                    .type(JsonFieldType.STRING)
                                                    .description("운영 시작 시간"),
                                            fieldWithPath("csInfo.operatingEndTime")
                                                    .type(JsonFieldType.STRING)
                                                    .description("운영 종료 시간"),
                                            fieldWithPath("csInfo.operatingDays")
                                                    .type(JsonFieldType.STRING)
                                                    .description("운영 요일"),
                                            fieldWithPath("csInfo.kakaoChannelUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카카오 채널 URL"),

                                            // Contract Info
                                            fieldWithPath("contractInfo").description("계약 정보"),
                                            fieldWithPath("contractInfo.commissionRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수수료율"),
                                            fieldWithPath("contractInfo.contractStartDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계약 시작일"),
                                            fieldWithPath("contractInfo.contractEndDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계약 종료일"),
                                            fieldWithPath("contractInfo.specialTerms")
                                                    .type(JsonFieldType.STRING)
                                                    .description("특약사항"),

                                            // Settlement Info
                                            fieldWithPath("settlementInfo").description("정산 정보"),
                                            fieldWithPath("settlementInfo.bankAccount")
                                                    .description("은행 계좌 정보"),
                                            fieldWithPath("settlementInfo.bankAccount.bankCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("은행 코드"),
                                            fieldWithPath("settlementInfo.bankAccount.bankName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("은행명"),
                                            fieldWithPath(
                                                            "settlementInfo.bankAccount.accountNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계좌번호"),
                                            fieldWithPath(
                                                            "settlementInfo.bankAccount.accountHolderName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("예금주명"),
                                            fieldWithPath("settlementInfo.settlementCycle")
                                                    .type(JsonFieldType.STRING)
                                                    .description("정산 주기"),
                                            fieldWithPath("settlementInfo.settlementDay")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정산일"))));
        }
    }

    @Nested
    @DisplayName("셀러 기본정보 수정 API")
    class UpdateSellerTest {

        @Test
        @DisplayName("셀러 기본정보 수정 성공")
        void updateSeller_Success() throws Exception {
            // given
            UpdateSellerApiRequest request = SellerApiFixtures.updateRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateSellerApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateSellerUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL + SellerAdminEndpoints.SELLER_ID, SELLER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    requestFields(
                                            fieldWithPath("sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("logoUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로고 URL"),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath("address").description("주소"),
                                            fieldWithPath("address.zipCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호"),
                                            fieldWithPath("address.line1")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소"),
                                            fieldWithPath("address.line2")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세주소"),
                                            fieldWithPath("csInfo").description("CS 정보"),
                                            fieldWithPath("csInfo.phone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호"),
                                            fieldWithPath("csInfo.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일"),
                                            fieldWithPath("csInfo.mobile")
                                                    .type(JsonFieldType.STRING)
                                                    .description("휴대폰"),
                                            fieldWithPath("businessInfo").description("사업자 정보"),
                                            fieldWithPath("businessInfo.registrationNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사업자등록번호"),
                                            fieldWithPath("businessInfo.companyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("회사명"),
                                            fieldWithPath("businessInfo.representative")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표자명"),
                                            fieldWithPath("businessInfo.saleReportNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("통신판매업 신고번호"),
                                            fieldWithPath("businessInfo.businessAddress")
                                                    .description("사업장 주소"),
                                            fieldWithPath("businessInfo.businessAddress.zipCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호"),
                                            fieldWithPath("businessInfo.businessAddress.line1")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소"),
                                            fieldWithPath("businessInfo.businessAddress.line2")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세주소"))));
        }
    }
}
