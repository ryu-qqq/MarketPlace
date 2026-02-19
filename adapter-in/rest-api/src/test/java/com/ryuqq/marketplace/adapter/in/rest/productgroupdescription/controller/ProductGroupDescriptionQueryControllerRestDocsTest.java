package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.ProductGroupDescriptionAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.ProductGroupDescriptionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.response.DescriptionPublishStatusApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.mapper.ProductGroupDescriptionQueryApiMapper;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.query.GetDescriptionPublishStatusUseCase;
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
@WebMvcTest(ProductGroupDescriptionQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ProductGroupDescriptionQueryController REST Docs 테스트")
class ProductGroupDescriptionQueryControllerRestDocsTest {

    private static final String BASE_URL = ProductGroupDescriptionAdminEndpoints.PRODUCT_GROUPS;
    private static final Long PRODUCT_GROUP_ID = 1L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetDescriptionPublishStatusUseCase getPublishStatusUseCase;
    @MockitoBean private ProductGroupDescriptionQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("상품 그룹 상세설명 발행 상태 조회 API")
    class GetDescriptionPublishStatusTest {

        @Test
        @DisplayName("유효한 ID로 조회하면 200과 발행 상태를 반환한다")
        void getPublishStatus_ValidId_Returns200() throws Exception {
            // given
            DescriptionPublishStatusResult result =
                    ProductGroupDescriptionApiFixtures.publishStatusResult(PRODUCT_GROUP_ID);
            DescriptionPublishStatusApiResponse response =
                    ProductGroupDescriptionApiFixtures.publishStatusApiResponse(PRODUCT_GROUP_ID);

            given(getPublishStatusUseCase.execute(PRODUCT_GROUP_ID)).willReturn(result);
            given(mapper.toResponse(any(DescriptionPublishStatusResult.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL
                                            + ProductGroupDescriptionAdminEndpoints.ID
                                            + ProductGroupDescriptionAdminEndpoints.DESCRIPTION
                                            + ProductGroupDescriptionAdminEndpoints.PUBLISH_STATUS,
                                    PRODUCT_GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.productGroupId").value(PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.data.publishStatus").value("DRAFT"))
                    .andExpect(jsonPath("$.data.images").isArray())
                    .andDo(
                            document(
                                    "product-group-description/get-publish-status",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID")),
                                    responseFields(
                                            fieldWithPath("data.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 그룹 ID"),
                                            fieldWithPath("data.descriptionId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상세설명 ID")
                                                    .optional(),
                                            fieldWithPath("data.publishStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "발행 상태 (DRAFT, PUBLISHED, PENDING)")
                                                    .optional(),
                                            fieldWithPath("data.cdnPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("CDN 경로")
                                                    .optional(),
                                            fieldWithPath("data.totalImageCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 이미지 수"),
                                            fieldWithPath("data.completedImageCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("업로드 완료 이미지 수"),
                                            fieldWithPath("data.pendingImageCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("업로드 대기 이미지 수"),
                                            fieldWithPath("data.failedImageCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("업로드 실패 이미지 수"),
                                            fieldWithPath("data.images[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 업로드 상세 목록"),
                                            fieldWithPath("data.images[].imageId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("이미지 ID"),
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
                                            fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                            fieldWithPath("requestId").type(JsonFieldType.STRING).description("요청 ID"))));
        }
    }
}
