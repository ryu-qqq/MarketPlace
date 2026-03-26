package com.ryuqq.marketplace.domain.inboundcategorymapping.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundCategoryMapping 예외 테스트")
class InboundCategoryMappingExceptionTest {

    @Nested
    @DisplayName("InboundCategoryMappingErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("EXTERNAL_CATEGORY_MAPPING_NOT_FOUND 에러 코드가 올바르다")
        void notFoundErrorCode() {
            InboundCategoryMappingErrorCode code =
                    InboundCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("ECM-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("EXTERNAL_CATEGORY_MAPPING_DUPLICATE 에러 코드가 올바르다")
        void duplicateErrorCode() {
            InboundCategoryMappingErrorCode code =
                    InboundCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_DUPLICATE;

            assertThat(code.getCode()).isEqualTo("ECM-002");
            assertThat(code.getHttpStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("InboundCategoryMappingException 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("ErrorCode만으로 예외를 생성한다")
        void createExceptionWithErrorCode() {
            InboundCategoryMappingException exception =
                    new InboundCategoryMappingException(
                            InboundCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("ECM-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("커스텀 메시지로 예외를 생성한다")
        void createExceptionWithCustomMessage() {
            InboundCategoryMappingException exception =
                    new InboundCategoryMappingException(
                            InboundCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_DUPLICATE,
                            "소스ID=1, 카테고리코드=CAT001은 이미 존재합니다");

            assertThat(exception.getMessage()).contains("CAT001");
        }

        @Test
        @DisplayName("원인 예외를 포함하여 예외를 생성한다")
        void createExceptionWithCause() {
            RuntimeException cause = new RuntimeException("DB 오류");

            InboundCategoryMappingException exception =
                    new InboundCategoryMappingException(
                            InboundCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_NOT_FOUND,
                            cause);

            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }
}
