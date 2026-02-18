package com.ryuqq.marketplace.application.imageupload.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.imageupload.internal.ImageUploadProcessBundle;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageUploadProcessBundleFactory 단위 테스트")
class ImageUploadProcessBundleFactoryTest {

    private ImageUploadProcessBundleFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ImageUploadProcessBundleFactory();
    }

    @Nested
    @DisplayName("create() - 업로드 처리 Bundle 생성")
    class CreateTest {

        @Test
        @DisplayName("Outbox와 처리 시각으로 ImageUploadProcessBundle을 생성한다")
        void create_ValidOutbox_ReturnsBundle() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = Instant.now();

            // when
            ImageUploadProcessBundle bundle = sut.create(outbox, now);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.outbox()).isEqualTo(outbox);
            assertThat(bundle.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Bundle에 포함된 DownloadRequest에 originUrl과 카테고리가 설정된다")
        void create_ValidOutbox_DownloadRequestHasCorrectFields() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = Instant.now();

            // when
            ImageUploadProcessBundle bundle = sut.create(outbox, now);

            // then
            assertThat(bundle.downloadRequest()).isNotNull();
            assertThat(bundle.downloadRequest().sourceUrl()).isEqualTo(outbox.originUrlValue());
            assertThat(bundle.downloadRequest().category()).isEqualTo("product-images");
            assertThat(bundle.downloadRequest().filename()).isNotBlank();
        }

        @Test
        @DisplayName("Bundle의 filename은 Outbox generateFilename 결과와 일치한다")
        void create_ValidOutbox_FilenameMatchesOutboxGeneratedFilename() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Instant now = Instant.now();
            String expectedFilename = outbox.generateFilename(now);

            // when
            ImageUploadProcessBundle bundle = sut.create(outbox, now);

            // then
            assertThat(bundle.downloadRequest().filename()).isEqualTo(expectedFilename);
        }
    }
}
