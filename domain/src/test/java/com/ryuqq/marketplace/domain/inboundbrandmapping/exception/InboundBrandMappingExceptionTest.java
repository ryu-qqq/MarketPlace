package com.ryuqq.marketplace.domain.inboundbrandmapping.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundBrandMapping 예외 테스트")
class InboundBrandMappingExceptionTest {

    @Nested
    @DisplayName("InboundBrandMappingErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("EXTERNAL_BRAND_MAPPING_NOT_FOUND 에러 코드가 올바르다")
        void notFoundErrorCode() {
            InboundBrandMappingErrorCode code =
                    InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("EBM-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("EXTERNAL_BRAND_MAPPING_DUPLICATE 에러 코드가 올바르다")
        void duplicateErrorCode() {
            InboundBrandMappingErrorCode code =
                    InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_DUPLICATE;

            assertThat(code.getCode()).isEqualTo("EBM-002");
            assertThat(code.getHttpStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("InboundBrandMappingException 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("ErrorCode만으로 예외를 생성한다")
        void createExceptionWithErrorCode() {
            InboundBrandMappingException exception =
                    new InboundBrandMappingException(
                            InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("EBM-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("커스텀 메시지로 예외를 생성한다")
        void createExceptionWithCustomMessage() {
            InboundBrandMappingException exception =
                    new InboundBrandMappingException(
                            InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_DUPLICATE,
                            "소스ID=1, 브랜드코드=BR001은 이미 존재합니다");

            assertThat(exception.getMessage()).contains("BR001");
        }

        @Test
        @DisplayName("원인 예외를 포함하여 예외를 생성한다")
        void createExceptionWithCause() {
            RuntimeException cause = new RuntimeException("DB 오류");

            InboundBrandMappingException exception =
                    new InboundBrandMappingException(
                            InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_NOT_FOUND, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }
}
