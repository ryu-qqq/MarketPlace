package com.ryuqq.marketplace.domain.brandmapping.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SearchField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandMappingSearchField 테스트")
class BrandMappingSearchFieldTest {

    @Nested
    @DisplayName("SearchField 인터페이스 구현 테스트")
    class SearchFieldInterfaceTest {

        @Test
        @DisplayName("SearchField 인터페이스를 구현한다")
        void implementsSearchField() {
            assertThat(BrandMappingSearchField.EXTERNAL_BRAND_NAME)
                    .isInstanceOf(SearchField.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("EXTERNAL_BRAND_NAME의 필드명은 externalBrandName이다")
        void externalBrandNameFieldName() {
            assertThat(BrandMappingSearchField.EXTERNAL_BRAND_NAME.fieldName())
                    .isEqualTo("externalBrandName");
        }

        @Test
        @DisplayName("INTERNAL_BRAND_NAME의 필드명은 internalBrandName이다")
        void internalBrandNameFieldName() {
            assertThat(BrandMappingSearchField.INTERNAL_BRAND_NAME.fieldName())
                    .isEqualTo("internalBrandName");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("필드명으로 SearchField를 찾는다")
        void findByFieldName() {
            assertThat(BrandMappingSearchField.fromString("externalBrandName"))
                    .isEqualTo(BrandMappingSearchField.EXTERNAL_BRAND_NAME);
            assertThat(BrandMappingSearchField.fromString("internalBrandName"))
                    .isEqualTo(BrandMappingSearchField.INTERNAL_BRAND_NAME);
        }

        @Test
        @DisplayName("enum 이름으로 SearchField를 찾는다")
        void findByEnumName() {
            assertThat(BrandMappingSearchField.fromString("EXTERNAL_BRAND_NAME"))
                    .isEqualTo(BrandMappingSearchField.EXTERNAL_BRAND_NAME);
        }

        @Test
        @DisplayName("대소문자를 무시하고 SearchField를 찾는다")
        void findCaseInsensitive() {
            assertThat(BrandMappingSearchField.fromString("EXTERNALBRANDNAME"))
                    .isEqualTo(BrandMappingSearchField.EXTERNAL_BRAND_NAME);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void nullReturnsNull() {
            assertThat(BrandMappingSearchField.fromString(null)).isNull();
        }

        @Test
        @DisplayName("빈 문자열이면 null을 반환한다")
        void blankReturnsNull() {
            assertThat(BrandMappingSearchField.fromString("")).isNull();
            assertThat(BrandMappingSearchField.fromString("   ")).isNull();
        }

        @Test
        @DisplayName("유효하지 않은 문자열이면 null을 반환한다")
        void invalidStringReturnsNull() {
            assertThat(BrandMappingSearchField.fromString("unknown")).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 검색 필드 값이 존재한다")
        void allValuesExist() {
            assertThat(BrandMappingSearchField.values())
                    .containsExactly(
                            BrandMappingSearchField.EXTERNAL_BRAND_NAME,
                            BrandMappingSearchField.INTERNAL_BRAND_NAME);
        }
    }
}
