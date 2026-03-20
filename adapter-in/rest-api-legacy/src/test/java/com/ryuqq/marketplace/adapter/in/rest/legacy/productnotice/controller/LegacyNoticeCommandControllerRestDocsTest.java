package com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.LegacyNoticeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.mapper.LegacyNoticeCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.productnotice.dto.command.LegacyUpdateNoticeCommand;
import com.ryuqq.marketplace.application.legacy.productnotice.port.in.command.LegacyProductUpdateNoticeUseCase;
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
@WebMvcTest(LegacyNoticeCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacyNoticeCommandController REST Docs 테스트")
class LegacyNoticeCommandControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/legacy/product/group/{productGroupId}/notice";
    private static final long PRODUCT_GROUP_ID = LegacyNoticeApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LegacyProductUpdateNoticeUseCase legacyProductUpdateNoticeUseCase;
    @MockitoBean private LegacyNoticeCommandApiMapper legacyNoticeCommandApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("레거시 고시정보 수정 API")
    class UpdateProductNoticeTest {

        @Test
        @DisplayName("고시정보 수정 성공")
        void updateProductNotice_Success() throws Exception {
            // given
            LegacyCreateProductNoticeRequest request = LegacyNoticeApiFixtures.request();
            LegacyUpdateNoticeCommand command = LegacyNoticeApiFixtures.command(PRODUCT_GROUP_ID);

            given(legacyNoticeCommandApiMapper.toLegacyNoticeCommand(anyLong(), any()))
                    .willReturn(command);
            doNothing().when(legacyProductUpdateNoticeUseCase).execute(any());

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
                                    "legacy-notice/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("수정할 상품그룹 ID")),
                                    requestFields(
                                            fieldWithPath("material")
                                                    .type(JsonFieldType.STRING)
                                                    .description("소재 (최대 500자)")
                                                    .optional(),
                                            fieldWithPath("color")
                                                    .type(JsonFieldType.STRING)
                                                    .description("색상 (최대 500자)")
                                                    .optional(),
                                            fieldWithPath("size")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사이즈 (최대 500자)")
                                                    .optional(),
                                            fieldWithPath("maker")
                                                    .type(JsonFieldType.STRING)
                                                    .description("제조사 (최대 500자)")
                                                    .optional(),
                                            fieldWithPath("origin")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원산지 (최대 500자)")
                                                    .optional(),
                                            fieldWithPath("washingMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("세탁 방법 (최대 500자)")
                                                    .optional(),
                                            fieldWithPath("yearMonth")
                                                    .type(JsonFieldType.STRING)
                                                    .description("제조 연월 (최대 500자)")
                                                    .optional(),
                                            fieldWithPath("assuranceStandard")
                                                    .type(JsonFieldType.STRING)
                                                    .description("품질 보증 기준 (최대 500자)")
                                                    .optional(),
                                            fieldWithPath("asPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("AS 전화번호 (최대 500자)")
                                                    .optional()),
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
        @DisplayName("null 값을 포함한 고시정보도 수정 성공")
        void updateProductNotice_WithNullFields_Success() throws Exception {
            // given
            LegacyCreateProductNoticeRequest request = LegacyNoticeApiFixtures.requestWithNulls();
            LegacyUpdateNoticeCommand command =
                    LegacyNoticeApiFixtures.commandWithEmptyValues(PRODUCT_GROUP_ID);

            given(legacyNoticeCommandApiMapper.toLegacyNoticeCommand(anyLong(), any()))
                    .willReturn(command);
            doNothing().when(legacyProductUpdateNoticeUseCase).execute(any());

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
