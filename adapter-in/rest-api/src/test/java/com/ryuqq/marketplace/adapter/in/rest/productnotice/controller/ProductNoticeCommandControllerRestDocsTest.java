package com.ryuqq.marketplace.adapter.in.rest.productnotice.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.productnotice.ProductNoticeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productnotice.ProductNoticeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productnotice.dto.command.UpdateProductNoticeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productnotice.mapper.ProductNoticeCommandApiMapper;
import com.ryuqq.marketplace.application.productnotice.port.in.command.UpdateProductNoticeUseCase;
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
@WebMvcTest(ProductNoticeCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ProductNoticeCommandController REST Docs 테스트")
class ProductNoticeCommandControllerRestDocsTest {

    private static final String BASE_URL = ProductNoticeAdminEndpoints.PRODUCT_GROUPS;
    private static final Long PRODUCT_GROUP_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UpdateProductNoticeUseCase updateNoticeUseCase;
    @MockitoBean private ProductNoticeCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("상품 그룹 고시정보 수정 API")
    class UpdateNoticeTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void updateNotice_ValidRequest_Returns204() throws Exception {
            // given
            UpdateProductNoticeApiRequest request = ProductNoticeApiFixtures.updateRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateProductNoticeApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateNoticeUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL
                                                    + ProductNoticeAdminEndpoints.ID
                                                    + ProductNoticeAdminEndpoints.NOTICE,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product-notice/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID")),
                                    requestFields(
                                            fieldWithPath("noticeCategoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 카테고리 ID"),
                                            fieldWithPath("entries")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("고시 항목 목록"),
                                            fieldWithPath("entries[].noticeFieldId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 필드 ID"),
                                            fieldWithPath("entries[].fieldValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("고시 필드 값"))));
        }
    }
}
