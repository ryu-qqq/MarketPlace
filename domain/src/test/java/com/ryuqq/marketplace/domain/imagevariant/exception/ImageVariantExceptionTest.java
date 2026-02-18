package com.ryuqq.marketplace.domain.imagevariant.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageVariant 예외 테스트")
class ImageVariantExceptionTest {

    @Test
    @DisplayName("ImageVariantNotFoundException 생성 시 올바른 에러 코드와 메시지를 가진다")
    void imageVariantNotFoundExceptionTest() {
        // given
        Long imageVariantId = 999L;

        // when
        ImageVariantNotFoundException exception = new ImageVariantNotFoundException(imageVariantId);

        // then
        assertThat(exception.code()).isEqualTo("IMGVAR-001");
        assertThat(exception.httpStatus()).isEqualTo(404);
        assertThat(exception.getMessage()).contains("999");
        assertThat(exception.args()).containsEntry("imageVariantId", 999L);
    }

    @Test
    @DisplayName("ImageVariantErrorCode 속성이 올바르다")
    void imageVariantErrorCodeTest() {
        ImageVariantErrorCode errorCode = ImageVariantErrorCode.IMAGE_VARIANT_NOT_FOUND;
        assertThat(errorCode.getCode()).isEqualTo("IMGVAR-001");
        assertThat(errorCode.getHttpStatus()).isEqualTo(404);
        assertThat(errorCode.getMessage()).contains("찾을 수 없습니다");
    }
}
