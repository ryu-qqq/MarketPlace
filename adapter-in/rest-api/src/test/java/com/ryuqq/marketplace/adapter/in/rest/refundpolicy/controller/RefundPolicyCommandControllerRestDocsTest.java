package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.RefundPolicyAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.RefundPolicyApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.ChangeRefundPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.RegisterRefundPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.UpdateRefundPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.mapper.RefundPolicyCommandApiMapper;
import com.ryuqq.marketplace.application.refundpolicy.port.in.command.ChangeRefundPolicyStatusUseCase;
import com.ryuqq.marketplace.application.refundpolicy.port.in.command.RegisterRefundPolicyUseCase;
import com.ryuqq.marketplace.application.refundpolicy.port.in.command.UpdateRefundPolicyUseCase;
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
@WebMvcTest(RefundPolicyCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("RefundPolicyCommandController REST Docs 테스트")
class RefundPolicyCommandControllerRestDocsTest {

    private static final String BASE_URL = RefundPolicyAdminEndpoints.REFUND_POLICIES;
    private static final Long SELLER_ID = 1L;
    private static final Long POLICY_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterRefundPolicyUseCase registerUseCase;
    @MockitoBean private UpdateRefundPolicyUseCase updateUseCase;
    @MockitoBean private ChangeRefundPolicyStatusUseCase changeStatusUseCase;
    @MockitoBean private RefundPolicyCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("환불정책 등록 API")
    class RegisterTest {

        @Test
        @DisplayName("등록 성공")
        void register_Success() throws Exception {
            // given
            RegisterRefundPolicyApiRequest request = RefundPolicyApiFixtures.registerRequest();

            given(mapper.toCommand(any(Long.class), any(RegisterRefundPolicyApiRequest.class)))
                    .willReturn(null);
            given(registerUseCase.execute(any())).willReturn(POLICY_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL, SELLER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.policyId").value(POLICY_ID))
                    .andDo(
                            document(
                                    "refund-policy/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    requestFields(
                                            fieldWithPath("policyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("정책명"),
                                            fieldWithPath("defaultPolicy")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("기본 정책 여부"),
                                            fieldWithPath("returnPeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 가능 기간 (1~90일)"),
                                            fieldWithPath("exchangePeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 가능 기간 (1~90일)"),
                                            fieldWithPath("nonReturnableConditions")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("반품 불가 조건 목록"),
                                            fieldWithPath("partialRefundEnabled")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("부분 환불 허용 여부"),
                                            fieldWithPath("inspectionRequired")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("검수 필요 여부"),
                                            fieldWithPath("inspectionPeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("검수 기간 (일)"),
                                            fieldWithPath("additionalInfo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("추가 안내사항")),
                                    responseFields(
                                            fieldWithPath("data.policyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 정책 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("환불정책 수정 API")
    class UpdateTest {

        @Test
        @DisplayName("수정 성공")
        void update_Success() throws Exception {
            // given
            UpdateRefundPolicyApiRequest request = RefundPolicyApiFixtures.updateRequest();

            given(
                            mapper.toCommand(
                                    any(Long.class),
                                    any(Long.class),
                                    any(UpdateRefundPolicyApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + RefundPolicyAdminEndpoints.ID,
                                            SELLER_ID,
                                            POLICY_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "refund-policy/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID"),
                                            parameterWithName("policyId").description("환불정책 ID")),
                                    requestFields(
                                            fieldWithPath("policyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("정책명"),
                                            fieldWithPath("defaultPolicy")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("기본 정책 여부"),
                                            fieldWithPath("returnPeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 가능 기간"),
                                            fieldWithPath("exchangePeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 가능 기간"),
                                            fieldWithPath("nonReturnableConditions")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("반품 불가 조건 목록"),
                                            fieldWithPath("partialRefundEnabled")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("부분 환불 허용 여부"),
                                            fieldWithPath("inspectionRequired")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("검수 필요 여부"),
                                            fieldWithPath("inspectionPeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("검수 기간"),
                                            fieldWithPath("additionalInfo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("추가 안내사항"))));
        }
    }

    @Nested
    @DisplayName("환불정책 상태 변경 API")
    class ChangeStatusTest {

        @Test
        @DisplayName("상태 변경 성공")
        void changeStatus_Success() throws Exception {
            // given
            ChangeRefundPolicyStatusApiRequest request =
                    RefundPolicyApiFixtures.changeStatusRequest();

            given(mapper.toCommand(any(Long.class), any(ChangeRefundPolicyStatusApiRequest.class)))
                    .willReturn(null);
            doNothing().when(changeStatusUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL + RefundPolicyAdminEndpoints.STATUS, SELLER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "refund-policy/change-status",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    requestFields(
                                            fieldWithPath("policyIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("정책 ID 목록"),
                                            fieldWithPath("active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description(
                                                            "활성화 여부 (true: 활성화, false: 비활성화)"))));
        }
    }
}
