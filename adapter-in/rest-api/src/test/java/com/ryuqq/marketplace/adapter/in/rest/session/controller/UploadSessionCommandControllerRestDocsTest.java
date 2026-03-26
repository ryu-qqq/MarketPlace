package com.ryuqq.marketplace.adapter.in.rest.session.controller;

import static org.mockito.ArgumentMatchers.any;
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
import com.ryuqq.marketplace.adapter.in.rest.session.UploadSessionAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.session.UploadSessionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.CompleteUploadSessionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.GenerateUploadUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.response.GenerateUploadUrlApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.session.mapper.UploadSessionCommandApiMapper;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.uploadsession.port.in.command.CompleteUploadSessionUseCase;
import com.ryuqq.marketplace.application.uploadsession.port.in.command.GenerateUploadUrlUseCase;
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
@WebMvcTest(UploadSessionCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("UploadSessionCommandController REST Docs 테스트")
class UploadSessionCommandControllerRestDocsTest {

    private static final String BASE_URL = UploadSessionAdminEndpoints.UPLOAD_SESSIONS;
    private static final String SESSION_ID = UploadSessionApiFixtures.DEFAULT_SESSION_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GenerateUploadUrlUseCase generateUploadUrlUseCase;
    @MockitoBean private CompleteUploadSessionUseCase completeUploadSessionUseCase;
    @MockitoBean private UploadSessionCommandApiMapper mapper;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.session.mapper.LegacyImagePresignedApiMapper
            legacyMapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("Presigned URL 발급 API")
    class GenerateUploadUrlTest {

        @Test
        @DisplayName("Presigned URL 발급 성공 - 201 반환")
        void generateUploadUrl_Success_Returns201() throws Exception {
            // given
            GenerateUploadUrlApiRequest request =
                    UploadSessionApiFixtures.generateUploadUrlRequest();
            PresignedUrlResponse useCaseResponse = UploadSessionApiFixtures.presignedUrlResponse();
            GenerateUploadUrlApiResponse apiResponse =
                    UploadSessionApiFixtures.generateUploadUrlApiResponse();

            given(mapper.toPresignedUploadUrlRequest(any(GenerateUploadUrlApiRequest.class)))
                    .willReturn(null);
            given(generateUploadUrlUseCase.execute(any())).willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(PresignedUrlResponse.class))).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.sessionId").value(SESSION_ID))
                    .andExpect(
                            jsonPath("$.data.presignedUrl")
                                    .value(UploadSessionApiFixtures.DEFAULT_PRESIGNED_URL))
                    .andExpect(
                            jsonPath("$.data.fileKey")
                                    .value(UploadSessionApiFixtures.DEFAULT_FILE_KEY))
                    .andExpect(
                            jsonPath("$.data.accessUrl")
                                    .value(UploadSessionApiFixtures.DEFAULT_ACCESS_URL))
                    .andDo(
                            document(
                                    "upload-session/generate-upload-url",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("directory")
                                                    .type(JsonFieldType.STRING)
                                                    .description("업로드 디렉토리 (예: product-images)"),
                                            fieldWithPath("filename")
                                                    .type(JsonFieldType.STRING)
                                                    .description("파일명 (예: image.jpg)"),
                                            fieldWithPath("contentType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("MIME 타입 (예: image/jpeg)"),
                                            fieldWithPath("contentLength")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("파일 크기 (바이트)")),
                                    responseFields(
                                            fieldWithPath("data.sessionId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("업로드 세션 ID"),
                                            fieldWithPath("data.presignedUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "클라이언트가 파일을 직접 업로드할 Presigned URL"),
                                            fieldWithPath("data.fileKey")
                                                    .type(JsonFieldType.STRING)
                                                    .description("S3 Object Key (파일 저장 경로)"),
                                            fieldWithPath("data.expiresAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("Presigned URL 만료 시각 (ISO 8601)"),
                                            fieldWithPath("data.accessUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("업로드 완료 후 CDN 접근 URL"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시각"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("directory가 공백이면 400 반환")
        void generateUploadUrl_BlankDirectory_Returns400() throws Exception {
            // given
            GenerateUploadUrlApiRequest request =
                    UploadSessionApiFixtures.generateUploadUrlRequest(
                            "", DEFAULT_FILENAME, DEFAULT_CONTENT_TYPE, DEFAULT_CONTENT_LENGTH);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("contentLength가 0 이하이면 400 반환")
        void generateUploadUrl_NonPositiveContentLength_Returns400() throws Exception {
            // given
            GenerateUploadUrlApiRequest request =
                    UploadSessionApiFixtures.generateUploadUrlRequest(
                            DEFAULT_DIRECTORY, DEFAULT_FILENAME, DEFAULT_CONTENT_TYPE, 0L);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("업로드 세션 완료 처리 API")
    class CompleteUploadSessionTest {

        @Test
        @DisplayName("업로드 세션 완료 처리 성공 - 200 반환")
        void completeUploadSession_Success_Returns200() throws Exception {
            // given
            CompleteUploadSessionApiRequest request =
                    UploadSessionApiFixtures.completeUploadSessionRequest();

            given(
                            mapper.toCompleteCommand(
                                    any(String.class), any(CompleteUploadSessionApiRequest.class)))
                    .willReturn(null);
            doNothing().when(completeUploadSessionUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + UploadSessionAdminEndpoints.COMPLETE,
                                            SESSION_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "upload-session/complete",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("sessionId")
                                                    .description("완료 처리할 업로드 세션 ID")),
                                    requestFields(
                                            fieldWithPath("fileSize")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("업로드된 파일 크기 (바이트)"),
                                            fieldWithPath("etag")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "S3 ETag 값 (nullable - CORS 제한으로 클라이언트가"
                                                                    + " 받지 못할 수 있음)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시각"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("etag 없이 완료 처리 시 200 반환")
        void completeUploadSession_WithoutEtag_Returns200() throws Exception {
            // given
            CompleteUploadSessionApiRequest request =
                    UploadSessionApiFixtures.completeUploadSessionRequestWithoutEtag();

            given(
                            mapper.toCompleteCommand(
                                    any(String.class), any(CompleteUploadSessionApiRequest.class)))
                    .willReturn(null);
            doNothing().when(completeUploadSessionUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + UploadSessionAdminEndpoints.COMPLETE,
                                            SESSION_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("fileSize가 0 이하이면 400 반환")
        void completeUploadSession_NonPositiveFileSize_Returns400() throws Exception {
            // given
            CompleteUploadSessionApiRequest request = new CompleteUploadSessionApiRequest(0L, null);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + UploadSessionAdminEndpoints.COMPLETE,
                                            SESSION_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ===== 내부 편의 상수 =====
    private static final String DEFAULT_DIRECTORY = UploadSessionApiFixtures.DEFAULT_DIRECTORY;
    private static final String DEFAULT_FILENAME = UploadSessionApiFixtures.DEFAULT_FILENAME;
    private static final String DEFAULT_CONTENT_TYPE =
            UploadSessionApiFixtures.DEFAULT_CONTENT_TYPE;
    private static final long DEFAULT_CONTENT_LENGTH =
            UploadSessionApiFixtures.DEFAULT_CONTENT_LENGTH;
}
