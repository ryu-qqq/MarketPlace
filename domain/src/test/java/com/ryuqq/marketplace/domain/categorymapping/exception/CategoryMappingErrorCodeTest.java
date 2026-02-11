package com.ryuqq.marketplace.domain.categorymapping.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryMappingErrorCode 테스트")
class CategoryMappingErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            assertThat(CategoryMappingErrorCode.CATEGORY_MAPPING_NOT_FOUND)
                    .isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("카테고리 매핑 에러 코드 테스트")
    class CategoryMappingErrorCodesTest {

        @Test
        @DisplayName("CATEGORY_MAPPING_NOT_FOUND 에러 코드를 검증한다")
        void categoryMappingNotFound() {
            assertThat(CategoryMappingErrorCode.CATEGORY_MAPPING_NOT_FOUND.getCode())
                    .isEqualTo("CATMAP-001");
            assertThat(CategoryMappingErrorCode.CATEGORY_MAPPING_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
            assertThat(CategoryMappingErrorCode.CATEGORY_MAPPING_NOT_FOUND.getMessage())
                    .isEqualTo("카테고리 매핑을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("CATEGORY_MAPPING_DUPLICATE 에러 코드를 검증한다")
        void categoryMappingDuplicate() {
            assertThat(CategoryMappingErrorCode.CATEGORY_MAPPING_DUPLICATE.getCode())
                    .isEqualTo("CATMAP-002");
            assertThat(CategoryMappingErrorCode.CATEGORY_MAPPING_DUPLICATE.getHttpStatus())
                    .isEqualTo(409);
            assertThat(CategoryMappingErrorCode.CATEGORY_MAPPING_DUPLICATE.getMessage())
                    .isEqualTo("해당 외부 카테고리에 이미 매핑이 존재합니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            assertThat(CategoryMappingErrorCode.values())
                    .containsExactly(
                            CategoryMappingErrorCode.CATEGORY_MAPPING_NOT_FOUND,
                            CategoryMappingErrorCode.CATEGORY_MAPPING_DUPLICATE);
        }
    }
}
