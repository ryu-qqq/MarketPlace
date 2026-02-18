package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.ProductGroupImageAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.ProductGroupImageApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.response.ProductGroupImageUploadStatusApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.mapper.ProductGroupImageQueryApiMapper;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import com.ryuqq.marketplace.application.productgroupimage.port.in.query.GetProductGroupImageUploadStatusUseCase;
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
@WebMvcTest(ProductGroupImageQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ProductGroupImageQueryController REST Docs 테스트")
class ProductGroupImageQueryControllerRestDocsTest {

    private static final String BASE_URL = ProductGroupImageAdminEndpoints.PRODUCT_GROUPS;
    private static final Long PRODUCT_GROUP_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetProductGroupImageUploadStatusUseCase getUploadStatusUseCase;
    @MockitoBean private ProductGroupImageQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("상품 그룹 이미지 업로드 상태 조회 API")
    class GetImageUploadStatusTest {

        @Test
        @DisplayName("유효한 ID로 조회하면 200과 업로드 상태를 반환한다")
        void getUploadStatus_ValidId_Returns200() throws Exception {
            // given
            ProductGroupImageUploadStatusResult result =
                    ProductGroupImageApiFixtures.uploadStatusResult(PRODUCT_GROUP_ID);
            ProductGroupImageUploadStatusApiResponse response =
                    ProductGroupImageApiFixtures.uploadStatusApiResponse(PRODUCT_GROUP_ID);

            given(getUploadStatusUseCase.execute(PRODUCT_GROUP_ID)).willReturn(result);
            given(mapper.toResponse(any(ProductGroupImageUploadStatusResult.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL
                                            + ProductGroupImageAdminEndpoints.ID
                                            + ProductGroupImageAdminEndpoints.IMAGES
                                            + ProductGroupImageAdminEndpoints.UPLOAD_STATUS,
                                    PRODUCT_GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.productGroupId").value(PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.completedCount").value(1))
                    .andExpect(jsonPath("$.data.images").isArray())
                    .andDo(
                            document(
                                    "product-group-image/get-upload-status",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID")),
                                    responseFields(
                                            fieldWithPath("data.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 그룹 ID"),
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 이미지 수"),
                                            fieldWithPath("data.completedCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("업로드 완료 이미지 수"),
                                            fieldWithPath("data.pendingCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("업로드 대기 이미지 수"),
                                            fieldWithPath("data.processingCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("업로드 처리 중 이미지 수"),
                                            fieldWithPath("data.failedCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("업로드 실패 이미지 수"),
                                            fieldWithPath("data.images[]")
                                                    .description("이미지 업로드 상세 목록"),
                                            fieldWithPath("data.images[].imageId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("이미지 ID"),
                                            fieldWithPath("data.images[].imageType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 유형 (MAIN, DETAIL 등)"),
                                            fieldWithPath("data.images[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 이미지 URL"),
                                            fieldWithPath("data.images[].uploadedUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("업로드된 이미지 URL")
                                                    .optional(),
                                            fieldWithPath("data.images[].outboxStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "업로드 상태 (PENDING, PROCESSING,"
                                                                    + " COMPLETED, FAILED)"),
                                            fieldWithPath("data.images[].retryCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재시도 횟수"),
                                            fieldWithPath("data.images[].errorMessage")
                                                    .type(JsonFieldType.STRING)
                                                    .description("오류 메시지")
                                                    .optional(),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
