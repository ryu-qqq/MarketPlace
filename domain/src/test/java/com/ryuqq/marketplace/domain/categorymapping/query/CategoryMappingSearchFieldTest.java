package com.ryuqq.marketplace.domain.categorymapping.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryMappingSearchField 테스트")
class CategoryMappingSearchFieldTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("EXTERNAL_CATEGORY_NAME의 fieldName을 반환한다")
        void externalCategoryNameFieldName() {
            assertThat(CategoryMappingSearchField.EXTERNAL_CATEGORY_NAME.fieldName())
                    .isEqualTo("externalCategoryName");
        }

        @Test
        @DisplayName("INTERNAL_CATEGORY_NAME의 fieldName을 반환한다")
        void internalCategoryNameFieldName() {
            assertThat(CategoryMappingSearchField.INTERNAL_CATEGORY_NAME.fieldName())
                    .isEqualTo("internalCategoryName");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("유효한 fieldName으로 검색 필드를 변환한다")
        void convertValidFieldName() {
            assertThat(CategoryMappingSearchField.fromString("externalCategoryName"))
                    .isEqualTo(CategoryMappingSearchField.EXTERNAL_CATEGORY_NAME);
            assertThat(CategoryMappingSearchField.fromString("internalCategoryName"))
                    .isEqualTo(CategoryMappingSearchField.INTERNAL_CATEGORY_NAME);
        }

        @Test
        @DisplayName("enum name으로 검색 필드를 변환한다")
        void convertEnumName() {
            assertThat(CategoryMappingSearchField.fromString("EXTERNAL_CATEGORY_NAME"))
                    .isEqualTo(CategoryMappingSearchField.EXTERNAL_CATEGORY_NAME);
            assertThat(CategoryMappingSearchField.fromString("INTERNAL_CATEGORY_NAME"))
                    .isEqualTo(CategoryMappingSearchField.INTERNAL_CATEGORY_NAME);
        }

        @Test
        @DisplayName("대소문자를 무시하고 변환한다")
        void convertCaseInsensitive() {
            assertThat(CategoryMappingSearchField.fromString("externalcategoryname"))
                    .isEqualTo(CategoryMappingSearchField.EXTERNAL_CATEGORY_NAME);
            assertThat(CategoryMappingSearchField.fromString("INTERNALCATEGORYNAME"))
                    .isEqualTo(CategoryMappingSearchField.INTERNAL_CATEGORY_NAME);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void nullReturnsNull() {
            assertThat(CategoryMappingSearchField.fromString(null)).isNull();
        }

        @Test
        @DisplayName("빈 문자열이면 null을 반환한다")
        void blankReturnsNull() {
            assertThat(CategoryMappingSearchField.fromString("")).isNull();
            assertThat(CategoryMappingSearchField.fromString("   ")).isNull();
        }

        @Test
        @DisplayName("유효하지 않은 문자열이면 null을 반환한다")
        void invalidStringReturnsNull() {
            assertThat(CategoryMappingSearchField.fromString("UNKNOWN")).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 검색 필드 값이 존재한다")
        void allValuesExist() {
            assertThat(CategoryMappingSearchField.values())
                    .containsExactly(
                            CategoryMappingSearchField.EXTERNAL_CATEGORY_NAME,
                            CategoryMappingSearchField.INTERNAL_CATEGORY_NAME);
        }
    }
}
