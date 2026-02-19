package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.ExternalBrandMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.ExternalBrandMappingApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.RegisterExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.UpdateExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.mapper.ExternalBrandMappingCommandApiMapper;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.BatchRegisterExternalBrandMappingUseCase;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.RegisterExternalBrandMappingUseCase;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.UpdateExternalBrandMappingUseCase;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(ExternalBrandMappingCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ExternalBrandMappingCommandController REST Docs 테스트")
class ExternalBrandMappingCommandControllerRestDocsTest {

    private static final String BASE_URL = ExternalBrandMappingAdminEndpoints.BRAND_MAPPINGS;
    private static final Long EXTERNAL_SOURCE_ID = 1L;
    private static final Long MAPPING_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterExternalBrandMappingUseCase registerUseCase;
    @MockitoBean private BatchRegisterExternalBrandMappingUseCase batchRegisterUseCase;
    @MockitoBean private UpdateExternalBrandMappingUseCase updateUseCase;
    @MockitoBean private ExternalBrandMappingCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("외부 브랜드 매핑 등록 API")
    class RegisterTest {

        @Test
        @DisplayName("유효한 요청이면 201과 ID를 반환한다")
        void register_ValidRequest_Returns201WithId() throws Exception {
            // given
            RegisterExternalBrandMappingApiRequest request =
                    ExternalBrandMappingApiFixtures.registerRequest();

            given(
                            mapper.toCommand(
                                    any(Long.class),
                                    any(RegisterExternalBrandMappingApiRequest.class)))
                    .willReturn(null);
            given(registerUseCase.execute(any())).willReturn(MAPPING_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL, EXTERNAL_SOURCE_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(MAPPING_ID))
                    .andDo(
                            document(
                                    "external-brand-mapping/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("externalSourceId")
                                                    .description("외부 소스 ID")),
                                    requestFields(
                                            fieldWithPath("externalBrandCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드 코드"),
                                            fieldWithPath("externalBrandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드명"),
                                            fieldWithPath("internalBrandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("내부 브랜드 ID")),
                                    responseFields(
                                            fieldWithPath("data.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 매핑 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("외부 브랜드 매핑 일괄 등록 API")
    class BatchRegisterTest {

        @Test
        @DisplayName("유효한 요청이면 201과 ID 목록을 반환한다")
        void batchRegister_ValidRequest_Returns201WithIds() throws Exception {
            // given
            BatchRegisterExternalBrandMappingApiRequest request =
                    ExternalBrandMappingApiFixtures.batchRegisterRequest();

            given(
                            mapper.toBatchCommand(
                                    any(Long.class),
                                    any(BatchRegisterExternalBrandMappingApiRequest.class)))
                    .willReturn(null);
            given(batchRegisterUseCase.execute(any())).willReturn(List.of(1L, 2L));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + ExternalBrandMappingAdminEndpoints.BATCH,
                                            EXTERNAL_SOURCE_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andDo(
                            document(
                                    "external-brand-mapping/batch-register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("externalSourceId")
                                                    .description("외부 소스 ID")),
                                    requestFields(
                                            fieldWithPath("entries[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("매핑 항목 목록"),
                                            fieldWithPath("entries[].externalBrandCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드 코드"),
                                            fieldWithPath("entries[].externalBrandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드명"),
                                            fieldWithPath("entries[].internalBrandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("내부 브랜드 ID")),
                                    responseFields(
                                            fieldWithPath("data[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("생성된 매핑 ID 목록"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("외부 브랜드 매핑 수정 API")
    class UpdateTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void update_ValidRequest_Returns204() throws Exception {
            // given
            UpdateExternalBrandMappingApiRequest request =
                    ExternalBrandMappingApiFixtures.updateRequest();

            given(
                            mapper.toCommand(
                                    any(Long.class),
                                    any(UpdateExternalBrandMappingApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL
                                                    + ExternalBrandMappingAdminEndpoints
                                                            .BRAND_MAPPING_ID,
                                            EXTERNAL_SOURCE_ID,
                                            MAPPING_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "external-brand-mapping/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("externalSourceId")
                                                    .description("외부 소스 ID"),
                                            parameterWithName("id").description("매핑 ID")),
                                    requestFields(
                                            fieldWithPath("externalBrandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드명")
                                                    .optional(),
                                            fieldWithPath("internalBrandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("내부 브랜드 ID")
                                                    .optional(),
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태 (ACTIVE, INACTIVE)")
                                                    .optional())));
        }
    }
}
