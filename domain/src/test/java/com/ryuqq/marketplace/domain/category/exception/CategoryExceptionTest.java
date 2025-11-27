package com.ryuqq.marketplace.domain.category.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Category Exception 단위 테스트
 *
 * <p><strong>테스트 대상 (4개)</strong>:</p>
 * <ul>
 *   <li>CategoryErrorCode (enum)</li>
 *   <li>CategoryNotFoundException</li>
 *   <li>CategoryCodeDuplicateException</li>
 *   <li>CategoryHasChildrenException</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Category Exception 단위 테스트")
@Tag("unit")
@Tag("domain")
@Tag("category")
@Tag("exception")
class CategoryExceptionTest {

    // ==================== CategoryErrorCode 테스트 ====================

    @Nested
    @DisplayName("CategoryErrorCode 테스트")
    class CategoryErrorCodeTest {

        @Test
        @DisplayName("[성공] CATEGORY_NOT_FOUND 에러 코드 확인")
        void categoryNotFound_ShouldHaveCorrectValues() {
            CategoryErrorCode errorCode = CategoryErrorCode.CATEGORY_NOT_FOUND;

            assertThat(errorCode.getCode()).isEqualTo("CATEGORY-001");
            assertThat(errorCode.getHttpStatus()).isEqualTo(404);
            assertThat(errorCode.getMessage()).isEqualTo("카테고리를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("[성공] CATEGORY_CODE_DUPLICATE 에러 코드 확인")
        void categoryCodeDuplicate_ShouldHaveCorrectValues() {
            CategoryErrorCode errorCode = CategoryErrorCode.CATEGORY_CODE_DUPLICATE;

            assertThat(errorCode.getCode()).isEqualTo("CATEGORY-002");
            assertThat(errorCode.getHttpStatus()).isEqualTo(409);
            assertThat(errorCode.getMessage()).isEqualTo("카테고리 코드가 중복됩니다");
        }

        @Test
        @DisplayName("[성공] CATEGORY_HAS_CHILDREN 에러 코드 확인")
        void categoryHasChildren_ShouldHaveCorrectValues() {
            CategoryErrorCode errorCode = CategoryErrorCode.CATEGORY_HAS_CHILDREN;

            assertThat(errorCode.getCode()).isEqualTo("CATEGORY-003");
            assertThat(errorCode.getHttpStatus()).isEqualTo(400);
            assertThat(errorCode.getMessage()).isEqualTo("하위 카테고리가 있어 삭제할 수 없습니다");
        }

        @Test
        @DisplayName("[성공] CATEGORY_MAX_DEPTH_EXCEEDED 에러 코드 확인")
        void categoryMaxDepthExceeded_ShouldHaveCorrectValues() {
            CategoryErrorCode errorCode = CategoryErrorCode.CATEGORY_MAX_DEPTH_EXCEEDED;

            assertThat(errorCode.getCode()).isEqualTo("CATEGORY-004");
            assertThat(errorCode.getHttpStatus()).isEqualTo(400);
            assertThat(errorCode.getMessage()).isEqualTo("카테고리 최대 깊이를 초과했습니다");
        }

        @Test
        @DisplayName("[성공] 모든 에러 코드 값 확인")
        void allErrorCodes_ShouldExist() {
            assertThat(CategoryErrorCode.values()).hasSize(4);
        }

        @Test
        @DisplayName("[성공] ErrorCode 인터페이스 구현 확인")
        void shouldImplementErrorCode() {
            for (CategoryErrorCode errorCode : CategoryErrorCode.values()) {
                assertThat(errorCode.getCode()).isNotNull();
                assertThat(errorCode.getHttpStatus()).isGreaterThan(0);
                assertThat(errorCode.getMessage()).isNotNull();
            }
        }
    }

    // ==================== CategoryNotFoundException 테스트 ====================

    @Nested
    @DisplayName("CategoryNotFoundException 테스트")
    class CategoryNotFoundExceptionTest {

        @Test
        @DisplayName("[성공] categoryId로 예외 생성")
        void constructor_WithCategoryId_ShouldCreate() {
            // Given
            Long categoryId = 123L;

            // When
            CategoryNotFoundException exception = new CategoryNotFoundException(categoryId);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("CATEGORY-001");
            assertThat(exception.getMessage()).isEqualTo("카테고리를 찾을 수 없습니다");
            assertThat(exception.args()).containsEntry("categoryId", categoryId);
        }

        @Test
        @DisplayName("[성공] code로 예외 생성")
        void constructor_WithCode_ShouldCreate() {
            // Given
            String code = "FASHION";

            // When
            CategoryNotFoundException exception = new CategoryNotFoundException(code);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("CATEGORY-001");
            assertThat(exception.getMessage()).isEqualTo("카테고리를 찾을 수 없습니다");
            assertThat(exception.args()).containsEntry("code", code);
        }

        @Test
        @DisplayName("[성공] DomainException 상속 확인")
        void shouldExtendDomainException() {
            // When
            CategoryNotFoundException exception = new CategoryNotFoundException(1L);

            // Then
            assertThat(exception).isInstanceOf(RuntimeException.class);
            assertThat(exception.code()).isNotNull();
            assertThat(exception.args()).isNotNull();
        }
    }

    // ==================== CategoryCodeDuplicateException 테스트 ====================

    @Nested
    @DisplayName("CategoryCodeDuplicateException 테스트")
    class CategoryCodeDuplicateExceptionTest {

        @Test
        @DisplayName("[성공] code로 예외 생성")
        void constructor_WithCode_ShouldCreate() {
            // Given
            String code = "FASHION";

            // When
            CategoryCodeDuplicateException exception = new CategoryCodeDuplicateException(code);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("CATEGORY-002");
            assertThat(exception.getMessage()).isEqualTo("카테고리 코드가 중복됩니다");
            assertThat(exception.args()).containsEntry("code", code);
        }

        @Test
        @DisplayName("[성공] DomainException 상속 확인")
        void shouldExtendDomainException() {
            // When
            CategoryCodeDuplicateException exception = new CategoryCodeDuplicateException("TEST");

            // Then
            assertThat(exception).isInstanceOf(RuntimeException.class);
            assertThat(exception.code()).isNotNull();
            assertThat(exception.args()).isNotNull();
        }
    }

    // ==================== CategoryHasChildrenException 테스트 ====================

    @Nested
    @DisplayName("CategoryHasChildrenException 테스트")
    class CategoryHasChildrenExceptionTest {

        @Test
        @DisplayName("[성공] categoryId로 예외 생성")
        void constructor_WithCategoryId_ShouldCreate() {
            // Given
            Long categoryId = 123L;

            // When
            CategoryHasChildrenException exception = new CategoryHasChildrenException(categoryId);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("CATEGORY-003");
            assertThat(exception.getMessage()).isEqualTo("하위 카테고리가 있어 삭제할 수 없습니다");
            assertThat(exception.args()).containsEntry("categoryId", categoryId);
        }

        @Test
        @DisplayName("[성공] DomainException 상속 확인")
        void shouldExtendDomainException() {
            // When
            CategoryHasChildrenException exception = new CategoryHasChildrenException(1L);

            // Then
            assertThat(exception).isInstanceOf(RuntimeException.class);
            assertThat(exception.code()).isNotNull();
            assertThat(exception.args()).isNotNull();
        }
    }
}
