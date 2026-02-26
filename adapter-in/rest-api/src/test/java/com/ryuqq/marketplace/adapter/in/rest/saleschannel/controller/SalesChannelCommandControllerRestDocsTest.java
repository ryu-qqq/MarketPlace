package com.ryuqq.marketplace.adapter.in.rest.saleschannel.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.SalesChannelAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.RegisterSalesChannelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.UpdateSalesChannelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.mapper.SalesChannelCommandApiMapper;
import com.ryuqq.marketplace.application.saleschannel.port.in.command.RegisterSalesChannelUseCase;
import com.ryuqq.marketplace.application.saleschannel.port.in.command.UpdateSalesChannelUseCase;
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
@WebMvcTest(SalesChannelCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SalesChannelCommandController REST Docs 테스트")
class SalesChannelCommandControllerRestDocsTest {

    private static final String BASE_URL = SalesChannelAdminEndpoints.SALES_CHANNELS;
    private static final Long SALES_CHANNEL_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterSalesChannelUseCase registerSalesChannelUseCase;
    @MockitoBean private UpdateSalesChannelUseCase updateSalesChannelUseCase;
    @MockitoBean private SalesChannelCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("판매채널 등록 API")
    class RegisterSalesChannelTest {

        @Test
        @DisplayName("유효한 요청이면 201과 판매채널 ID를 반환한다")
        void registerSalesChannel_ValidRequest_Returns201() throws Exception {
            // given
            RegisterSalesChannelApiRequest request = new RegisterSalesChannelApiRequest("쿠팡");

            given(mapper.toCommand(any(RegisterSalesChannelApiRequest.class))).willReturn(null);
            given(registerSalesChannelUseCase.execute(any())).willReturn(SALES_CHANNEL_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.salesChannelId").value(SALES_CHANNEL_ID))
                    .andDo(
                            document(
                                    "sales-channel/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("channelName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("판매채널명")),
                                    responseFields(
                                            fieldWithPath("data.salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 판매채널 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("필수 필드 누락시 400 에러")
        void registerSalesChannel_MissingRequiredFields_Returns400() throws Exception {
            // given
            String invalidRequest = "{}";

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("판매채널 수정 API")
    class UpdateSalesChannelTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void updateSalesChannel_ValidRequest_Returns204() throws Exception {
            // given
            UpdateSalesChannelApiRequest request =
                    new UpdateSalesChannelApiRequest("수정된 판매채널명", "ACTIVE");

            given(mapper.toCommand(any(Long.class), any(UpdateSalesChannelApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateSalesChannelUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + "/{salesChannelId}", SALES_CHANNEL_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "sales-channel/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("salesChannelId")
                                                    .description("판매채널 ID")),
                                    requestFields(
                                            fieldWithPath("channelName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("판매채널명"),
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태 (ACTIVE, INACTIVE)"))));
        }
    }
}
