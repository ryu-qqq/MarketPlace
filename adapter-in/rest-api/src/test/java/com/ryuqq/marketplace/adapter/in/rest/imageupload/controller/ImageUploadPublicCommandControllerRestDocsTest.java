package com.ryuqq.marketplace.adapter.in.rest.imageupload.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.imageupload.ImageUploadApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.imageupload.ImageUploadPublicEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.imageupload.dto.request.ImageUploadCallbackApiRequest;
import com.ryuqq.marketplace.application.imageupload.port.in.command.CompleteImageUploadCallbackUseCase;
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
@WebMvcTest(ImageUploadPublicCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ImageUploadPublicCommandController REST Docs 테스트")
class ImageUploadPublicCommandControllerRestDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CompleteImageUploadCallbackUseCase completeCallbackUseCase;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("이미지 업로드 콜백 수신 API")
    class HandleImageUploadCallbackTest {

        @Test
        @DisplayName("COMPLETED 상태 콜백 수신 성공")
        void handleCallback_CompletedStatus_Returns200() throws Exception {
            // given
            ImageUploadCallbackApiRequest request =
                    ImageUploadApiFixtures.completedCallbackRequest();

            doNothing().when(completeCallbackUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            ImageUploadPublicEndpoints.CALLBACK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andDo(
                            document(
                                    "image-upload/callback-completed",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("downloadTaskId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("다운로드 태스크 ID"),
                                            fieldWithPath("assetId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("FileFlow 에셋 ID (COMPLETED 시)")
                                                    .optional(),
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("태스크 상태 (COMPLETED, FAILED)"),
                                            fieldWithPath("sourceUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 다운로드 URL")
                                                    .optional(),
                                            fieldWithPath("s3Key")
                                                    .type(JsonFieldType.STRING)
                                                    .description("S3 저장 경로 (COMPLETED 시)")
                                                    .optional(),
                                            fieldWithPath("bucket")
                                                    .type(JsonFieldType.STRING)
                                                    .description("S3 버킷 (COMPLETED 시)")
                                                    .optional(),
                                            fieldWithPath("fileName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("파일명 (COMPLETED 시)")
                                                    .optional(),
                                            fieldWithPath("contentType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("콘텐츠 타입")
                                                    .optional(),
                                            fieldWithPath("fileSize")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("파일 크기 (바이트)"),
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
            ImageUploadCallbackApiRequest request = ImageUploadApiFixtures.failedCallbackRequest();

            doNothing().when(completeCallbackUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            ImageUploadPublicEndpoints.CALLBACK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }
}
