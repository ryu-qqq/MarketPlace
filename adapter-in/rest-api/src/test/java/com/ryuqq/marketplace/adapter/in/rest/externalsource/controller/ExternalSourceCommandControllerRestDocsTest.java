package com.ryuqq.marketplace.adapter.in.rest.externalsource.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.externalsource.ExternalSourceAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.ExternalSourceApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command.RegisterExternalSourceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command.UpdateExternalSourceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.mapper.ExternalSourceCommandApiMapper;
import com.ryuqq.marketplace.application.externalsource.port.in.command.RegisterExternalSourceUseCase;
import com.ryuqq.marketplace.application.externalsource.port.in.command.UpdateExternalSourceUseCase;
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
@WebMvcTest(ExternalSourceCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ExternalSourceCommandController REST Docs 테스트")
class ExternalSourceCommandControllerRestDocsTest {

    private static final String BASE_URL = ExternalSourceAdminEndpoints.EXTERNAL_SOURCES;
    private static final Long EXTERNAL_SOURCE_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterExternalSourceUseCase registerExternalSourceUseCase;
    @MockitoBean private UpdateExternalSourceUseCase updateExternalSourceUseCase;
    @MockitoBean private ExternalSourceCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("외부 소스 등록 API")
    class RegisterTest {

        @Test
        @DisplayName("유효한 요청이면 201과 ID를 반환한다")
        void register_ValidRequest_Returns201WithId() throws Exception {
            // given
            RegisterExternalSourceApiRequest request = ExternalSourceApiFixtures.registerRequest();

            given(mapper.toCommand(any(RegisterExternalSourceApiRequest.class))).willReturn(null);
            given(registerExternalSourceUseCase.execute(any())).willReturn(EXTERNAL_SOURCE_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.externalSourceId").value(EXTERNAL_SOURCE_ID))
                    .andDo(
                            document(
                                    "external-source/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 소스 코드"),
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 소스명"),
                                            fieldWithPath("type")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "외부 소스 유형 (SALES_CHANNEL, CRAWLING,"
                                                                    + " LEGACY, PARTNER)"),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.externalSourceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 외부 소스 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("외부 소스 수정 API")
    class UpdateTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void update_ValidRequest_Returns204() throws Exception {
            // given
            UpdateExternalSourceApiRequest request = ExternalSourceApiFixtures.updateRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateExternalSourceApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateExternalSourceUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL
                                                    + ExternalSourceAdminEndpoints
                                                            .EXTERNAL_SOURCE_ID,
                                            EXTERNAL_SOURCE_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "external-source/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("externalSourceId")
                                                    .description("외부 소스 ID")),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 소스명")
                                                    .optional(),
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태 (ACTIVE, INACTIVE)")
                                                    .optional(),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명")
                                                    .optional())));
        }
    }
}
