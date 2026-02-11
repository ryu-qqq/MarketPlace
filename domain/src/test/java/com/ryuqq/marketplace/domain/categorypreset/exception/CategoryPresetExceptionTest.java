package com.ryuqq.marketplace.domain.categorypreset.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPresetException 테스트")
class CategoryPresetExceptionTest {

    @Nested
    @DisplayName("기본 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("ErrorCode로 예외를 생성한다")
        void createWithErrorCode() {
            CategoryPresetException exception =
                    new CategoryPresetException(CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND);

            assertThat(exception.getMessage()).isEqualTo("카테고리 프리셋을 찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("CATPRE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndMessage() {
            CategoryPresetException exception = new CategoryPresetException(
                    CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND, "ID 100 프리셋 없음");

            assertThat(exception.getMessage()).isEqualTo("ID 100 프리셋 없음");
            assertThat(exception.code()).isEqualTo("CATPRE-001");
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            RuntimeException cause = new RuntimeException("원인 예외");
            CategoryPresetException exception = new CategoryPresetException(
                    CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("CATPRE-001");
        }
    }

    @Nested
    @DisplayName("구체적 예외 클래스 테스트")
    class ConcreteExceptionTest {

        @Test
        @DisplayName("CategoryPresetNotFoundException 기본 생성")
        void createCategoryPresetNotFoundException() {
            CategoryPresetNotFoundException exception = new CategoryPresetNotFoundException();

            assertThat(exception.code()).isEqualTo("CATPRE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("카테고리 프리셋을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("CategoryPresetNotFoundException ID 포함 생성")
        void createCategoryPresetNotFoundExceptionWithId() {
            CategoryPresetNotFoundException exception = new CategoryPresetNotFoundException(456L);

            assertThat(exception.code()).isEqualTo("CATPRE-001");
            assertThat(exception.getMessage()).contains("456");
        }

        @Test
        @DisplayName("CategoryPresetChannelMismatchException 생성")
        void createChannelMismatchException() {
            CategoryPresetChannelMismatchException exception =
                    new CategoryPresetChannelMismatchException(1L, 2L);

            assertThat(exception.code()).isEqualTo("CATPRE-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).contains("1").contains("2");
        }

        @Test
        @DisplayName("CategoryPresetInternalCategoryNotFoundException 생성")
        void createInternalCategoryNotFoundException() {
            CategoryPresetInternalCategoryNotFoundException exception =
                    new CategoryPresetInternalCategoryNotFoundException(List.of(10L, 20L));

            assertThat(exception.code()).isEqualTo("CATPRE-003");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).contains("10").contains("20");
        }

        @Test
        @DisplayName("CategoryPresetSalesChannelCategoryNotFoundException 생성")
        void createSalesChannelCategoryNotFoundException() {
            CategoryPresetSalesChannelCategoryNotFoundException exception =
                    new CategoryPresetSalesChannelCategoryNotFoundException("CAT-001");

            assertThat(exception.code()).isEqualTo("CATPRE-004");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("CAT-001");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("CategoryPresetException은 DomainException을 상속한다")
        void categoryPresetExceptionExtendsDomainException() {
            CategoryPresetException exception = new CategoryPresetException(
                    CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND);
            assertThat(exception).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("CategoryPresetNotFoundException은 CategoryPresetException을 상속한다")
        void notFoundExceptionExtendsCategoryPresetException() {
            CategoryPresetNotFoundException exception = new CategoryPresetNotFoundException();
            assertThat(exception).isInstanceOf(CategoryPresetException.class);
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }
}
