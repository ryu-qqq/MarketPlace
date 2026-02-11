package com.ryuqq.marketplace.domain.categorypreset.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPresetErrorCode 테스트")
class CategoryPresetErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            assertThat(CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND)
                    .isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("카테고리 프리셋 에러 코드 테스트")
    class CategoryPresetErrorCodesTest {

        @Test
        @DisplayName("CATEGORY_PRESET_NOT_FOUND 에러 코드를 검증한다")
        void categoryPresetNotFound() {
            assertThat(CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND.getCode())
                    .isEqualTo("CATPRE-001");
            assertThat(CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
            assertThat(CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND.getMessage())
                    .isEqualTo("카테고리 프리셋을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("CATEGORY_PRESET_CHANNEL_MISMATCH 에러 코드를 검증한다")
        void categoryPresetChannelMismatch() {
            assertThat(CategoryPresetErrorCode.CATEGORY_PRESET_CHANNEL_MISMATCH.getCode())
                    .isEqualTo("CATPRE-002");
            assertThat(CategoryPresetErrorCode.CATEGORY_PRESET_CHANNEL_MISMATCH.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("CATEGORY_PRESET_INTERNAL_CATEGORY_NOT_FOUND 에러 코드를 검증한다")
        void categoryPresetInternalCategoryNotFound() {
            assertThat(
                            CategoryPresetErrorCode.CATEGORY_PRESET_INTERNAL_CATEGORY_NOT_FOUND
                                    .getCode())
                    .isEqualTo("CATPRE-003");
            assertThat(
                            CategoryPresetErrorCode.CATEGORY_PRESET_INTERNAL_CATEGORY_NOT_FOUND
                                    .getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("CATEGORY_PRESET_SALES_CHANNEL_CATEGORY_NOT_FOUND 에러 코드를 검증한다")
        void categoryPresetSalesChannelCategoryNotFound() {
            assertThat(
                            CategoryPresetErrorCode
                                    .CATEGORY_PRESET_SALES_CHANNEL_CATEGORY_NOT_FOUND
                                    .getCode())
                    .isEqualTo("CATPRE-004");
            assertThat(
                            CategoryPresetErrorCode
                                    .CATEGORY_PRESET_SALES_CHANNEL_CATEGORY_NOT_FOUND
                                    .getHttpStatus())
                    .isEqualTo(404);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            assertThat(CategoryPresetErrorCode.values())
                    .containsExactly(
                            CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND,
                            CategoryPresetErrorCode.CATEGORY_PRESET_CHANNEL_MISMATCH,
                            CategoryPresetErrorCode.CATEGORY_PRESET_INTERNAL_CATEGORY_NOT_FOUND,
                            CategoryPresetErrorCode
                                    .CATEGORY_PRESET_SALES_CHANNEL_CATEGORY_NOT_FOUND);
        }
    }
}
