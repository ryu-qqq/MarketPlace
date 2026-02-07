package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.ShippingPolicyAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.ShippingPolicyApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.ChangeShippingPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.RegisterShippingPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.UpdateShippingPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.mapper.ShippingPolicyCommandApiMapper;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.ChangeShippingPolicyStatusUseCase;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.RegisterShippingPolicyUseCase;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.UpdateShippingPolicyUseCase;
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
@WebMvcTest(ShippingPolicyCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ShippingPolicyCommandController REST Docs 테스트")
class ShippingPolicyCommandControllerRestDocsTest {

    private static final String BASE_URL = ShippingPolicyAdminEndpoints.SHIPPING_POLICIES;
    private static final Long SELLER_ID = 1L;
    private static final Long POLICY_ID = 10L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterShippingPolicyUseCase registerUseCase;
    @MockitoBean private UpdateShippingPolicyUseCase updateUseCase;
    @MockitoBean private ChangeShippingPolicyStatusUseCase changeStatusUseCase;
    @MockitoBean private ShippingPolicyCommandApiMapper mapper;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry
            errorMapperRegistry;

    @Nested
    @DisplayName("배송정책 등록 API")
    class RegisterShippingPolicyTest {

        @Test
        @DisplayName("배송정책 등록 성공")
        void register_Success() throws Exception {
            // given
            RegisterShippingPolicyApiRequest request = ShippingPolicyApiFixtures.registerRequest();

            given(mapper.toCommand(any(Long.class), any(RegisterShippingPolicyApiRequest.class)))
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
                                    "shipping-policy/register",
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
                                            fieldWithPath("shippingFeeType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "배송비 유형 (FREE, PAID, CONDITIONAL_FREE,"
                                                                    + " QUANTITY_BASED)"),
                                            fieldWithPath("baseFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("기본 배송비 (원)"),
                                            fieldWithPath("freeThreshold")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("무료배송 기준금액 (원)")
                                                    .optional(),
                                            fieldWithPath("jejuExtraFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("제주 추가 배송비 (원)")
                                                    .optional(),
                                            fieldWithPath("islandExtraFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("도서산간 추가 배송비 (원)")
                                                    .optional(),
                                            fieldWithPath("returnFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 배송비 (원)")
                                                    .optional(),
                                            fieldWithPath("exchangeFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 배송비 (원)")
                                                    .optional(),
                                            fieldWithPath("leadTime")
                                                    .description("발송 소요일 정보")
                                                    .optional(),
                                            fieldWithPath("leadTime.minDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최소 발송일")
                                                    .optional(),
                                            fieldWithPath("leadTime.maxDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최대 발송일")
                                                    .optional(),
                                            fieldWithPath("leadTime.cutoffTime")
                                                    .type(JsonFieldType.STRING)
                                                    .description("당일발송 마감시간 (HH:mm)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.policyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 정책 ID"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("배송정책 수정 API")
    class UpdateShippingPolicyTest {

        @Test
        @DisplayName("배송정책 수정 성공")
        void update_Success() throws Exception {
            // given
            UpdateShippingPolicyApiRequest request = ShippingPolicyApiFixtures.updateRequest();

            given(
                            mapper.toCommand(
                                    any(Long.class),
                                    any(Long.class),
                                    any(UpdateShippingPolicyApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + ShippingPolicyAdminEndpoints.ID,
                                            SELLER_ID,
                                            POLICY_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "shipping-policy/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID"),
                                            parameterWithName("policyId").description("배송정책 ID")),
                                    requestFields(
                                            fieldWithPath("policyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("정책명"),
                                            fieldWithPath("defaultPolicy")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("기본 정책 여부"),
                                            fieldWithPath("shippingFeeType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "배송비 유형 (FREE, PAID, CONDITIONAL_FREE,"
                                                                    + " QUANTITY_BASED)"),
                                            fieldWithPath("baseFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("기본 배송비 (원)"),
                                            fieldWithPath("freeThreshold")
                                                    .type(JsonFieldType.NULL)
                                                    .description("무료배송 기준금액 (원)")
                                                    .optional(),
                                            fieldWithPath("jejuExtraFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("제주 추가 배송비 (원)")
                                                    .optional(),
                                            fieldWithPath("islandExtraFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("도서산간 추가 배송비 (원)")
                                                    .optional(),
                                            fieldWithPath("returnFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 배송비 (원)")
                                                    .optional(),
                                            fieldWithPath("exchangeFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 배송비 (원)")
                                                    .optional(),
                                            fieldWithPath("leadTime")
                                                    .description("발송 소요일 정보")
                                                    .optional(),
                                            fieldWithPath("leadTime.minDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최소 발송일")
                                                    .optional(),
                                            fieldWithPath("leadTime.maxDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최대 발송일")
                                                    .optional(),
                                            fieldWithPath("leadTime.cutoffTime")
                                                    .type(JsonFieldType.STRING)
                                                    .description("당일발송 마감시간 (HH:mm)")
                                                    .optional())));
        }
    }

    @Nested
    @DisplayName("배송정책 다건 상태 변경 API")
    class ChangeShippingPolicyStatusTest {

        @Test
        @DisplayName("배송정책 상태 변경 성공")
        void changeStatus_Success() throws Exception {
            // given
            ChangeShippingPolicyStatusApiRequest request =
                    ShippingPolicyApiFixtures.changeStatusRequest();

            given(
                            mapper.toCommand(
                                    any(Long.class),
                                    any(ChangeShippingPolicyStatusApiRequest.class)))
                    .willReturn(null);
            doNothing().when(changeStatusUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL + ShippingPolicyAdminEndpoints.STATUS,
                                            SELLER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "shipping-policy/change-status",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sellerId").description("셀러 ID")),
                                    requestFields(
                                            fieldWithPath("policyIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상태 변경할 정책 ID 목록"),
                                            fieldWithPath("active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description(
                                                            "변경할 활성화 상태 (true: 활성화, false:"
                                                                    + " 비활성화)"))));
        }
    }
}
