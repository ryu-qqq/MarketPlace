package com.ryuqq.marketplace.adapter.in.rest.session.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.in.rest.session.UploadSessionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.CompleteUploadSessionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.GenerateUploadUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.response.GenerateUploadUrlApiResponse;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.uploadsession.dto.command.CompleteUploadSessionCommand;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("UploadSessionCommandApiMapper 단위 테스트")
class UploadSessionCommandApiMapperTest {

    private UploadSessionCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UploadSessionCommandApiMapper();
    }

    @Nested
    @DisplayName("toPresignedUploadUrlRequest(GenerateUploadUrlApiRequest) - 업로드 URL 발급 요청 변환")
    class ToPresignedUploadUrlRequestTest {

        @Test
        @DisplayName("GenerateUploadUrlApiRequest를 PresignedUploadUrlRequest로 변환한다")
        void toPresignedUploadUrlRequest_ConvertsRequest_ReturnsPresignedUploadUrlRequest() {
            // given
            GenerateUploadUrlApiRequest request =
                    UploadSessionApiFixtures.generateUploadUrlRequest();

            // when
            PresignedUploadUrlRequest result = mapper.toPresignedUploadUrlRequest(request);

            // then
            assertThat(result.directory()).isEqualTo(UploadDirectory.PRODUCT_IMAGES);
            assertThat(result.filename()).isEqualTo(UploadSessionApiFixtures.DEFAULT_FILENAME);
            assertThat(result.contentType())
                    .isEqualTo(UploadSessionApiFixtures.DEFAULT_CONTENT_TYPE);
            assertThat(result.contentLength())
                    .isEqualTo(UploadSessionApiFixtures.DEFAULT_CONTENT_LENGTH);
        }

        @Test
        @DisplayName("directory 값이 UploadDirectory enum으로 올바르게 변환된다")
        void toPresignedUploadUrlRequest_Directory_IsConvertedToEnum() {
            // given
            GenerateUploadUrlApiRequest request =
                    UploadSessionApiFixtures.generateUploadUrlRequest(
                            "description", "file.png", "image/png", 512_000L);

            // when
            PresignedUploadUrlRequest result = mapper.toPresignedUploadUrlRequest(request);

            // then
            assertThat(result.directory()).isEqualTo(UploadDirectory.DESCRIPTION);
        }

        @Test
        @DisplayName("허용되지 않은 directory 값은 IllegalArgumentException을 발생시킨다")
        void toPresignedUploadUrlRequest_InvalidDirectory_ThrowsException() {
            // given
            GenerateUploadUrlApiRequest request =
                    UploadSessionApiFixtures.generateUploadUrlRequest(
                            "invalid-directory", "file.png", "image/png", 512_000L);

            // when & then
            assertThatThrownBy(() -> mapper.toPresignedUploadUrlRequest(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("contentLength 값이 올바르게 매핑된다")
        void toPresignedUploadUrlRequest_ContentLength_IsMappedCorrectly() {
            // given
            long expectedContentLength = 2_097_152L;
            GenerateUploadUrlApiRequest request =
                    UploadSessionApiFixtures.generateUploadUrlRequest(
                            "seller-documents",
                            "report.pdf",
                            "application/pdf",
                            expectedContentLength);

            // when
            PresignedUploadUrlRequest result = mapper.toPresignedUploadUrlRequest(request);

            // then
            assertThat(result.contentLength()).isEqualTo(expectedContentLength);
            assertThat(result.directory()).isEqualTo(UploadDirectory.SELLER_DOCUMENTS);
        }
    }

    @Nested
    @DisplayName("toCompleteCommand(String, CompleteUploadSessionApiRequest) - 업로드 완료 커맨드 변환")
    class ToCompleteCommandTest {

        @Test
        @DisplayName(
                "sessionId와 CompleteUploadSessionApiRequest를 CompleteUploadSessionCommand로 변환한다")
        void toCompleteCommand_ConvertsRequest_ReturnsCompleteUploadSessionCommand() {
            // given
            String sessionId = UploadSessionApiFixtures.DEFAULT_SESSION_ID;
            CompleteUploadSessionApiRequest request =
                    UploadSessionApiFixtures.completeUploadSessionRequest();

            // when
            CompleteUploadSessionCommand command = mapper.toCompleteCommand(sessionId, request);

            // then
            assertThat(command.sessionId()).isEqualTo(sessionId);
            assertThat(command.fileSize()).isEqualTo(UploadSessionApiFixtures.DEFAULT_FILE_SIZE);
            assertThat(command.etag()).isEqualTo(UploadSessionApiFixtures.DEFAULT_ETAG);
        }

        @Test
        @DisplayName("etag가 null인 경우에도 올바르게 변환한다")
        void toCompleteCommand_WithNullEtag_ReturnsCommandWithNullEtag() {
            // given
            String sessionId = UploadSessionApiFixtures.DEFAULT_SESSION_ID;
            CompleteUploadSessionApiRequest request =
                    UploadSessionApiFixtures.completeUploadSessionRequestWithoutEtag();

            // when
            CompleteUploadSessionCommand command = mapper.toCompleteCommand(sessionId, request);

            // then
            assertThat(command.sessionId()).isEqualTo(sessionId);
            assertThat(command.fileSize()).isEqualTo(UploadSessionApiFixtures.DEFAULT_FILE_SIZE);
            assertThat(command.etag()).isNull();
        }

        @Test
        @DisplayName("PathVariable로 전달된 sessionId가 Command에 올바르게 매핑된다")
        void toCompleteCommand_SessionId_IsMappedFromPathVariable() {
            // given
            String customSessionId = "sess-custom-999";
            CompleteUploadSessionApiRequest request =
                    UploadSessionApiFixtures.completeUploadSessionRequest();

            // when
            CompleteUploadSessionCommand command =
                    mapper.toCompleteCommand(customSessionId, request);

            // then
            assertThat(command.sessionId()).isEqualTo("sess-custom-999");
        }
    }

    @Nested
    @DisplayName("toApiResponse(PresignedUrlResponse) - API 응답 변환")
    class ToApiResponseTest {

        @Test
        @DisplayName("PresignedUrlResponse를 GenerateUploadUrlApiResponse로 변환한다")
        void toApiResponse_ConvertsResponse_ReturnsGenerateUploadUrlApiResponse() {
            // given
            PresignedUrlResponse response = UploadSessionApiFixtures.presignedUrlResponse();

            // when
            GenerateUploadUrlApiResponse result = mapper.toApiResponse(response);

            // then
            assertThat(result.sessionId()).isEqualTo(UploadSessionApiFixtures.DEFAULT_SESSION_ID);
            assertThat(result.presignedUrl())
                    .isEqualTo(UploadSessionApiFixtures.DEFAULT_PRESIGNED_URL);
            assertThat(result.fileKey()).isEqualTo(UploadSessionApiFixtures.DEFAULT_FILE_KEY);
            assertThat(result.expiresAt()).isEqualTo(UploadSessionApiFixtures.DEFAULT_EXPIRES_AT);
            assertThat(result.accessUrl()).isEqualTo(UploadSessionApiFixtures.DEFAULT_ACCESS_URL);
        }

        @Test
        @DisplayName("sessionId가 올바르게 매핑된다")
        void toApiResponse_SessionId_IsMappedCorrectly() {
            // given
            PresignedUrlResponse response = UploadSessionApiFixtures.presignedUrlResponse();

            // when
            GenerateUploadUrlApiResponse result = mapper.toApiResponse(response);

            // then
            assertThat(result.sessionId()).isEqualTo(UploadSessionApiFixtures.DEFAULT_SESSION_ID);
        }

        @Test
        @DisplayName("expiresAt이 올바르게 매핑된다")
        void toApiResponse_ExpiresAt_IsMappedCorrectly() {
            // given
            PresignedUrlResponse response = UploadSessionApiFixtures.presignedUrlResponse();

            // when
            GenerateUploadUrlApiResponse result = mapper.toApiResponse(response);

            // then
            assertThat(result.expiresAt()).isEqualTo(UploadSessionApiFixtures.DEFAULT_EXPIRES_AT);
        }
    }
}
