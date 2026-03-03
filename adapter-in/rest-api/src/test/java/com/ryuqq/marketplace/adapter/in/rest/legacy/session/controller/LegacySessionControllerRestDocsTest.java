package com.ryuqq.marketplace.adapter.in.rest.legacy.session.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.LegacySessionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.request.LegacyPresignedUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.response.LegacyPresignedUrlApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.mapper.LegacySessionCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.legacy.session.dto.response.LegacyPresignedUrlResult;
import com.ryuqq.marketplace.application.legacy.session.port.in.command.LegacyGetPresignedUrlUseCase;
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
@WebMvcTest(LegacySessionController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacySessionController REST Docs 테스트")
class LegacySessionControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/legacy/image/presigned";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LegacyGetPresignedUrlUseCase legacyGetPresignedUrlUseCase;
    @MockitoBean private LegacySessionCommandApiMapper mapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("레거시 Presigned URL 발급 API")
    class GetContentTest {

        @Test
        @DisplayName("Presigned URL 발급 성공")
        void getContent_Success() throws Exception {
            // given
            LegacyPresignedUrlApiRequest request = LegacySessionApiFixtures.request();
            LegacyGetPresignedUrlCommand command = LegacySessionApiFixtures.command();
            LegacyPresignedUrlResult result = LegacySessionApiFixtures.result();
            LegacyPresignedUrlApiResponse apiResponse = LegacySessionApiFixtures.apiResponse();

            given(mapper.toCommand(any())).willReturn(command);
            given(legacyGetPresignedUrlUseCase.execute(any())).willReturn(result);
            given(mapper.toApiResponse(any())).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.sessionId")
                                    .value(LegacySessionApiFixtures.DEFAULT_SESSION_ID))
                    .andExpect(
                            jsonPath("$.data.preSignedUrl")
                                    .value(LegacySessionApiFixtures.DEFAULT_PRESIGNED_URL))
                    .andExpect(
                            jsonPath("$.data.objectKey")
                                    .value(LegacySessionApiFixtures.DEFAULT_OBJECT_KEY))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andExpect(jsonPath("$.response.message").value("success"))
                    .andDo(
                            document(
                                    "legacy-session/image-presigned",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("fileName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("파일명 (필수)"),
                                            fieldWithPath("imagePath")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "이미지 경로 구분 (PRODUCT, DESCRIPTION,"
                                                                    + " QNA, CONTENT,"
                                                                    + " IMAGE_COMPONENT, BANNER)"),
                                            fieldWithPath("fileSize")
                                                    .type(JsonFieldType.NUMBER)
                                                    .optional()
                                                    .description(
                                                            "파일 크기 (bytes), 미입력 시 기본값" + " 10MB")),
                                    responseFields(
                                            fieldWithPath("data.sessionId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("업로드 세션 ID"),
                                            fieldWithPath("data.preSignedUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("S3 Presigned URL"),
                                            fieldWithPath("data.objectKey")
                                                    .type(JsonFieldType.STRING)
                                                    .description("S3 객체 키"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }

        @Test
        @DisplayName("fileSize 없이 요청해도 성공한다")
        void getContent_WithoutFileSize_Success() throws Exception {
            // given
            LegacyPresignedUrlApiRequest request =
                    LegacySessionApiFixtures.requestWithoutFileSize();
            LegacyGetPresignedUrlCommand command =
                    LegacySessionApiFixtures.commandWithoutFileSize();
            LegacyPresignedUrlResult result = LegacySessionApiFixtures.result();
            LegacyPresignedUrlApiResponse apiResponse = LegacySessionApiFixtures.apiResponse();

            given(mapper.toCommand(any())).willReturn(command);
            given(legacyGetPresignedUrlUseCase.execute(any())).willReturn(result);
            given(mapper.toApiResponse(any())).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.sessionId")
                                    .value(LegacySessionApiFixtures.DEFAULT_SESSION_ID));
        }
    }
}
