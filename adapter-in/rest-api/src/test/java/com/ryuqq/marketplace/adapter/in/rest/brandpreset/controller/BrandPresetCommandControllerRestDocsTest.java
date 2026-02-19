package com.ryuqq.marketplace.adapter.in.rest.brandpreset.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.BrandPresetAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.BrandPresetApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.DeleteBrandPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.RegisterBrandPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.UpdateBrandPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.mapper.BrandPresetCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.DeleteBrandPresetsUseCase;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.RegisterBrandPresetUseCase;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.UpdateBrandPresetUseCase;
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
@WebMvcTest(BrandPresetCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("BrandPresetCommandController REST Docs 테스트")
class BrandPresetCommandControllerRestDocsTest {

    private static final String BASE_URL = BrandPresetAdminEndpoints.BRAND_PRESETS;
    private static final Long BRAND_PRESET_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterBrandPresetUseCase registerBrandPresetUseCase;
    @MockitoBean private UpdateBrandPresetUseCase updateBrandPresetUseCase;
    @MockitoBean private DeleteBrandPresetsUseCase deleteBrandPresetsUseCase;
    @MockitoBean private BrandPresetCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("브랜드 프리셋 등록 API")
    class RegisterBrandPresetTest {

        @Test
        @DisplayName("유효한 요청이면 201과 프리셋 ID를 반환한다")
        void registerBrandPreset_ValidRequest_Returns201() throws Exception {
            // given
            RegisterBrandPresetApiRequest request = BrandPresetApiFixtures.registerRequest();

            given(mapper.toCommand(any(RegisterBrandPresetApiRequest.class))).willReturn(null);
            given(registerBrandPresetUseCase.execute(any())).willReturn(BRAND_PRESET_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(BRAND_PRESET_ID))
                    .andDo(
                            document(
                                    "brand-preset/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("Shop ID"),
                                            fieldWithPath("salesChannelBrandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 브랜드 ID"),
                                            fieldWithPath("presetName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("프리셋 이름"),
                                            fieldWithPath("internalBrandIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("매핑할 내부 브랜드 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 프리셋 ID"),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description("등록일")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("필수 필드 누락시 400 에러")
        void registerBrandPreset_MissingRequiredFields_Returns400() throws Exception {
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
    @DisplayName("브랜드 프리셋 수정 API")
    class UpdateBrandPresetTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void updateBrandPreset_ValidRequest_Returns204() throws Exception {
            // given
            UpdateBrandPresetApiRequest request = BrandPresetApiFixtures.updateRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateBrandPresetApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateBrandPresetUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + "/{brandPresetId}", BRAND_PRESET_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "brand-preset/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("brandPresetId")
                                                    .description("브랜드 프리셋 ID")),
                                    requestFields(
                                            fieldWithPath("presetName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("프리셋 이름")
                                                    .optional(),
                                            fieldWithPath("salesChannelBrandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 브랜드 ID")
                                                    .optional(),
                                            fieldWithPath("internalBrandIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("매핑할 내부 브랜드 ID 목록")
                                                    .optional())));
        }
    }

    @Nested
    @DisplayName("브랜드 프리셋 삭제 API")
    class DeleteBrandPresetsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 삭제 건수를 반환한다")
        void deleteBrandPresets_ValidRequest_Returns200() throws Exception {
            // given
            DeleteBrandPresetsApiRequest request = BrandPresetApiFixtures.deleteRequest();

            given(mapper.toDeleteCommand(any())).willReturn(null);
            given(deleteBrandPresetsUseCase.execute(any())).willReturn(3);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.delete(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.deletedCount").value(3))
                    .andDo(
                            document(
                                    "brand-preset/delete",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("ids")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("삭제할 프리셋 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.deletedCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("삭제된 프리셋 수"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
