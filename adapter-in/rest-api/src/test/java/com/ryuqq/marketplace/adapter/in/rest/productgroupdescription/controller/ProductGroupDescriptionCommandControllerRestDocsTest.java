package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.ProductGroupDescriptionAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.ProductGroupDescriptionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.command.UpdateProductGroupDescriptionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.mapper.ProductGroupDescriptionCommandApiMapper;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.command.UpdateProductGroupDescriptionUseCase;
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
@WebMvcTest(ProductGroupDescriptionCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ProductGroupDescriptionCommandController REST Docs 테스트")
class ProductGroupDescriptionCommandControllerRestDocsTest {

    private static final String BASE_URL = ProductGroupDescriptionAdminEndpoints.PRODUCT_GROUPS;
    private static final Long PRODUCT_GROUP_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UpdateProductGroupDescriptionUseCase updateDescriptionUseCase;
    @MockitoBean private ProductGroupDescriptionCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("상품 그룹 상세설명 수정 API")
    class UpdateDescriptionTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void updateDescription_ValidRequest_Returns204() throws Exception {
            // given
            UpdateProductGroupDescriptionApiRequest request =
                    ProductGroupDescriptionApiFixtures.updateRequest();

            given(
                            mapper.toCommand(
                                    any(Long.class),
                                    any(UpdateProductGroupDescriptionApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateDescriptionUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL
                                                    + ProductGroupDescriptionAdminEndpoints.ID
                                                    + ProductGroupDescriptionAdminEndpoints
                                                            .DESCRIPTION,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product-group-description/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID")),
                                    requestFields(
                                            fieldWithPath("content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세 설명 HTML 내용"))));
        }
    }
}
