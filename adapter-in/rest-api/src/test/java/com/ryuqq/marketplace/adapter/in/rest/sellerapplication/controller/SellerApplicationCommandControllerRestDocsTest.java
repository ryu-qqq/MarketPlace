package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.SellerApplicationAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.SellerApplicationApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.ApplySellerApplicationApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.RejectSellerApplicationApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.mapper.SellerApplicationCommandApiMapper;
import com.ryuqq.marketplace.application.sellerapplication.port.in.command.ApplySellerApplicationUseCase;
import com.ryuqq.marketplace.application.sellerapplication.port.in.command.ApproveSellerApplicationUseCase;
import com.ryuqq.marketplace.application.sellerapplication.port.in.command.RejectSellerApplicationUseCase;
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
@WebMvcTest(SellerApplicationCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerApplicationCommandController REST Docs 테스트")
class SellerApplicationCommandControllerRestDocsTest {

    private static final String BASE_URL = SellerApplicationAdminEndpoints.SELLER_APPLICATIONS;
    private static final Long APPLICATION_ID = 1L;
    private static final Long SELLER_ID = 100L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ApplySellerApplicationUseCase applyUseCase;
    @MockitoBean private ApproveSellerApplicationUseCase approveUseCase;
    @MockitoBean private RejectSellerApplicationUseCase rejectUseCase;
    @MockitoBean private SellerApplicationCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("입점 신청 API")
    class ApplyTest {

        @Test
        @DisplayName("입점 신청 성공")
        void apply_Success() throws Exception {
            // given
            ApplySellerApplicationApiRequest request = SellerApplicationApiFixtures.applyRequest();

            given(mapper.toCommand(any(ApplySellerApplicationApiRequest.class))).willReturn(null);
            given(applyUseCase.execute(any())).willReturn(APPLICATION_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.applicationId").value(APPLICATION_ID))
                    .andDo(
                            document(
                                    "seller-application/apply",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("sellerInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("셀러 기본 정보"),
                                            fieldWithPath("sellerInfo.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("sellerInfo.displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시명"),
                                            fieldWithPath("sellerInfo.logoUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로고 URL"),
                                            fieldWithPath("sellerInfo.description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath("businessInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("사업자 정보"),
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
                                                    .type(JsonFieldType.OBJECT)
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
                                            fieldWithPath("csContact")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("CS 연락처"),
                                            fieldWithPath("csContact.phone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호"),
                                            fieldWithPath("csContact.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일"),
                                            fieldWithPath("csContact.mobile")
                                                    .type(JsonFieldType.STRING)
                                                    .description("휴대폰"),
                                            fieldWithPath("contactInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("담당자 연락처")
                                                    .optional(),
                                            fieldWithPath("contactInfo.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("담당자명")
                                                    .optional(),
                                            fieldWithPath("contactInfo.phone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("담당자 연락처")
                                                    .optional(),
                                            fieldWithPath("contactInfo.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("담당자 이메일")
                                                    .optional(),
                                            fieldWithPath("settlementInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("정산 정보"),
                                            fieldWithPath("settlementInfo.bankCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("은행 코드"),
                                            fieldWithPath("settlementInfo.bankName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("은행명"),
                                            fieldWithPath("settlementInfo.accountNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계좌번호"),
                                            fieldWithPath("settlementInfo.accountHolderName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("예금주"),
                                            fieldWithPath("settlementInfo.settlementCycle")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "정산 주기 (WEEKLY/BIWEEKLY/MONTHLY, 미입력 시"
                                                                    + " MONTHLY)"),
                                            fieldWithPath("settlementInfo.settlementDay")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정산일 1-31 (미입력 시 1)")),
                                    responseFields(
                                            fieldWithPath("data.applicationId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 신청 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("입점 신청 승인 API")
    class ApproveTest {

        @Test
        @DisplayName("승인 성공")
        void approve_Success() throws Exception {
            // given
            given(mapper.toApproveCommand(APPLICATION_ID)).willReturn(null);
            given(approveUseCase.execute(any())).willReturn(SELLER_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + SellerApplicationAdminEndpoints.APPROVE,
                                    APPLICATION_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.sellerId").value(SELLER_ID))
                    .andDo(
                            document(
                                    "seller-application/approve",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("applicationId")
                                                    .description("입점 신청 ID")),
                                    responseFields(
                                            fieldWithPath("data.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 셀러 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("입점 신청 거절 API")
    class RejectTest {

        @Test
        @DisplayName("거절 성공")
        void reject_Success() throws Exception {
            // given
            RejectSellerApplicationApiRequest request =
                    SellerApplicationApiFixtures.rejectRequest();

            given(
                            mapper.toRejectCommand(
                                    any(Long.class), any(RejectSellerApplicationApiRequest.class)))
                    .willReturn(null);
            doNothing().when(rejectUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + SellerApplicationAdminEndpoints.REJECT,
                                            APPLICATION_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller-application/reject",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("applicationId")
                                                    .description("입점 신청 ID")),
                                    requestFields(
                                            fieldWithPath("rejectionReason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("거절 사유"))));
        }
    }
}
