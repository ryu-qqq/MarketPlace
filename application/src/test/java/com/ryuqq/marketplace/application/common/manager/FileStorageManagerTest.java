package com.ryuqq.marketplace.application.common.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.port.out.client.FileStorageClient;
import com.ryuqq.marketplace.application.uploadsession.UploadSessionCommandFixtures;
import com.ryuqq.marketplace.application.uploadsession.UploadSessionResponseFixtures;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FileStorageManager 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("FileStorageManager 단위 테스트")
class FileStorageManagerTest {

    @InjectMocks private FileStorageManager sut;

    @Mock private FileStorageClient fileStorageClient;

    @Nested
    @DisplayName("generateUploadUrl() - Presigned URL 발급")
    class GenerateUploadUrlTest {

        @Test
        @DisplayName("요청을 FileStorageClient에 위임하고 응답을 반환한다")
        void generateUploadUrl_ValidRequest_DelegatesAndReturnsResponse() {
            // given
            PresignedUploadUrlRequest request =
                    UploadSessionCommandFixtures.presignedUploadUrlRequest();
            PresignedUrlResponse expectedResponse =
                    UploadSessionResponseFixtures.presignedUrlResponse();

            given(fileStorageClient.generateUploadUrl(request)).willReturn(expectedResponse);

            // when
            PresignedUrlResponse result = sut.generateUploadUrl(request);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            assertThat(result.sessionId())
                    .isEqualTo(UploadSessionResponseFixtures.DEFAULT_SESSION_ID);
            assertThat(result.fileKey()).isEqualTo(UploadSessionResponseFixtures.DEFAULT_FILE_KEY);
            then(fileStorageClient).should().generateUploadUrl(request);
        }

        @Test
        @DisplayName("다양한 디렉토리와 파일명으로 Presigned URL을 발급할 수 있다")
        void generateUploadUrl_CustomDirectoryAndFilename_ReturnsResponse() {
            // given
            PresignedUploadUrlRequest request =
                    UploadSessionCommandFixtures.presignedUploadUrlRequest(
                            UploadDirectory.SELLER_LOGOS, "thumbnail.png", "image/png", 512000L);
            PresignedUrlResponse expectedResponse =
                    UploadSessionResponseFixtures.presignedUrlResponse("session-video-001");

            given(fileStorageClient.generateUploadUrl(request)).willReturn(expectedResponse);

            // when
            PresignedUrlResponse result = sut.generateUploadUrl(request);

            // then
            assertThat(result.sessionId()).isEqualTo("session-video-001");
            then(fileStorageClient).should().generateUploadUrl(request);
        }
    }

    @Nested
    @DisplayName("completeUploadSession() - 업로드 세션 완료 처리")
    class CompleteUploadSessionTest {

        @Test
        @DisplayName("sessionId, fileSize, etag를 FileStorageClient에 위임한다")
        void completeUploadSession_ValidParams_DelegatesToClient() {
            // given
            String sessionId = UploadSessionCommandFixtures.DEFAULT_SESSION_ID;
            long fileSize = UploadSessionCommandFixtures.DEFAULT_FILE_SIZE;
            String etag = UploadSessionCommandFixtures.DEFAULT_ETAG;
            willDoNothing()
                    .given(fileStorageClient)
                    .completeUploadSession(sessionId, fileSize, etag);

            // when
            sut.completeUploadSession(sessionId, fileSize, etag);

            // then
            then(fileStorageClient).should().completeUploadSession(sessionId, fileSize, etag);
        }

        @Test
        @DisplayName("etag가 null인 경우에도 FileStorageClient에 위임한다")
        void completeUploadSession_NullEtag_DelegatesToClient() {
            // given
            String sessionId = UploadSessionCommandFixtures.DEFAULT_SESSION_ID;
            long fileSize = UploadSessionCommandFixtures.DEFAULT_FILE_SIZE;
            willDoNothing()
                    .given(fileStorageClient)
                    .completeUploadSession(sessionId, fileSize, null);

            // when
            sut.completeUploadSession(sessionId, fileSize, null);

            // then
            then(fileStorageClient).should().completeUploadSession(sessionId, fileSize, null);
        }
    }
}
