package com.ryuqq.marketplace.adapter.in.rest.session.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.session.UploadSessionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.LegacyImagePresignedApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.response.LegacyImagePresignedApiResponse;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyImagePresignedApiMapper 단위 테스트")
class LegacyImagePresignedApiMapperTest {

    private LegacyImagePresignedApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyImagePresignedApiMapper();
    }

    @Nested
    @DisplayName(
            "toCommand(LegacyImagePresignedApiRequest) - 레거시 요청을 PresignedUploadUrlRequest로 변환")
    class ToCommandTest {

        @Test
        @DisplayName("imagePath가 PRODUCT이면 UploadDirectory.PRODUCT_IMAGES로 변환된다")
        void toCommand_ProductImagePath_ReturnsProductImagesDirectory() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("test.jpg", "PRODUCT", 1_048_576L);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.directory()).isEqualTo(UploadDirectory.PRODUCT_IMAGES);
        }

        @Test
        @DisplayName("imagePath가 PRODUCT이면 fileName의 contentType이 자동 추론된다")
        void toCommand_JpgFileName_InfersImageJpegContentType() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("test.jpg", "PRODUCT", 1_048_576L);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.contentType()).isEqualTo("image/jpeg");
        }

        @Test
        @DisplayName("fileSize가 null이면 기본값 10MB로 설정된다")
        void toCommand_NullFileSize_UsesDefaultTenMegabytes() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("test.jpg", "PRODUCT", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            long expectedDefaultSize = 10L * 1024 * 1024;
            assertThat(result.contentLength()).isEqualTo(expectedDefaultSize);
        }

        @Test
        @DisplayName("fileSize가 명시되면 해당 값이 사용된다")
        void toCommand_ExplicitFileSize_UsesGivenValue() {
            // given
            long expectedSize = 512_000L;
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("banner.png", "BANNER", expectedSize);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.contentLength()).isEqualTo(expectedSize);
        }

        @Test
        @DisplayName("imagePath가 DESCRIPTION이면 UploadDirectory.DESCRIPTION으로 변환된다")
        void toCommand_DescriptionImagePath_ReturnsDescriptionDirectory() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("desc.png", "DESCRIPTION", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.directory()).isEqualTo(UploadDirectory.DESCRIPTION);
        }

        @Test
        @DisplayName("imagePath가 QNA이면 UploadDirectory.QNAS로 변환된다")
        void toCommand_QnaImagePath_ReturnsQnasDirectory() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("qna.png", "QNA", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.directory()).isEqualTo(UploadDirectory.QNAS);
        }

        @Test
        @DisplayName("imagePath가 CONTENT이면 UploadDirectory.CONTENTS로 변환된다")
        void toCommand_ContentImagePath_ReturnsContentsDirectory() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("content.png", "CONTENT", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.directory()).isEqualTo(UploadDirectory.CONTENTS);
        }

        @Test
        @DisplayName("imagePath가 IMAGE_COMPONENT이면 UploadDirectory.CONTENTS로 변환된다")
        void toCommand_ImageComponentImagePath_ReturnsContentsDirectory() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("component.png", "IMAGE_COMPONENT", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.directory()).isEqualTo(UploadDirectory.CONTENTS);
        }

        @Test
        @DisplayName("imagePath가 BANNER이면 UploadDirectory.CONTENTS로 변환된다")
        void toCommand_BannerImagePath_ReturnsContentsDirectory() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("banner.png", "BANNER", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.directory()).isEqualTo(UploadDirectory.CONTENTS);
        }

        @Test
        @DisplayName("알 수 없는 imagePath이면 UploadDirectory.PRODUCT_IMAGES 기본값으로 변환된다")
        void toCommand_UnknownImagePath_FallsBackToProductImages() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("unknown.jpg", "UNKNOWN_TYPE", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.directory()).isEqualTo(UploadDirectory.PRODUCT_IMAGES);
        }

        @Test
        @DisplayName("fileName이 png이면 contentType이 image/png로 추론된다")
        void toCommand_PngFileName_InfersImagePngContentType() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("image.png", "PRODUCT", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.contentType()).isEqualTo("image/png");
        }

        @Test
        @DisplayName("확장자를 알 수 없는 fileName이면 application/octet-stream으로 기본 추론된다")
        void toCommand_UnknownExtension_UsesDefaultContentType() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("file.xyz", "PRODUCT", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.contentType()).isEqualTo("application/octet-stream");
        }

        @Test
        @DisplayName("fileName이 올바르게 매핑된다")
        void toCommand_FileName_IsMappedCorrectly() {
            // given
            LegacyImagePresignedApiRequest request =
                    new LegacyImagePresignedApiRequest("product-image.jpg", "PRODUCT", null);

            // when
            PresignedUploadUrlRequest result = mapper.toCommand(request);

            // then
            assertThat(result.filename()).isEqualTo("product-image.jpg");
        }
    }

    @Nested
    @DisplayName(
            "toResponse(PresignedUrlResponse) - PresignedUrlResponse를"
                    + " LegacyImagePresignedApiResponse로 변환")
    class ToResponseTest {

        @Test
        @DisplayName("sessionId, presignedUrl, fileKey가 올바르게 매핑된다")
        void toResponse_AllFields_AreMappedCorrectly() {
            // given
            PresignedUrlResponse useCaseResponse = UploadSessionApiFixtures.presignedUrlResponse();

            // when
            LegacyImagePresignedApiResponse result = mapper.toResponse(useCaseResponse);

            // then
            assertThat(result.sessionId()).isEqualTo(UploadSessionApiFixtures.DEFAULT_SESSION_ID);
            assertThat(result.preSignedUrl())
                    .isEqualTo(UploadSessionApiFixtures.DEFAULT_PRESIGNED_URL);
            assertThat(result.objectKey()).isEqualTo(UploadSessionApiFixtures.DEFAULT_FILE_KEY);
        }

        @Test
        @DisplayName("sessionId가 올바르게 매핑된다")
        void toResponse_SessionId_IsMappedCorrectly() {
            // given
            PresignedUrlResponse useCaseResponse = UploadSessionApiFixtures.presignedUrlResponse();

            // when
            LegacyImagePresignedApiResponse result = mapper.toResponse(useCaseResponse);

            // then
            assertThat(result.sessionId()).isEqualTo(UploadSessionApiFixtures.DEFAULT_SESSION_ID);
        }

        @Test
        @DisplayName("presignedUrl이 올바르게 매핑된다")
        void toResponse_PresignedUrl_IsMappedCorrectly() {
            // given
            PresignedUrlResponse useCaseResponse = UploadSessionApiFixtures.presignedUrlResponse();

            // when
            LegacyImagePresignedApiResponse result = mapper.toResponse(useCaseResponse);

            // then
            assertThat(result.preSignedUrl())
                    .isEqualTo(UploadSessionApiFixtures.DEFAULT_PRESIGNED_URL);
        }

        @Test
        @DisplayName("fileKey가 objectKey로 올바르게 매핑된다")
        void toResponse_FileKey_IsMappedToObjectKey() {
            // given
            PresignedUrlResponse useCaseResponse = UploadSessionApiFixtures.presignedUrlResponse();

            // when
            LegacyImagePresignedApiResponse result = mapper.toResponse(useCaseResponse);

            // then
            assertThat(result.objectKey()).isEqualTo(UploadSessionApiFixtures.DEFAULT_FILE_KEY);
        }
    }
}
