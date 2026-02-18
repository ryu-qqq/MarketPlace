package com.ryuqq.marketplace.adapter.in.rest.imagevariant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.ImageVariantAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.ImageVariantApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.response.ImageVariantApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.mapper.ImageVariantQueryApiMapper;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.application.imagevariant.port.in.query.GetImageVariantsByImageIdUseCase;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(ImageVariantQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ImageVariantQueryController REST Docs 테스트")
class ImageVariantQueryControllerRestDocsTest {

    private static final Long PRODUCT_GROUP_ID = ImageVariantApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
    private static final Long IMAGE_ID = ImageVariantApiFixtures.DEFAULT_IMAGE_ID;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetImageVariantsByImageIdUseCase getImageVariantsByImageIdUseCase;
    @MockitoBean private ImageVariantQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("이미지 Variant 목록 조회 API")
    class GetVariantsByImageIdTest {

        @Test
        @DisplayName("이미지의 모든 Variant 목록 조회 성공")
        void getVariantsByImageId_Success() throws Exception {
            // given
            List<ImageVariantResult> results = ImageVariantApiFixtures.imageVariantResults();
            List<ImageVariantApiResponse> responses = ImageVariantApiFixtures.apiResponses();

            given(getImageVariantsByImageIdUseCase.execute(IMAGE_ID)).willReturn(results);
            given(mapper.toApiResponses(any())).willReturn(responses);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    ImageVariantAdminEndpoints.IMAGE_VARIANTS
                                            + ImageVariantAdminEndpoints.VARIANTS,
                                    PRODUCT_GROUP_ID,
                                    IMAGE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(4))
                    .andExpect(jsonPath("$.data[0].variantType").value("SMALL_WEBP"))
                    .andExpect(
                            jsonPath("$.data[0].variantUrl")
                                    .value(ImageVariantApiFixtures.DEFAULT_SMALL_URL))
                    .andExpect(jsonPath("$.data[0].width").value(300))
                    .andExpect(jsonPath("$.data[0].height").value(300))
                    .andDo(
                            document(
                                    "image-variant/get-variants",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID"),
                                            parameterWithName("imageId").description("이미지 ID")),
                                    responseFields(
                                            fieldWithPath("data[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 Variant 목록"),
                                            fieldWithPath("data[].variantType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "Variant 타입 (SMALL_WEBP, MEDIUM_WEBP,"
                                                                + " LARGE_WEBP, ORIGINAL_WEBP)"),
                                            fieldWithPath("data[].variantUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변환된 이미지 CDN URL"),
                                            fieldWithPath("data[].width")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("너비 (ORIGINAL_WEBP는 null)")
                                                    .optional(),
                                            fieldWithPath("data[].height")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("높이 (ORIGINAL_WEBP는 null)")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("SMALL_WEBP만 존재하는 경우 1건을 반환한다")
        void getVariantsByImageId_OnlySmallWebp_ReturnsSingle() throws Exception {
            // given
            List<ImageVariantResult> results =
                    ImageVariantApiFixtures.imageVariantResults(ImageVariantType.SMALL_WEBP);
            List<ImageVariantApiResponse> responses =
                    ImageVariantApiFixtures.apiResponses(ImageVariantType.SMALL_WEBP);

            given(getImageVariantsByImageIdUseCase.execute(IMAGE_ID)).willReturn(results);
            given(mapper.toApiResponses(any())).willReturn(responses);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    ImageVariantAdminEndpoints.IMAGE_VARIANTS
                                            + ImageVariantAdminEndpoints.VARIANTS,
                                    PRODUCT_GROUP_ID,
                                    IMAGE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].variantType").value("SMALL_WEBP"))
                    .andExpect(jsonPath("$.data[0].width").value(300))
                    .andExpect(jsonPath("$.data[0].height").value(300));
        }

        @Test
        @DisplayName("Variant가 없으면 빈 배열을 반환한다")
        void getVariantsByImageId_EmptyResult_ReturnsEmptyArray() throws Exception {
            // given
            given(getImageVariantsByImageIdUseCase.execute(IMAGE_ID)).willReturn(List.of());
            given(mapper.toApiResponses(any())).willReturn(List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    ImageVariantAdminEndpoints.IMAGE_VARIANTS
                                            + ImageVariantAdminEndpoints.VARIANTS,
                                    PRODUCT_GROUP_ID,
                                    IMAGE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }

        @Test
        @DisplayName("ORIGINAL_WEBP는 width/height가 null로 응답된다")
        void getVariantsByImageId_OriginalWebp_HasNullDimensions() throws Exception {
            // given
            List<ImageVariantResult> results =
                    ImageVariantApiFixtures.imageVariantResults(ImageVariantType.ORIGINAL_WEBP);
            List<ImageVariantApiResponse> responses =
                    ImageVariantApiFixtures.apiResponses(ImageVariantType.ORIGINAL_WEBP);

            given(getImageVariantsByImageIdUseCase.execute(IMAGE_ID)).willReturn(results);
            given(mapper.toApiResponses(any())).willReturn(responses);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    ImageVariantAdminEndpoints.IMAGE_VARIANTS
                                            + ImageVariantAdminEndpoints.VARIANTS,
                                    PRODUCT_GROUP_ID,
                                    IMAGE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].variantType").value("ORIGINAL_WEBP"))
                    .andExpect(jsonPath("$.data[0].width").doesNotExist())
                    .andExpect(jsonPath("$.data[0].height").doesNotExist());
        }
    }
}
