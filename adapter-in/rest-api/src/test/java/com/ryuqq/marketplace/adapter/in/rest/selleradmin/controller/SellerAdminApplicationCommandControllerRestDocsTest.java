package com.ryuqq.marketplace.adapter.in.rest.selleradmin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminApplicationApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminApplicationEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ApplySellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkApproveSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkRejectSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ChangeSellerAdminPasswordApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.BulkApproveSellerAdminApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.mapper.SellerAdminApplicationCommandApiMapper;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ApplySellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ApproveSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.BulkApproveSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.BulkRejectSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ChangeSellerAdminPasswordUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.RejectSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ResetSellerAdminPasswordUseCase;
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
@WebMvcTest(SellerAdminApplicationCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerAdminApplicationCommandController REST Docs 테스트")
class SellerAdminApplicationCommandControllerRestDocsTest {

    private static final String BASE_URL = SellerAdminApplicationEndpoints.BASE;
    private static final String SELLER_ADMIN_ID =
            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ApplySellerAdminUseCase applyUseCase;
    @MockitoBean private ApproveSellerAdminUseCase approveUseCase;
    @MockitoBean private RejectSellerAdminUseCase rejectUseCase;
    @MockitoBean private BulkApproveSellerAdminUseCase bulkApproveUseCase;
    @MockitoBean private BulkRejectSellerAdminUseCase bulkRejectUseCase;
    @MockitoBean private ResetSellerAdminPasswordUseCase resetPasswordUseCase;
    @MockitoBean private ChangeSellerAdminPasswordUseCase changePasswordUseCase;
    @MockitoBean private SellerAdminApplicationCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("셀러 관리자 가입 신청 API")
    class ApplySellerAdminTest {

        @Test
        @DisplayName("셀러 관리자 가입 신청 성공")
        void apply_Success() throws Exception {
            // given
            ApplySellerAdminApiRequest request = SellerAdminApplicationApiFixtures.applyRequest();

            given(mapper.toCommand(any(ApplySellerAdminApiRequest.class))).willReturn(null);
            given(applyUseCase.execute(any())).willReturn(SELLER_ADMIN_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.sellerAdminId").value(SELLER_ADMIN_ID))
                    .andDo(
                            document(
                                    "seller-admin-application/apply",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("loginId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로그인 ID (이메일)"),
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("관리자 이름"),
                                            fieldWithPath("phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("휴대폰 번호"),
                                            fieldWithPath("password")
                                                    .type(JsonFieldType.STRING)
                                                    .description("비밀번호")),
                                    responseFields(
                                            fieldWithPath("data.sellerAdminId")
                                                    .description("생성된 셀러 관리자 ID (UUIDv7)"),
                                            fieldWithPath("timestamp").description("응답 시각"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("셀러 관리자 가입 신청 승인 API")
    class ApproveSellerAdminTest {

        @Test
        @DisplayName("셀러 관리자 가입 신청 승인 성공")
        void approve_Success() throws Exception {
            // given
            given(mapper.toApproveCommand(SELLER_ADMIN_ID)).willReturn(null);
            given(approveUseCase.execute(any())).willReturn(SELLER_ADMIN_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + SellerAdminApplicationEndpoints.APPROVE,
                                    SELLER_ADMIN_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.sellerAdminId").value(SELLER_ADMIN_ID))
                    .andDo(
                            document(
                                    "seller-admin-application/approve",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerAdminId")
                                                    .description("셀러 관리자 ID (UUIDv7)")),
                                    responseFields(
                                            fieldWithPath("data.sellerAdminId")
                                                    .description("승인된 셀러 관리자 ID"),
                                            fieldWithPath("timestamp").description("응답 시각"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("셀러 관리자 가입 신청 거절 API")
    class RejectSellerAdminTest {

        @Test
        @DisplayName("셀러 관리자 가입 신청 거절 성공")
        void reject_Success() throws Exception {
            // given
            given(mapper.toRejectCommand(SELLER_ADMIN_ID)).willReturn(null);
            doNothing().when(rejectUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + SellerAdminApplicationEndpoints.REJECT,
                                    SELLER_ADMIN_ID))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller-admin-application/reject",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerAdminId")
                                                    .description("셀러 관리자 ID (UUIDv7)"))));
        }
    }

    @Nested
    @DisplayName("셀러 관리자 가입 신청 일괄 승인 API")
    class BulkApproveSellerAdminTest {

        @Test
        @DisplayName("일괄 승인 성공 (전체 성공)")
        void bulkApprove_AllSuccess() throws Exception {
            // given
            BulkApproveSellerAdminApiRequest request =
                    SellerAdminApplicationApiFixtures.bulkApproveRequest();
            BatchProcessingResult<String> result =
                    SellerAdminApplicationApiFixtures.allSuccessBatchResult(
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID,
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID_2);
            BulkApproveSellerAdminApiResponse response =
                    SellerAdminApplicationApiFixtures.bulkApproveAllSuccessResponse(
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID,
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID_2);

            given(mapper.toBulkApproveCommand(any())).willReturn(null);
            given(bulkApproveUseCase.execute(any())).willReturn(result);
            given(mapper.toResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + SellerAdminApplicationEndpoints.BULK_APPROVE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andDo(
                            document(
                                    "seller-admin-application/bulk-approve",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("sellerAdminIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("승인할 셀러 관리자 ID 목록 (UUIDv7)")),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .description("전체 처리 건수"),
                                            fieldWithPath("data.successCount").description("성공 건수"),
                                            fieldWithPath("data.failureCount").description("실패 건수"),
                                            fieldWithPath("data.results[]").description("처리 결과 목록"),
                                            fieldWithPath("data.results[].sellerAdminId")
                                                    .description("셀러 관리자 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .description("에러 코드 (실패 시)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .description("에러 메시지 (실패 시)")
                                                    .optional(),
                                            fieldWithPath("timestamp").description("응답 시각"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }

        @Test
        @DisplayName("일괄 승인 부분 실패")
        void bulkApprove_PartialFailure() throws Exception {
            // given
            BulkApproveSellerAdminApiRequest request =
                    SellerAdminApplicationApiFixtures.bulkApproveRequest();
            BatchProcessingResult<String> result =
                    SellerAdminApplicationApiFixtures.partialFailureBatchResult(
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID,
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID_2);
            BulkApproveSellerAdminApiResponse response =
                    SellerAdminApplicationApiFixtures.bulkApprovePartialFailureResponse(
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID,
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID_2);

            given(mapper.toBulkApproveCommand(any())).willReturn(null);
            given(bulkApproveUseCase.execute(any())).willReturn(result);
            given(mapper.toResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + SellerAdminApplicationEndpoints.BULK_APPROVE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(1))
                    .andExpect(jsonPath("$.data.failureCount").value(1))
                    .andExpect(jsonPath("$.data.results[1].success").value(false))
                    .andExpect(jsonPath("$.data.results[1].errorCode").value("SELADM-003"));
        }
    }

    @Nested
    @DisplayName("셀러 관리자 가입 신청 일괄 거절 API")
    class BulkRejectSellerAdminTest {

        @Test
        @DisplayName("일괄 거절 성공")
        void bulkReject_Success() throws Exception {
            // given
            BulkRejectSellerAdminApiRequest request =
                    SellerAdminApplicationApiFixtures.bulkRejectRequest();

            given(mapper.toBulkRejectCommand(any())).willReturn(null);
            doNothing().when(bulkRejectUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + SellerAdminApplicationEndpoints.BULK_REJECT)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller-admin-application/bulk-reject",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("sellerAdminIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("거절할 셀러 관리자 ID 목록 (UUIDv7)"))));
        }
    }

    @Nested
    @DisplayName("셀러 관리자 비밀번호 초기화 API")
    class ResetPasswordTest {

        @Test
        @DisplayName("비밀번호 초기화 성공")
        void resetPassword_Success() throws Exception {
            // given
            given(mapper.toResetPasswordCommand(SELLER_ADMIN_ID)).willReturn(null);
            doNothing().when(resetPasswordUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + SellerAdminApplicationEndpoints.RESET_PASSWORD,
                                    SELLER_ADMIN_ID))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller-admin-application/reset-password",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerAdminId")
                                                    .description("셀러 관리자 ID (UUIDv7)"))));
        }
    }

    @Nested
    @DisplayName("셀러 관리자 비밀번호 변경 API")
    class ChangePasswordTest {

        @Test
        @DisplayName("비밀번호 변경 성공")
        void changePassword_Success() throws Exception {
            // given
            ChangeSellerAdminPasswordApiRequest request =
                    SellerAdminApplicationApiFixtures.changePasswordRequest();
            given(mapper.toChangePasswordCommand(eq(SELLER_ADMIN_ID), any())).willReturn(null);
            doNothing().when(changePasswordUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL
                                                    + SellerAdminApplicationEndpoints
                                                            .CHANGE_PASSWORD,
                                            SELLER_ADMIN_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller-admin-application/change-password",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerAdminId")
                                                    .description("셀러 관리자 ID (UUIDv7)")),
                                    requestFields(
                                            fieldWithPath("newPassword")
                                                    .type(JsonFieldType.STRING)
                                                    .description("새 비밀번호 (본인인증 완료 후 설정)"))));
        }
    }
}
