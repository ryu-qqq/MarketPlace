package com.ryuqq.marketplace.domain.imagevariant.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageVariantType 테스트")
class ImageVariantTypeTest {

    @Nested
    @DisplayName("속성 값 검증")
    class PropertyTest {

        @Test
        @DisplayName("SMALL_WEBP는 300x300 RESIZE 타입이다")
        void smallWebp() {
            ImageVariantType type = ImageVariantType.SMALL_WEBP;
            assertThat(type.width()).isEqualTo(300);
            assertThat(type.height()).isEqualTo(300);
            assertThat(type.targetFormat()).isEqualTo("webp");
            assertThat(type.transformType()).isEqualTo("RESIZE");
            assertThat(type.quality()).isEqualTo(85);
        }

        @Test
        @DisplayName("MEDIUM_WEBP는 600x600 RESIZE 타입이다")
        void mediumWebp() {
            ImageVariantType type = ImageVariantType.MEDIUM_WEBP;
            assertThat(type.width()).isEqualTo(600);
            assertThat(type.height()).isEqualTo(600);
        }

        @Test
        @DisplayName("LARGE_WEBP는 1200x1200 RESIZE 타입이다")
        void largeWebp() {
            ImageVariantType type = ImageVariantType.LARGE_WEBP;
            assertThat(type.width()).isEqualTo(1200);
            assertThat(type.height()).isEqualTo(1200);
        }

        @Test
        @DisplayName("ORIGINAL_WEBP는 크기가 null인 CONVERT 타입이다")
        void originalWebp() {
            ImageVariantType type = ImageVariantType.ORIGINAL_WEBP;
            assertThat(type.width()).isNull();
            assertThat(type.height()).isNull();
            assertThat(type.transformType()).isEqualTo("CONVERT");
            assertThat(type.quality()).isEqualTo(90);
        }
    }

    @Nested
    @DisplayName("isResize() / isOriginalConversion()")
    class TypeCheckTest {

        @Test
        @DisplayName("SMALL_WEBP는 RESIZE 타입이다")
        void smallIsResize() {
            assertThat(ImageVariantType.SMALL_WEBP.isResize()).isTrue();
            assertThat(ImageVariantType.SMALL_WEBP.isOriginalConversion()).isFalse();
        }

        @Test
        @DisplayName("ORIGINAL_WEBP는 CONVERT 타입이다")
        void originalIsConvert() {
            assertThat(ImageVariantType.ORIGINAL_WEBP.isOriginalConversion()).isTrue();
            assertThat(ImageVariantType.ORIGINAL_WEBP.isResize()).isFalse();
        }
    }

    @Nested
    @DisplayName("requiresDimensions() - 크기 지정 필요 여부")
    class RequiresDimensionsTest {

        @Test
        @DisplayName("RESIZE 타입은 크기 지정이 필요하다")
        void resizeRequiresDimensions() {
            assertThat(ImageVariantType.SMALL_WEBP.requiresDimensions()).isTrue();
            assertThat(ImageVariantType.MEDIUM_WEBP.requiresDimensions()).isTrue();
            assertThat(ImageVariantType.LARGE_WEBP.requiresDimensions()).isTrue();
        }

        @Test
        @DisplayName("ORIGINAL_WEBP는 크기 지정이 필요하지 않다")
        void originalDoesNotRequireDimensions() {
            assertThat(ImageVariantType.ORIGINAL_WEBP.requiresDimensions()).isFalse();
        }
    }

    @Nested
    @DisplayName("toFileSuffix() - 파일 접미사 생성")
    class ToFileSuffixTest {

        @Test
        @DisplayName("SMALL_WEBP의 접미사는 300x300.webp이다")
        void smallWebpSuffix() {
            assertThat(ImageVariantType.SMALL_WEBP.toFileSuffix()).isEqualTo("300x300.webp");
        }

        @Test
        @DisplayName("MEDIUM_WEBP의 접미사는 600x600.webp이다")
        void mediumWebpSuffix() {
            assertThat(ImageVariantType.MEDIUM_WEBP.toFileSuffix()).isEqualTo("600x600.webp");
        }

        @Test
        @DisplayName("ORIGINAL_WEBP의 접미사는 original.webp이다")
        void originalWebpSuffix() {
            assertThat(ImageVariantType.ORIGINAL_WEBP.toFileSuffix()).isEqualTo("original.webp");
        }
    }
}
