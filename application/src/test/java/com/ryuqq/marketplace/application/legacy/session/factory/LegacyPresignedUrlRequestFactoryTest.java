package com.ryuqq.marketplace.application.legacy.session.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyPresignedUrlRequestFactory 단위 테스트")
class LegacyPresignedUrlRequestFactoryTest {

    private LegacyPresignedUrlRequestFactory factory;

    @BeforeEach
    void setUp() {
        factory = new LegacyPresignedUrlRequestFactory();
    }

    @Nested
    @DisplayName("create - 커맨드를 PresignedUploadUrlRequest로 변환")
    class CreateTest {

        @Test
        @DisplayName("PRODUCT imagePath를 PRODUCT_IMAGES 디렉토리로 매핑한다")
        void create_ProductImagePath_MapsToProductImages() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("image.jpg", "PRODUCT", 1_048_576L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.directory()).isEqualTo(UploadDirectory.PRODUCT_IMAGES);
            assertThat(request.filename()).isEqualTo("image.jpg");
            assertThat(request.contentType()).isEqualTo("image/jpeg");
            assertThat(request.contentLength()).isEqualTo(1_048_576L);
        }

        @Test
        @DisplayName("DESCRIPTION imagePath를 DESCRIPTION 디렉토리로 매핑한다")
        void create_DescriptionImagePath_MapsToDescription() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("desc.png", "DESCRIPTION", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.directory()).isEqualTo(UploadDirectory.DESCRIPTION);
        }

        @Test
        @DisplayName("QNA imagePath를 QNAS 디렉토리로 매핑한다")
        void create_QnaImagePath_MapsToQnas() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("qna.jpg", "QNA", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.directory()).isEqualTo(UploadDirectory.QNAS);
        }

        @Test
        @DisplayName("CONTENT imagePath를 CONTENTS 디렉토리로 매핑한다")
        void create_ContentImagePath_MapsToContents() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("content.jpg", "CONTENT", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.directory()).isEqualTo(UploadDirectory.CONTENTS);
        }

        @Test
        @DisplayName("IMAGE_COMPONENT imagePath를 CONTENTS 디렉토리로 매핑한다")
        void create_ImageComponentPath_MapsToContents() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("component.jpg", "IMAGE_COMPONENT", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.directory()).isEqualTo(UploadDirectory.CONTENTS);
        }

        @Test
        @DisplayName("BANNER imagePath를 CONTENTS 디렉토리로 매핑한다")
        void create_BannerImagePath_MapsToContents() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("banner.jpg", "BANNER", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.directory()).isEqualTo(UploadDirectory.CONTENTS);
        }

        @Test
        @DisplayName("알 수 없는 imagePath는 PRODUCT_IMAGES 기본값으로 매핑한다")
        void create_UnknownImagePath_FallsBackToProductImages() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("file.jpg", "UNKNOWN", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.directory()).isEqualTo(UploadDirectory.PRODUCT_IMAGES);
        }

        @Test
        @DisplayName("null imagePath는 PRODUCT_IMAGES 기본값으로 매핑한다")
        void create_NullImagePath_FallsBackToProductImages() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("file.jpg", null, 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.directory()).isEqualTo(UploadDirectory.PRODUCT_IMAGES);
        }

        @Test
        @DisplayName("fileSize가 null이면 기본값 10MB를 사용한다")
        void create_NullFileSize_UsesDefault10MB() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("image.jpg", "PRODUCT", null);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.contentLength()).isEqualTo(10L * 1024 * 1024);
        }

        @Test
        @DisplayName("jpg 파일명에서 image/jpeg contentType을 추론한다")
        void create_JpgFile_InfersJpegContentType() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("photo.jpg", "PRODUCT", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.contentType()).isEqualTo("image/jpeg");
        }

        @Test
        @DisplayName("png 파일명에서 image/png contentType을 추론한다")
        void create_PngFile_InfersPngContentType() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("logo.png", "PRODUCT", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.contentType()).isEqualTo("image/png");
        }

        @Test
        @DisplayName("알 수 없는 확장자는 application/octet-stream을 사용한다")
        void create_UnknownExtension_UsesOctetStream() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand("data.xyz", "PRODUCT", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.contentType()).isEqualTo("application/octet-stream");
        }

        @Test
        @DisplayName("null 파일명은 application/octet-stream을 사용한다")
        void create_NullFileName_UsesOctetStream() {
            // given
            LegacyGetPresignedUrlCommand command =
                    new LegacyGetPresignedUrlCommand(null, "PRODUCT", 500_000L);

            // when
            PresignedUploadUrlRequest request = factory.create(command);

            // then
            assertThat(request.contentType()).isEqualTo("application/octet-stream");
        }
    }
}
