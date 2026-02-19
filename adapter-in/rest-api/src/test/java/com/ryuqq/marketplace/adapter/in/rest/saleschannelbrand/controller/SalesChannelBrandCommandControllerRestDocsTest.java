package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.SalesChannelBrandApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.command.RegisterSalesChannelBrandApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.mapper.SalesChannelBrandCommandApiMapper;
import com.ryuqq.marketplace.application.saleschannelbrand.port.in.command.RegisterSalesChannelBrandUseCase;
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
@WebMvcTest(SalesChannelBrandCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("SalesChannelBrandCommandController REST Docs 테스트")
class SalesChannelBrandCommandControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/market/sales-channels/{salesChannelId}/brands";
    private static final Long SALES_CHANNEL_ID = 1L;
    private static final Long BRAND_ID = 100L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterSalesChannelBrandUseCase registerUseCase;
    @MockitoBean private SalesChannelBrandCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("외부채널 브랜드 등록 API")
    class RegisterBrandTest {

        @Test
        @DisplayName("외부채널 브랜드 등록 성공")
        void registerBrand_Success() throws Exception {
            // given
            RegisterSalesChannelBrandApiRequest request =
                    SalesChannelBrandApiFixtures.registerRequest();

            given(mapper.toCommand(any(Long.class), any(RegisterSalesChannelBrandApiRequest.class)))
                    .willReturn(null);
            given(registerUseCase.execute(any())).willReturn(BRAND_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL, SALES_CHANNEL_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.brandIds[0]").value(BRAND_ID))
                    .andDo(
                            document(
                                    "sales-channel-brand/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("salesChannelId")
                                                    .description("판매채널 ID")),
                                    requestFields(
                                            fieldWithPath("externalBrandCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드 코드"),
                                            fieldWithPath("externalBrandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드명")),
                                    responseFields(
                                            fieldWithPath("data.brandIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("생성된 브랜드 ID 목록"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("필수 필드 누락시 400 에러")
        void registerBrand_MissingRequiredFields_Returns400() throws Exception {
            // given
            String invalidRequest = "{}";

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL, SALES_CHANNEL_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest());
        }
    }
}
