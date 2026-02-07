package com.ryuqq.marketplace.adapter.in.rest.selleradmin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
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
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ApplySellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkApproveSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkRejectSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ChangeSellerAdminPasswordApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.BulkApproveSellerAdminApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.mapper.SellerAdminApplicationCommandApiMapper;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApplySellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkRejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ChangeSellerAdminPasswordCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ResetSellerAdminPasswordCommand;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ApplySellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ApproveSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.BulkApproveSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.BulkRejectSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ChangeSellerAdminPasswordUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.RejectSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ResetSellerAdminPasswordUseCase;
import java.util.List;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(SellerAdminApplicationCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SellerAdminApplicationCommandController 단위 테스트")
class SellerAdminApplicationCommandControllerTest {

    private static final String BASE_URL = "/api/v1/market/seller-admin-applications";
    private static final String SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;
    @MockitoBean private ApplySellerAdminUseCase applyUseCase;
    @MockitoBean private ApproveSellerAdminUseCase approveUseCase;
    @MockitoBean private RejectSellerAdminUseCase rejectUseCase;
    @MockitoBean private BulkApproveSellerAdminUseCase bulkApproveUseCase;
    @MockitoBean private BulkRejectSellerAdminUseCase bulkRejectUseCase;
    @MockitoBean private ResetSellerAdminPasswordUseCase resetPasswordUseCase;
    @MockitoBean private ChangeSellerAdminPasswordUseCase changePasswordUseCase;
    @MockitoBean private SellerAdminApplicationCommandApiMapper mapper;

    @Nested
    @DisplayName("POST /api/v1/market/seller-admin-applications - 가입 신청")
    class ApplyTest {

        @Test
        @DisplayName("유효한 요청이면 201과 생성된 ID를 반환한다")
        void apply_ValidRequest_Returns201WithId() throws Exception {
            // given
            ApplySellerAdminApiRequest request = SellerAdminApplicationApiFixtures.applyRequest();
            ApplySellerAdminCommand command =
                    new ApplySellerAdminCommand(
                            1L, "admin@example.com", "홍길동", "010-1234-5678", "Password123!");

            given(mapper.toCommand(any(ApplySellerAdminApiRequest.class))).willReturn(command);
            given(applyUseCase.execute(command)).willReturn(SELLER_ADMIN_ID);

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
                                            fieldWithPath("sellerId").description("가입할 셀러 ID"),
                                            fieldWithPath("loginId").description("로그인 ID (이메일 형식)"),
                                            fieldWithPath("name").description("관리자 이름"),
                                            fieldWithPath("phoneNumber").description("휴대폰 번호"),
                                            fieldWithPath("password").description("비밀번호")),
                                    responseFields(
                                            fieldWithPath("data.sellerAdminId")
                                                    .description("생성된 셀러 관리자 ID"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));

            then(mapper).should().toCommand(any(ApplySellerAdminApiRequest.class));
            then(applyUseCase).should().execute(command);
        }

        @Test
        @DisplayName("sellerId가 null이면 400을 반환한다")
        void apply_NullSellerId_Returns400() throws Exception {
            // given
            ApplySellerAdminApiRequest request =
                    new ApplySellerAdminApiRequest(
                            null, "admin@example.com", "홍길동", "010-1234-5678", "Password123!");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("loginId가 빈 문자열이면 400을 반환한다")
        void apply_BlankLoginId_Returns400() throws Exception {
            // given
            ApplySellerAdminApiRequest request =
                    new ApplySellerAdminApiRequest(1L, "", "홍길동", "010-1234-5678", "Password123!");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("loginId가 이메일 형식이 아니면 400을 반환한다")
        void apply_InvalidEmail_Returns400() throws Exception {
            // given
            ApplySellerAdminApiRequest request =
                    new ApplySellerAdminApiRequest(
                            1L, "not-email", "홍길동", "010-1234-5678", "Password123!");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("name이 빈 문자열이면 400을 반환한다")
        void apply_BlankName_Returns400() throws Exception {
            // given
            ApplySellerAdminApiRequest request =
                    new ApplySellerAdminApiRequest(
                            1L, "admin@example.com", "", "010-1234-5678", "Password123!");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("phoneNumber가 빈 문자열이면 400을 반환한다")
        void apply_BlankPhoneNumber_Returns400() throws Exception {
            // given
            ApplySellerAdminApiRequest request =
                    new ApplySellerAdminApiRequest(
                            1L, "admin@example.com", "홍길동", "", "Password123!");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("password가 8자 미만이면 400을 반환한다")
        void apply_ShortPassword_Returns400() throws Exception {
            // given
            ApplySellerAdminApiRequest request =
                    new ApplySellerAdminApiRequest(
                            1L, "admin@example.com", "홍길동", "010-1234-5678", "short");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/market/seller-admin-applications/{sellerAdminId}/approve - 승인")
    class ApproveTest {

        @Test
        @DisplayName("유효한 요청이면 200과 승인 결과를 반환한다")
        void approve_ValidRequest_Returns200() throws Exception {
            // given
            ApproveSellerAdminCommand command = new ApproveSellerAdminCommand(SELLER_ADMIN_ID);

            given(mapper.toApproveCommand(SELLER_ADMIN_ID)).willReturn(command);
            given(approveUseCase.execute(command)).willReturn(SELLER_ADMIN_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + "/{sellerAdminId}/approve", SELLER_ADMIN_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.sellerAdminId").value(SELLER_ADMIN_ID))
                    .andDo(
                            document(
                                    "seller-admin-application/approve",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerAdminId")
                                                    .description("셀러 관리자 ID")),
                                    responseFields(
                                            fieldWithPath("data.sellerAdminId")
                                                    .description("승인된 셀러 관리자 ID"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));

            then(mapper).should().toApproveCommand(SELLER_ADMIN_ID);
            then(approveUseCase).should().execute(command);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/market/seller-admin-applications/{sellerAdminId}/reject - 거절")
    class RejectTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void reject_ValidRequest_Returns204() throws Exception {
            // given
            RejectSellerAdminCommand command = new RejectSellerAdminCommand(SELLER_ADMIN_ID);

            given(mapper.toRejectCommand(SELLER_ADMIN_ID)).willReturn(command);
            willDoNothing().given(rejectUseCase).execute(command);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + "/{sellerAdminId}/reject", SELLER_ADMIN_ID))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller-admin-application/reject",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerAdminId")
                                                    .description("셀러 관리자 ID"))));

            then(mapper).should().toRejectCommand(SELLER_ADMIN_ID);
            then(rejectUseCase).should().execute(command);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/market/seller-admin-applications/bulk-approve - 일괄 승인")
    class BulkApproveTest {

        @Test
        @DisplayName("유효한 요청이면 200과 일괄 승인 결과를 반환한다")
        void bulkApprove_ValidRequest_Returns200() throws Exception {
            // given
            BulkApproveSellerAdminApiRequest request =
                    SellerAdminApplicationApiFixtures.bulkApproveRequest();
            BulkApproveSellerAdminCommand command =
                    new BulkApproveSellerAdminCommand(request.sellerAdminIds());
            BatchProcessingResult<String> batchResult =
                    SellerAdminApplicationApiFixtures.allSuccessBatchResult(
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID,
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID_2);
            BulkApproveSellerAdminApiResponse apiResponse =
                    SellerAdminApplicationApiFixtures.bulkApproveAllSuccessResponse(
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID,
                            SellerAdminApplicationApiFixtures.DEFAULT_SELLER_ADMIN_ID_2);

            given(mapper.toBulkApproveCommand(any(BulkApproveSellerAdminApiRequest.class)))
                    .willReturn(command);
            given(bulkApproveUseCase.execute(command)).willReturn(batchResult);
            given(mapper.toResponse(any(BatchProcessingResult.class))).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL + "/bulk-approve")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andExpect(jsonPath("$.data.results").isArray())
                    .andExpect(jsonPath("$.data.results.length()").value(2))
                    .andDo(
                            document(
                                    "seller-admin-application/bulk-approve",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("sellerAdminIds")
                                                    .description("승인할 셀러 관리자 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.totalCount").description("총 처리 건수"),
                                            fieldWithPath("data.successCount").description("성공 건수"),
                                            fieldWithPath("data.failureCount").description("실패 건수"),
                                            fieldWithPath("data.results[]")
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].sellerAdminId")
                                                    .description("셀러 관리자 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .description("에러 코드 (실패 시)"),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .description("에러 메시지 (실패 시)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));

            then(mapper).should().toBulkApproveCommand(any(BulkApproveSellerAdminApiRequest.class));
            then(bulkApproveUseCase).should().execute(command);
        }

        @Test
        @DisplayName("sellerAdminIds가 비어있으면 400을 반환한다")
        void bulkApprove_EmptyIds_Returns400() throws Exception {
            // given
            BulkApproveSellerAdminApiRequest request =
                    new BulkApproveSellerAdminApiRequest(List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL + "/bulk-approve")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/market/seller-admin-applications/bulk-reject - 일괄 거절")
    class BulkRejectTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void bulkReject_ValidRequest_Returns204() throws Exception {
            // given
            BulkRejectSellerAdminApiRequest request =
                    SellerAdminApplicationApiFixtures.bulkRejectRequest();
            BulkRejectSellerAdminCommand command =
                    new BulkRejectSellerAdminCommand(request.sellerAdminIds());

            given(mapper.toBulkRejectCommand(any(BulkRejectSellerAdminApiRequest.class)))
                    .willReturn(command);
            willDoNothing().given(bulkRejectUseCase).execute(command);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL + "/bulk-reject")
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
                                                    .description("거절할 셀러 관리자 ID 목록"))));

            then(mapper).should().toBulkRejectCommand(any(BulkRejectSellerAdminApiRequest.class));
            then(bulkRejectUseCase).should().execute(command);
        }

        @Test
        @DisplayName("sellerAdminIds가 비어있으면 400을 반환한다")
        void bulkReject_EmptyIds_Returns400() throws Exception {
            // given
            BulkRejectSellerAdminApiRequest request =
                    new BulkRejectSellerAdminApiRequest(List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL + "/bulk-reject")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName(
            "POST /api/v1/market/seller-admin-applications/{sellerAdminId}/reset-password -"
                    + " 비밀번호 초기화")
    class ResetPasswordTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void resetPassword_ValidRequest_Returns204() throws Exception {
            // given
            ResetSellerAdminPasswordCommand command =
                    new ResetSellerAdminPasswordCommand(SELLER_ADMIN_ID);

            given(mapper.toResetPasswordCommand(SELLER_ADMIN_ID)).willReturn(command);
            willDoNothing().given(resetPasswordUseCase).execute(command);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    BASE_URL + "/{sellerAdminId}/reset-password", SELLER_ADMIN_ID))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "seller-admin-application/reset-password",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerAdminId")
                                                    .description("셀러 관리자 ID"))));

            then(mapper).should().toResetPasswordCommand(SELLER_ADMIN_ID);
            then(resetPasswordUseCase).should().execute(command);
        }
    }

    @Nested
    @DisplayName(
            "PATCH /api/v1/market/seller-admin-applications/{sellerAdminId}/change-password -"
                    + " 비밀번호 변경")
    class ChangePasswordTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void changePassword_ValidRequest_Returns204() throws Exception {
            // given
            ChangeSellerAdminPasswordApiRequest request =
                    SellerAdminApplicationApiFixtures.changePasswordRequest();
            ChangeSellerAdminPasswordCommand command =
                    new ChangeSellerAdminPasswordCommand(SELLER_ADMIN_ID, "NewPass123!");

            given(mapper.toChangePasswordCommand(eq(SELLER_ADMIN_ID), any())).willReturn(command);
            willDoNothing().given(changePasswordUseCase).execute(command);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL + "/{sellerAdminId}/change-password",
                                            SELLER_ADMIN_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            then(mapper).should().toChangePasswordCommand(eq(SELLER_ADMIN_ID), any());
            then(changePasswordUseCase).should().execute(command);
        }

        @Test
        @DisplayName("newPassword가 빈 문자열이면 400을 반환한다")
        void changePassword_BlankNewPassword_Returns400() throws Exception {
            // given
            ChangeSellerAdminPasswordApiRequest request =
                    SellerAdminApplicationApiFixtures.changePasswordRequest("");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL + "/{sellerAdminId}/change-password",
                                            SELLER_ADMIN_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("newPassword가 8자 미만이면 400을 반환한다")
        void changePassword_ShortNewPassword_Returns400() throws Exception {
            // given
            ChangeSellerAdminPasswordApiRequest request =
                    SellerAdminApplicationApiFixtures.changePasswordRequest("short");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL + "/{sellerAdminId}/change-password",
                                            SELLER_ADMIN_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
