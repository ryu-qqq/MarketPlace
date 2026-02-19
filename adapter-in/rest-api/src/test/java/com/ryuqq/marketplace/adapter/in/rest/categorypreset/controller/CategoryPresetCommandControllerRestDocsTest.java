package com.ryuqq.marketplace.adapter.in.rest.categorypreset.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.CategoryPresetAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.CategoryPresetApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.DeleteCategoryPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.RegisterCategoryPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.UpdateCategoryPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.mapper.CategoryPresetCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.DeleteCategoryPresetsUseCase;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.RegisterCategoryPresetUseCase;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.UpdateCategoryPresetUseCase;
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
@WebMvcTest(CategoryPresetCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CategoryPresetCommandController REST Docs 테스트")
class CategoryPresetCommandControllerRestDocsTest {

    private static final String BASE_URL = CategoryPresetAdminEndpoints.CATEGORY_PRESETS;
    private static final Long CATEGORY_PRESET_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterCategoryPresetUseCase registerCategoryPresetUseCase;
    @MockitoBean private UpdateCategoryPresetUseCase updateCategoryPresetUseCase;
    @MockitoBean private DeleteCategoryPresetsUseCase deleteCategoryPresetsUseCase;
    @MockitoBean private CategoryPresetCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("카테고리 프리셋 등록 API")
    class RegisterCategoryPresetTest {

        @Test
        @DisplayName("유효한 요청이면 201과 프리셋 ID를 반환한다")
        void registerCategoryPreset_ValidRequest_Returns201() throws Exception {
            // given
            RegisterCategoryPresetApiRequest request = CategoryPresetApiFixtures.registerRequest();

            given(mapper.toRegisterCommand(any(RegisterCategoryPresetApiRequest.class)))
                    .willReturn(null);
            given(registerCategoryPresetUseCase.execute(any())).willReturn(CATEGORY_PRESET_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(CATEGORY_PRESET_ID))
                    .andDo(
                            document(
                                    "category-preset/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("Shop ID"),
                                            fieldWithPath("presetName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("프리셋 이름"),
                                            fieldWithPath("categoryCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 카테고리 코드"),
                                            fieldWithPath("internalCategoryIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("매핑할 내부 카테고리 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 프리셋 ID"),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description("등록일")
                                                    .optional(),
                                            fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                            fieldWithPath("requestId").type(JsonFieldType.STRING).description("요청 ID"))));
        }

        @Test
        @DisplayName("필수 필드 누락시 400 에러")
        void registerCategoryPreset_MissingRequiredFields_Returns400() throws Exception {
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
    @DisplayName("카테고리 프리셋 수정 API")
    class UpdateCategoryPresetTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void updateCategoryPreset_ValidRequest_Returns204() throws Exception {
            // given
            UpdateCategoryPresetApiRequest request = CategoryPresetApiFixtures.updateRequest();

            given(
                            mapper.toUpdateCommand(
                                    any(Long.class), any(UpdateCategoryPresetApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateCategoryPresetUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + "/{categoryPresetId}", CATEGORY_PRESET_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "category-preset/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("categoryPresetId")
                                                    .description("카테고리 프리셋 ID")),
                                    requestFields(
                                            fieldWithPath("presetName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("프리셋 이름")
                                                    .optional(),
                                            fieldWithPath("categoryCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 카테고리 코드")
                                                    .optional(),
                                            fieldWithPath("internalCategoryIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("매핑할 내부 카테고리 ID 목록")
                                                    .optional())));
        }
    }

    @Nested
    @DisplayName("카테고리 프리셋 삭제 API")
    class DeleteCategoryPresetsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 삭제 건수를 반환한다")
        void deleteCategoryPresets_ValidRequest_Returns200() throws Exception {
            // given
            DeleteCategoryPresetsApiRequest request = CategoryPresetApiFixtures.deleteRequest();

            given(mapper.toDeleteCommand(any(DeleteCategoryPresetsApiRequest.class)))
                    .willReturn(null);
            given(deleteCategoryPresetsUseCase.execute(any())).willReturn(3);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.delete(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.deletedCount").value(3))
                    .andDo(
                            document(
                                    "category-preset/delete",
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
                                            fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                            fieldWithPath("requestId").type(JsonFieldType.STRING).description("요청 ID"))));
        }
    }
}
