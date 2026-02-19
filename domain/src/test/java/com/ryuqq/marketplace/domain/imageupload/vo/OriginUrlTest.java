package com.ryuqq.marketplace.domain.imageupload.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OriginUrl VO 테스트")
class OriginUrlTest {

    @Nested
    @DisplayName("생성 및 검증")
    class CreationTest {

        @Test
        @DisplayName("유효한 URL로 생성한다")
        void createWithValidUrl() {
            OriginUrl url = OriginUrl.of("https://example.com/image.jpg");
            assertThat(url.value()).isEqualTo("https://example.com/image.jpg");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> OriginUrl.of(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> OriginUrl.of("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("빈 값");
        }
    }

    @Nested
    @DisplayName("extension() - 확장자 추출")
    class ExtensionTest {

        @Test
        @DisplayName(".jpg 확장자를 추출한다")
        void extractJpgExtension() {
            OriginUrl url = OriginUrl.of("https://example.com/images/photo.jpg");
            assertThat(url.extension()).isEqualTo(".jpg");
        }

        @Test
        @DisplayName(".png 확장자를 추출한다")
        void extractPngExtension() {
            OriginUrl url = OriginUrl.of("https://example.com/images/photo.png");
            assertThat(url.extension()).isEqualTo(".png");
        }

        @Test
        @DisplayName("쿼리 파라미터가 있어도 확장자를 정확히 추출한다")
        void extractExtensionWithQueryParams() {
            OriginUrl url =
                    OriginUrl.of("https://example.com/images/photo.png?width=500&quality=80");
            assertThat(url.extension()).isEqualTo(".png");
        }

        @Test
        @DisplayName("확장자가 없으면 빈 문자열을 반환한다")
        void returnEmptyWhenNoExtension() {
            OriginUrl url = OriginUrl.of("https://example.com/images/photo");
            assertThat(url.extension()).isEmpty();
        }

        @Test
        @DisplayName("확장자가 5자 초과이면 빈 문자열을 반환한다")
        void returnEmptyWhenExtensionTooLong() {
            OriginUrl url = OriginUrl.of("https://example.com/file.toolongext");
            assertThat(url.extension()).isEmpty();
        }
    }
}
