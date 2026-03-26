package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.LegacyImageApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.mapper.LegacyImageCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.productgroupimage.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.legacy.productgroupimage.port.in.command.LegacyProductUpdateImagesUseCase;
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
@WebMvcTest(LegacyProductGroupImageCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacyProductGroupImageCommandController REST Docs 테스트")
class LegacyProductGroupImageCommandControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/legacy/product/group/{productGroupId}/images";
    private static final long PRODUCT_GROUP_ID = LegacyImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LegacyProductUpdateImagesUseCase legacyProductUpdateImagesUseCase;
    @MockitoBean private LegacyImageCommandApiMapper legacyImageCommandApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private LegacyAccessChecker legacyAccessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("레거시 상품그룹 이미지 수정 API")
    class UpdateProductImagesTest {

        @Test
        @DisplayName("이미지 목록 수정 성공")
        void updateProductImages_Success() throws Exception {
            // given
            List<LegacyCreateProductImageRequest> request = LegacyImageApiFixtures.requestList();
            LegacyUpdateImagesCommand command = LegacyImageApiFixtures.command(PRODUCT_GROUP_ID);

            given(legacyImageCommandApiMapper.toLegacyUpdateImagesCommand(anyLong(), any()))
                    .willReturn(command);
            doNothing().when(legacyProductUpdateImagesUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(BASE_URL, PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andExpect(jsonPath("$.response.message").value("success"))
                    .andDo(
                            document(
                                    "legacy-image/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("수정할 상품그룹 ID")),
                                    requestFields(
                                            fieldWithPath("[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 목록"),
                                            fieldWithPath("[].type")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "이미지 타입 (예: THUMBNAIL, DETAIL) (필수)"),
                                            fieldWithPath("[].productImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 이미지 URL (필수, 최대 500자)"),
                                            fieldWithPath("[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 이미지 URL (필수, 최대 500자)")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정된 상품그룹 ID"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }

        @Test
        @DisplayName("단일 이미지 수정도 성공")
        void updateProductImages_SingleImage_Success() throws Exception {
            // given
            List<LegacyCreateProductImageRequest> request =
                    LegacyImageApiFixtures.singleRequestList();
            LegacyUpdateImagesCommand command =
                    LegacyImageApiFixtures.singleImageCommand(PRODUCT_GROUP_ID);

            given(legacyImageCommandApiMapper.toLegacyUpdateImagesCommand(anyLong(), any()))
                    .willReturn(command);
            doNothing().when(legacyProductUpdateImagesUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(BASE_URL, PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(PRODUCT_GROUP_ID));
        }
    }
}
