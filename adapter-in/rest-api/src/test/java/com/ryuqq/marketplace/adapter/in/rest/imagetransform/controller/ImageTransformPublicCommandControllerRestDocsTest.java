package com.ryuqq.marketplace.adapter.in.rest.imagetransform.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.imagetransform.ImageTransformApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.imagetransform.ImageTransformPublicEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.imagetransform.dto.request.ImageTransformCallbackApiRequest;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.CompleteImageTransformCallbackUseCase;
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
@WebMvcTest(ImageTransformPublicCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ImageTransformPublicCommandController REST Docs 테스트")
class ImageTransformPublicCommandControllerRestDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CompleteImageTransformCallbackUseCase completeCallbackUseCase;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("이미지 변환 콜백 수신 API")
    class HandleImageTransformCallbackTest {

        @Test
        @DisplayName("COMPLETED 상태 콜백 수신 성공")
        void handleCallback_CompletedStatus_Returns200() throws Exception {
            // given
            ImageTransformCallbackApiRequest request =
                    ImageTransformApiFixtures.completedCallbackRequest();

            doNothing().when(completeCallbackUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            ImageTransformPublicEndpoints.CALLBACK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andDo(
                            document(
                                    "image-transform/callback-completed",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("transformRequestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변환 요청 ID"),
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변환 상태 (COMPLETED, FAILED)"),
                                            fieldWithPath("sourceAssetId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 에셋 ID")
                                                    .optional(),
                                            fieldWithPath("resultAssetId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결과 에셋 ID (COMPLETED 시)")
                                                    .optional(),
                                            fieldWithPath("transformType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "변환 타입 (COMPLETED 시, e.g."
                                                                    + " IMAGE_RESIZE)")
                                                    .optional(),
                                            fieldWithPath("width")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("변환 결과 너비 (COMPLETED 시)")
                                                    .optional(),
                                            fieldWithPath("height")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("변환 결과 높이 (COMPLETED 시)")
                                                    .optional(),
                                            fieldWithPath("quality")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("변환 품질 (COMPLETED 시)")
                                                    .optional(),
                                            fieldWithPath("targetFormat")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변환 포맷 (COMPLETED 시, e.g. webp)")
                                                    .optional(),
                                            fieldWithPath("errorMessage")
                                                    .type(JsonFieldType.STRING)
                                                    .description("에러 메시지 (FAILED 시)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("FAILED 상태 콜백 수신 성공")
        void handleCallback_FailedStatus_Returns200() throws Exception {
            // given
            ImageTransformCallbackApiRequest request =
                    ImageTransformApiFixtures.failedCallbackRequest();

            doNothing().when(completeCallbackUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            ImageTransformPublicEndpoints.CALLBACK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }
}
