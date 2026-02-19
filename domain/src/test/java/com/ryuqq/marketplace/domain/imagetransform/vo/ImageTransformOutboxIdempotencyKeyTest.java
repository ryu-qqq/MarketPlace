package com.ryuqq.marketplace.domain.imagetransform.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageTransformOutboxIdempotencyKey VO 테스트")
class ImageTransformOutboxIdempotencyKeyTest {

    @Nested
    @DisplayName("generate() - 멱등키 생성")
    class GenerateTest {

        @Test
        @DisplayName("ITO:sourceImageId:variantType:epochMilli 형식으로 생성한다")
        void generateKey() {
            // given
            Instant now = Instant.ofEpochMilli(1706612400000L);

            // when
            ImageTransformOutboxIdempotencyKey key =
                    ImageTransformOutboxIdempotencyKey.generate(
                            123L, ImageVariantType.SMALL_WEBP, now);

            // then
            assertThat(key.value()).isEqualTo("ITO:123:SMALL_WEBP:1706612400000");
        }

        @Test
        @DisplayName("ORIGINAL_WEBP variantType으로 생성한다")
        void generateKeyWithOriginalWebp() {
            // given
            Instant now = Instant.ofEpochMilli(1706612400000L);

            // when
            ImageTransformOutboxIdempotencyKey key =
                    ImageTransformOutboxIdempotencyKey.generate(
                            456L, ImageVariantType.ORIGINAL_WEBP, now);

            // then
            assertThat(key.value()).isEqualTo("ITO:456:ORIGINAL_WEBP:1706612400000");
        }

        @Test
        @DisplayName("sourceImageId가 null이면 예외가 발생한다")
        void generateWithNullSourceImageId_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ImageTransformOutboxIdempotencyKey.generate(
                                            null, ImageVariantType.SMALL_WEBP, Instant.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("variantType이 null이면 예외가 발생한다")
        void generateWithNullVariantType_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ImageTransformOutboxIdempotencyKey.generate(
                                            123L, null, Instant.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 예외가 발생한다")
        void generateWithNullCreatedAt_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ImageTransformOutboxIdempotencyKey.generate(
                                            123L, ImageVariantType.SMALL_WEBP, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("of() - 기존 값으로 재구성")
    class OfTest {

        @Test
        @DisplayName("올바른 형식의 값으로 재구성한다")
        void reconstituteWithValidValue() {
            String value = "ITO:123:SMALL_WEBP:1706612400000";
            ImageTransformOutboxIdempotencyKey key = ImageTransformOutboxIdempotencyKey.of(value);
            assertThat(key.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void reconstituteWithNull_ThrowsException() {
            assertThatThrownBy(() -> ImageTransformOutboxIdempotencyKey.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("잘못된 형식이면 예외가 발생한다")
        void reconstituteWithInvalidFormat_ThrowsException() {
            assertThatThrownBy(() -> ImageTransformOutboxIdempotencyKey.of("INVALID:FORMAT"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 멱등키 형식");
        }
    }
}
