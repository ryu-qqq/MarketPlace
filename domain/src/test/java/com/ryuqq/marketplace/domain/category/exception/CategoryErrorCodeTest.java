package com.ryuqq.marketplace.domain.category.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Category 예외 테스트")
class CategoryErrorCodeTest {

    @Nested
    @DisplayName("CategoryErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("CATEGORY_NOT_FOUND 에러 코드가 올바르다")
        void categoryNotFound() {
            CategoryErrorCode code = CategoryErrorCode.CATEGORY_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("CAT-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("CATEGORY_CODE_DUPLICATE 에러 코드가 올바르다")
        void categoryCodeDuplicate() {
            CategoryErrorCode code = CategoryErrorCode.CATEGORY_CODE_DUPLICATE;

            assertThat(code.getCode()).isEqualTo("CAT-002");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("카테고리 코드");
        }

        @Test
        @DisplayName("CATEGORY_DEPTH_EXCEEDED 에러 코드가 올바르다")
        void categoryDepthExceeded() {
            CategoryErrorCode code = CategoryErrorCode.CATEGORY_DEPTH_EXCEEDED;

            assertThat(code.getCode()).isEqualTo("CAT-004");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("깊이");
        }
    }

    @Nested
    @DisplayName("CategoryException 예외 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("CategoryException은 ErrorCode만으로 생성된다")
        void categoryExceptionWithErrorCode() {
            CategoryException exception =
                    new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("CAT-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("CategoryException은 커스텀 메시지를 지원한다")
        void categoryExceptionWithCustomMessage() {
            CategoryException exception =
                    new CategoryException(
                            CategoryErrorCode.CATEGORY_NOT_FOUND, "ID가 1인 카테고리를 찾을 수 없습니다");

            assertThat(exception.code()).isEqualTo("CAT-001");
            assertThat(exception.getMessage()).isEqualTo("ID가 1인 카테고리를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("CategoryException은 원인 예외를 지원한다")
        void categoryExceptionWithCause() {
            RuntimeException cause = new RuntimeException("원인 예외");

            CategoryException exception =
                    new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("CAT-001");
        }

        @Test
        @DisplayName("CATEGORY_CODE_DUPLICATE로 생성한 예외의 HTTP 상태는 409다")
        void codeDuplicateExceptionHttpStatus() {
            CategoryException exception =
                    new CategoryException(CategoryErrorCode.CATEGORY_CODE_DUPLICATE);

            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }

    @Test
    @DisplayName("CategoryErrorCode 값이 3개 존재한다")
    void hasExpectedValues() {
        assertThat(CategoryErrorCode.values()).hasSize(3);
    }
}
