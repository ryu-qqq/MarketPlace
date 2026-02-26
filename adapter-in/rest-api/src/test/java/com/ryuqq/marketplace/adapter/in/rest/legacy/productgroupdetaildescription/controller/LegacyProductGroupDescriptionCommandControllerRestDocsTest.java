package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.LegacyDescriptionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.mapper.LegacyDescriptionCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.description.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.legacy.description.port.in.command.LegacyProductUpdateDescriptionUseCase;
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
@WebMvcTest(LegacyProductGroupDescriptionCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacyProductGroupDescriptionCommandController REST Docs 테스트")
class LegacyProductGroupDescriptionCommandControllerRestDocsTest {

    private static final String BASE_URL =
            "/api/v1/legacy/product/group/{productGroupId}/detailDescription";
    private static final long PRODUCT_GROUP_ID =
            LegacyDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private LegacyProductUpdateDescriptionUseCase legacyProductUpdateDescriptionUseCase;

    @MockitoBean private LegacyDescriptionCommandApiMapper legacyDescriptionCommandApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("레거시 상품그룹 상세설명 수정 API")
    class UpdateDetailDescriptionTest {

        @Test
        @DisplayName("상세설명 수정 성공")
        void updateDetailDescription_Success() throws Exception {
            // given
            LegacyUpdateProductDescriptionRequest request = LegacyDescriptionApiFixtures.request();
            LegacyUpdateDescriptionCommand command =
                    LegacyDescriptionApiFixtures.legacyCommand(PRODUCT_GROUP_ID);

            given(
                            legacyDescriptionCommandApiMapper.toLegacyUpdateDescriptionCommand(
                                    anyLong(), any()))
                    .willReturn(command);
            doNothing().when(legacyProductUpdateDescriptionUseCase).execute(any());

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
                                    "legacy-description/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("수정할 상품그룹 ID")),
                                    requestFields(
                                            fieldWithPath("detailDescription")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세설명 HTML 내용 (필수)")),
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
        @DisplayName("HTML 태그가 포함된 상세설명도 수정 성공")
        void updateDetailDescription_WithHtmlContent_Success() throws Exception {
            // given
            LegacyUpdateProductDescriptionRequest request =
                    LegacyDescriptionApiFixtures.updatedRequest();
            LegacyUpdateDescriptionCommand command =
                    new LegacyUpdateDescriptionCommand(
                            PRODUCT_GROUP_ID,
                            LegacyDescriptionApiFixtures.UPDATED_DETAIL_DESCRIPTION);

            given(
                            legacyDescriptionCommandApiMapper.toLegacyUpdateDescriptionCommand(
                                    anyLong(), any()))
                    .willReturn(command);
            doNothing().when(legacyProductUpdateDescriptionUseCase).execute(any());

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
