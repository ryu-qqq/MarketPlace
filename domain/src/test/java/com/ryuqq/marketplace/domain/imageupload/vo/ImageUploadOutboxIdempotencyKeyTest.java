package com.ryuqq.marketplace.domain.imageupload.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageUploadOutboxIdempotencyKey VO 테스트")
class ImageUploadOutboxIdempotencyKeyTest {

    @Nested
    @DisplayName("generate() - 멱등키 생성")
    class GenerateTest {

        @Test
        @DisplayName("IUO:sourceType:sourceId:epochMilli 형식으로 생성한다")
        void generateKey() {
            // given
            Instant now = Instant.ofEpochMilli(1706612400000L);

            // when
            ImageUploadOutboxIdempotencyKey key =
                    ImageUploadOutboxIdempotencyKey.generate(
                            ImageSourceType.PRODUCT_GROUP_IMAGE, 123L, now);

            // then
            assertThat(key.value()).isEqualTo("IUO:PRODUCT_GROUP_IMAGE:123:1706612400000");
        }

        @Test
        @DisplayName("sourceType이 null이면 예외가 발생한다")
        void generateWithNullSourceType_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ImageUploadOutboxIdempotencyKey.generate(
                                            null, 123L, Instant.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("sourceId가 null이면 예외가 발생한다")
        void generateWithNullSourceId_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ImageUploadOutboxIdempotencyKey.generate(
                                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                                            null,
                                            Instant.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 예외가 발생한다")
        void generateWithNullCreatedAt_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ImageUploadOutboxIdempotencyKey.generate(
                                            ImageSourceType.PRODUCT_GROUP_IMAGE, 123L, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("of() - 기존 값으로 재구성")
    class OfTest {

        @Test
        @DisplayName("올바른 형식의 값으로 재구성한다")
        void reconstituteWithValidValue() {
            String value = "IUO:PRODUCT_GROUP_IMAGE:123:1706612400000";
            ImageUploadOutboxIdempotencyKey key = ImageUploadOutboxIdempotencyKey.of(value);
            assertThat(key.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void reconstituteWithNull_ThrowsException() {
            assertThatThrownBy(() -> ImageUploadOutboxIdempotencyKey.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("잘못된 형식이면 예외가 발생한다")
        void reconstituteWithInvalidFormat_ThrowsException() {
            assertThatThrownBy(() -> ImageUploadOutboxIdempotencyKey.of("INVALID:FORMAT"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 멱등키 형식");
        }
    }
}
