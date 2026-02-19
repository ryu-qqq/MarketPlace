package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.ProductGroupImageAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.ProductGroupImageApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.command.UpdateProductGroupImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.mapper.ProductGroupImageCommandApiMapper;
import com.ryuqq.marketplace.application.productgroupimage.port.in.command.UpdateProductGroupImagesUseCase;
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
@WebMvcTest(ProductGroupImageCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ProductGroupImageCommandController REST Docs 테스트")
class ProductGroupImageCommandControllerRestDocsTest {

    private static final String BASE_URL = ProductGroupImageAdminEndpoints.PRODUCT_GROUPS;
    private static final Long PRODUCT_GROUP_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UpdateProductGroupImagesUseCase updateImagesUseCase;
    @MockitoBean private ProductGroupImageCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("상품 그룹 이미지 수정 API")
    class UpdateImagesTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void updateImages_ValidRequest_Returns204() throws Exception {
            // given
            UpdateProductGroupImagesApiRequest request =
                    ProductGroupImageApiFixtures.updateRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateProductGroupImagesApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateImagesUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL
                                                    + ProductGroupImageAdminEndpoints.ID
                                                    + ProductGroupImageAdminEndpoints.IMAGES,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product-group-image/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID")),
                                    requestFields(
                                            fieldWithPath("images")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("수정할 이미지 목록"),
                                            fieldWithPath("images[].imageType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 유형 (THUMBNAIL, DETAIL)"),
                                            fieldWithPath("images[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 이미지 URL"),
                                            fieldWithPath("images[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"))));
        }
    }
}
