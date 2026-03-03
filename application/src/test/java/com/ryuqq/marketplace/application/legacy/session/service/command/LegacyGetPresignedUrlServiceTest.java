package com.ryuqq.marketplace.application.legacy.session.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.legacy.session.dto.response.LegacyPresignedUrlResult;
import com.ryuqq.marketplace.application.legacy.session.factory.LegacyPresignedUrlRequestFactory;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyGetPresignedUrlService 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyGetPresignedUrlService 단위 테스트")
class LegacyGetPresignedUrlServiceTest {

    @InjectMocks private LegacyGetPresignedUrlService sut;

    @Mock private LegacyPresignedUrlRequestFactory requestFactory;
    @Mock private FileStorageManager fileStorageManager;

    @Nested
    @DisplayName("execute() - Presigned URL 발급")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드를 받아 Presigned URL을 발급하고 결과를 반환한다")
        void execute_WithValidCommand_ReturnsPresignedUrlResult() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("image.jpg", "PRODUCT", 1_048_576L);

            PresignedUploadUrlRequest uploadRequest =
                    PresignedUploadUrlRequest.of(
                            UploadDirectory.PRODUCT_IMAGES, "image.jpg", "image/jpeg", 1_048_576L);

            PresignedUrlResponse response =
                    new PresignedUrlResponse(
                            "sess-123",
                            "https://s3.example.com/presigned",
                            "product-images/image.jpg",
                            Instant.parse("2025-02-20T10:15:00Z"),
                            "https://cdn.example.com/product-images/image.jpg");

            given(requestFactory.create(command)).willReturn(uploadRequest);
            given(fileStorageManager.generateUploadUrl(uploadRequest)).willReturn(response);

            // when
            LegacyPresignedUrlResult result = sut.execute(command);

            // then
            assertThat(result.sessionId()).isEqualTo("sess-123");
            assertThat(result.preSignedUrl()).isEqualTo("https://s3.example.com/presigned");
            assertThat(result.objectKey()).isEqualTo("product-images/image.jpg");
        }

        @Test
        @DisplayName("Factory와 FileStorageManager에 순서대로 위임한다")
        void execute_DelegatesToFactoryAndManager_InOrder() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("banner.png", "BANNER", 2_000_000L);

            PresignedUploadUrlRequest uploadRequest =
                    PresignedUploadUrlRequest.of(
                            UploadDirectory.CONTENTS, "banner.png", "image/png", 2_000_000L);

            PresignedUrlResponse response =
                    new PresignedUrlResponse(
                            "sess-456",
                            "https://s3.example.com/presigned2",
                            "contents/banner.png",
                            Instant.parse("2025-02-20T11:00:00Z"),
                            "https://cdn.example.com/contents/banner.png");

            given(requestFactory.create(command)).willReturn(uploadRequest);
            given(fileStorageManager.generateUploadUrl(uploadRequest)).willReturn(response);

            // when
            sut.execute(command);

            // then
            then(requestFactory).should().create(command);
            then(fileStorageManager).should().generateUploadUrl(uploadRequest);
        }
    }
}
