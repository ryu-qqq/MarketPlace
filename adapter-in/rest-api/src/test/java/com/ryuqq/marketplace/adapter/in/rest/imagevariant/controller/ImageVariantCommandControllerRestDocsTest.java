package com.ryuqq.marketplace.adapter.in.rest.imagevariant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.ImageVariantAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.ImageVariantApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.command.RequestImageTransformApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.mapper.ImageVariantCommandApiMapper;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.RequestImageTransformUseCase;
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
@WebMvcTest(ImageVariantCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ImageVariantCommandController REST Docs 테스트")
class ImageVariantCommandControllerRestDocsTest {

    private static final Long PRODUCT_GROUP_ID = ImageVariantApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RequestImageTransformUseCase requestImageTransformUseCase;
    @MockitoBean private ImageVariantCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("수동 이미지 Variant 변환 요청 API")
    class RequestTransformTest {

        @Test
        @DisplayName("특정 Variant 타입으로 변환 요청 성공 - 202 반환")
        void requestTransform_WithVariantTypes_Returns202() throws Exception {
            // given
            RequestImageTransformApiRequest request =
                    ImageVariantApiFixtures.requestWithVariantTypes();

            given(mapper.toCommand(any(Long.class), any(RequestImageTransformApiRequest.class)))
                    .willReturn(null);
            doNothing().when(requestImageTransformUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            ImageVariantAdminEndpoints.IMAGE_VARIANTS
                                                    + ImageVariantAdminEndpoints.TRANSFORM_REQUEST,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isAccepted())
                    .andDo(
                            document(
                                    "image-variant/request-transform",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID")),
                                    requestFields(
                                            fieldWithPath("variantTypes")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description(
                                                            "변환 대상 Variant 타입 목록"
                                                                    + " (SMALL_WEBP, MEDIUM_WEBP,"
                                                                    + " LARGE_WEBP, ORIGINAL_WEBP)."
                                                                    + " 비어있으면 전체 타입 대상.")
                                                    .optional())));
        }

        @Test
        @DisplayName("request body 없이 변환 요청 시 전체 타입 대상으로 202 반환")
        void requestTransform_WithoutRequestBody_Returns202() throws Exception {
            // given
            given(mapper.toCommand(any(Long.class), any())).willReturn(null);
            doNothing().when(requestImageTransformUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                    ImageVariantAdminEndpoints.IMAGE_VARIANTS
                                            + ImageVariantAdminEndpoints.TRANSFORM_REQUEST,
                                    PRODUCT_GROUP_ID))
                    .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("빈 variantTypes로 요청 시 전체 타입 대상으로 202 반환")
        void requestTransform_WithEmptyVariantTypes_Returns202() throws Exception {
            // given
            RequestImageTransformApiRequest request =
                    ImageVariantApiFixtures.requestWithEmptyVariantTypes();

            given(mapper.toCommand(any(Long.class), any(RequestImageTransformApiRequest.class)))
                    .willReturn(null);
            doNothing().when(requestImageTransformUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            ImageVariantAdminEndpoints.IMAGE_VARIANTS
                                                    + ImageVariantAdminEndpoints.TRANSFORM_REQUEST,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("모든 Variant 타입을 명시적으로 지정하여 변환 요청 성공")
        void requestTransform_WithAllVariantTypes_Returns202() throws Exception {
            // given
            RequestImageTransformApiRequest request =
                    ImageVariantApiFixtures.requestWithAllVariantTypes();

            given(mapper.toCommand(any(Long.class), any(RequestImageTransformApiRequest.class)))
                    .willReturn(null);
            doNothing().when(requestImageTransformUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            ImageVariantAdminEndpoints.IMAGE_VARIANTS
                                                    + ImageVariantAdminEndpoints.TRANSFORM_REQUEST,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isAccepted());
        }
    }
}
