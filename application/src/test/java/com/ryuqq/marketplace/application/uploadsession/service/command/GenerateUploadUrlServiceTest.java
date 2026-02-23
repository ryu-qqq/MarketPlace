package com.ryuqq.marketplace.application.uploadsession.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
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
 * GenerateUploadUrlService 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GenerateUploadUrlService 단위 테스트")
class GenerateUploadUrlServiceTest {

    @InjectMocks private GenerateUploadUrlService sut;

    @Mock private FileStorageManager fileStorageManager;

    @Nested
    @DisplayName("execute() - Presigned URL 발급")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 요청으로 Presigned URL을 발급하고 응답을 반환한다")
        void execute_ValidRequest_ReturnsPresignedUrlResponse() {
            // given
            PresignedUploadUrlRequest request =
                    UploadSessionCommandFixtures.presignedUploadUrlRequest();
            PresignedUrlResponse expectedResponse =
                    UploadSessionResponseFixtures.presignedUrlResponse();

            given(fileStorageManager.generateUploadUrl(request)).willReturn(expectedResponse);

            // when
            PresignedUrlResponse result = sut.execute(request);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            assertThat(result.sessionId())
                    .isEqualTo(UploadSessionResponseFixtures.DEFAULT_SESSION_ID);
            assertThat(result.presignedUrl())
                    .isEqualTo(UploadSessionResponseFixtures.DEFAULT_PRESIGNED_URL);
            assertThat(result.fileKey()).isEqualTo(UploadSessionResponseFixtures.DEFAULT_FILE_KEY);
            assertThat(result.accessUrl())
                    .isEqualTo(UploadSessionResponseFixtures.DEFAULT_ACCESS_URL);
            then(fileStorageManager).should().generateUploadUrl(request);
        }

        @Test
        @DisplayName("FileStorageManager에 요청 객체를 그대로 위임한다")
        void execute_DelegatesRequest_ToFileStorageManager() {
            // given
            PresignedUploadUrlRequest request =
                    UploadSessionCommandFixtures.presignedUploadUrlRequest(
                            UploadDirectory.DESCRIPTION, "report.pdf", "application/pdf", 1048576L);
            PresignedUrlResponse expectedResponse =
                    UploadSessionResponseFixtures.presignedUrlResponse("session-custom-001");

            given(fileStorageManager.generateUploadUrl(request)).willReturn(expectedResponse);

            // when
            PresignedUrlResponse result = sut.execute(request);

            // then
            assertThat(result.sessionId()).isEqualTo("session-custom-001");
            then(fileStorageManager).should().generateUploadUrl(request);
        }
    }
}
