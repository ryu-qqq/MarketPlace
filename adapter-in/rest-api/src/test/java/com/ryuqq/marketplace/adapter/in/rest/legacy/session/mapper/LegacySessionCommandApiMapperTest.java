package com.ryuqq.marketplace.adapter.in.rest.legacy.session.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.session.LegacySessionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.request.LegacyPresignedUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.response.LegacyPresignedUrlApiResponse;
import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.legacy.session.dto.response.LegacyPresignedUrlResult;
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
    @DisplayName("toCommand - API 요청을 커맨드로 변환")
    class ToCommandTest {

        @Test
        @DisplayName("LegacyPresignedUrlApiRequest를 LegacyGetPresignedUrlCommand로 변환한다")
        void toCommand_ConvertsRequest_ReturnsCommand() {
            // given
            LegacyPresignedUrlApiRequest request = LegacySessionApiFixtures.request();

            // when
            LegacyGetPresignedUrlCommand command = mapper.toCommand(request);

            // then
            assertThat(command.fileName()).isEqualTo(LegacySessionApiFixtures.DEFAULT_FILE_NAME);
            assertThat(command.imagePath()).isEqualTo(LegacySessionApiFixtures.DEFAULT_IMAGE_PATH);
            assertThat(command.fileSize()).isEqualTo(LegacySessionApiFixtures.DEFAULT_FILE_SIZE);
        }

        @Test
        @DisplayName("fileSize가 null인 경우에도 정상 변환된다")
        void toCommand_WithNullFileSize_ReturnsCommandWithNullFileSize() {
            // given
            LegacyPresignedUrlApiRequest request =
                    LegacySessionApiFixtures.requestWithoutFileSize();

            // when
            LegacyGetPresignedUrlCommand command = mapper.toCommand(request);

            // then
            assertThat(command.fileName()).isEqualTo(LegacySessionApiFixtures.DEFAULT_FILE_NAME);
            assertThat(command.imagePath()).isEqualTo(LegacySessionApiFixtures.DEFAULT_IMAGE_PATH);
            assertThat(command.fileSize()).isNull();
        }

        @Test
        @DisplayName("커스텀 값으로 올바르게 변환된다")
        void toCommand_WithCustomValues_MapsCorrectly() {
            // given
            LegacyPresignedUrlApiRequest request =
                    LegacySessionApiFixtures.requestWith("banner.png", "BANNER", 2_097_152L);

            // when
            LegacyGetPresignedUrlCommand command = mapper.toCommand(request);

            // then
            assertThat(command.fileName()).isEqualTo("banner.png");
            assertThat(command.imagePath()).isEqualTo("BANNER");
            assertThat(command.fileSize()).isEqualTo(2_097_152L);
        }
    }

    @Nested
    @DisplayName("toApiResponse - 결과를 API 응답으로 변환")
    class ToApiResponseTest {

        @Test
        @DisplayName("LegacyPresignedUrlResult를 LegacyPresignedUrlApiResponse로 변환한다")
        void toApiResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            LegacyPresignedUrlResult result = LegacySessionApiFixtures.result();

            // when
            LegacyPresignedUrlApiResponse response = mapper.toApiResponse(result);

            // then
            assertThat(response.sessionId()).isEqualTo(LegacySessionApiFixtures.DEFAULT_SESSION_ID);
            assertThat(response.preSignedUrl())
                    .isEqualTo(LegacySessionApiFixtures.DEFAULT_PRESIGNED_URL);
            assertThat(response.objectKey()).isEqualTo(LegacySessionApiFixtures.DEFAULT_OBJECT_KEY);
        }

        @Test
        @DisplayName("다른 값으로도 올바르게 변환된다")
        void toApiResponse_WithDifferentValues_MapsCorrectly() {
            // given
            LegacyPresignedUrlResult result =
                    LegacySessionApiFixtures.resultWith(
                            "sess-xyz", "https://s3.example.com/presigned", "contents/banner.png");

            // when
            LegacyPresignedUrlApiResponse response = mapper.toApiResponse(result);

            // then
            assertThat(response.sessionId()).isEqualTo("sess-xyz");
            assertThat(response.preSignedUrl()).isEqualTo("https://s3.example.com/presigned");
            assertThat(response.objectKey()).isEqualTo("contents/banner.png");
        }
    }
}
