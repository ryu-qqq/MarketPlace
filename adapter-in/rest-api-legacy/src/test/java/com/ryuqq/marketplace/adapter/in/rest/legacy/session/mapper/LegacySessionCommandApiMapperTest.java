package com.ryuqq.marketplace.adapter.in.rest.legacy.session.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.session.LegacySessionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.request.LegacyPresignedUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.response.LegacyPresignedUrlApiResponse;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacySessionCommandApiMapper 단위 테스트")
class LegacySessionCommandApiMapperTest {

    private LegacySessionCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacySessionCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand - 요청 → 표준 커맨드 변환")
    class ToCommandTest {

        @Test
        @DisplayName("PRODUCT imagePath가 PRODUCT_IMAGES 디렉토리로 매핑된다")
        void toCommand_ProductPath_MapsToProductImages() {
            // given
            LegacyPresignedUrlApiRequest request = LegacySessionApiFixtures.request();

            // when
            PresignedUploadUrlRequest command = mapper.toCommand(request);

            // then
            assertThat(command.directory()).isEqualTo(UploadDirectory.PRODUCT_IMAGES);
            assertThat(command.filename()).isEqualTo(LegacySessionApiFixtures.DEFAULT_FILE_NAME);
        }

        @Test
        @DisplayName("fileSize가 null이면 기본값 10MB가 적용된다")
        void toCommand_NullFileSize_DefaultsTo10MB() {
            // given
            LegacyPresignedUrlApiRequest request =
                    LegacySessionApiFixtures.requestWithoutFileSize();

            // when
            PresignedUploadUrlRequest command = mapper.toCommand(request);

            // then
            assertThat(command.contentLength()).isEqualTo(10L * 1024 * 1024);
        }
    }

    @Nested
    @DisplayName("toApiResponse - 표준 결과 → 레거시 응답 변환")
    class ToApiResponseTest {

        @Test
        @DisplayName("PresignedUrlResponse를 LegacyPresignedUrlApiResponse로 변환한다")
        void toApiResponse_ConvertsCorrectly() {
            // given
            PresignedUrlResponse result = LegacySessionApiFixtures.presignedUrlResponse();

            // when
            LegacyPresignedUrlApiResponse response = mapper.toApiResponse(result);

            // then
            assertThat(response.sessionId()).isEqualTo(LegacySessionApiFixtures.DEFAULT_SESSION_ID);
            assertThat(response.preSignedUrl())
                    .isEqualTo(LegacySessionApiFixtures.DEFAULT_PRESIGNED_URL);
            assertThat(response.objectKey()).isEqualTo(LegacySessionApiFixtures.DEFAULT_OBJECT_KEY);
        }
    }
}
