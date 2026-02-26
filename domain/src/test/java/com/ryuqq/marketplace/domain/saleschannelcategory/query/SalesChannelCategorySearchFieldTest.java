package com.ryuqq.marketplace.domain.saleschannelcategory.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.SearchField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategorySearchField 테스트")
class SalesChannelCategorySearchFieldTest {

    @Nested
    @DisplayName("SearchField 인터페이스 구현 테스트")
    class SearchFieldInterfaceTest {

        @Test
        @DisplayName("SearchField 인터페이스를 구현한다")
        void implementsSearchField() {
            // then
            assertThat(SalesChannelCategorySearchField.EXTERNAL_CODE)
                    .isInstanceOf(SearchField.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 메서드 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("EXTERNAL_CODE의 필드명은 externalCategoryCode이다")
        void externalCodeFieldName() {
            // then
            assertThat(SalesChannelCategorySearchField.EXTERNAL_CODE.fieldName())
                    .isEqualTo("externalCategoryCode");
        }

        @Test
        @DisplayName("EXTERNAL_NAME의 필드명은 externalCategoryName이다")
        void externalNameFieldName() {
            // then
            assertThat(SalesChannelCategorySearchField.EXTERNAL_NAME.fieldName())
                    .isEqualTo("externalCategoryName");
        }
    }

    @Nested
    @DisplayName("fromString() 메서드 테스트")
    class FromStringTest {

        @Test
        @DisplayName("필드명 문자열로 EXTERNAL_CODE를 생성한다")
        void createExternalCodeFromFieldName() {
            // when
            SalesChannelCategorySearchField field =
                    SalesChannelCategorySearchField.fromString("externalCategoryCode");

            // then
            assertThat(field).isEqualTo(SalesChannelCategorySearchField.EXTERNAL_CODE);
        }

        @Test
        @DisplayName("enum 이름 문자열로 EXTERNAL_CODE를 생성한다")
        void createExternalCodeFromEnumName() {
            // when
            SalesChannelCategorySearchField field =
                    SalesChannelCategorySearchField.fromString("EXTERNAL_CODE");

            // then
            assertThat(field).isEqualTo(SalesChannelCategorySearchField.EXTERNAL_CODE);
        }

        @Test
        @DisplayName("대소문자 구분 없이 EXTERNAL_NAME을 생성한다")
        void createExternalNameCaseInsensitive() {
            // when
            SalesChannelCategorySearchField field1 =
                    SalesChannelCategorySearchField.fromString("externalcategoryname");
            SalesChannelCategorySearchField field2 =
                    SalesChannelCategorySearchField.fromString("EXTERNALCATEGORYNAME");

            // then
            assertThat(field1).isEqualTo(SalesChannelCategorySearchField.EXTERNAL_NAME);
            assertThat(field2).isEqualTo(SalesChannelCategorySearchField.EXTERNAL_NAME);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void nullReturnsNull() {
            // when
            SalesChannelCategorySearchField field =
                    SalesChannelCategorySearchField.fromString(null);

            // then
            assertThat(field).isNull();
        }

        @Test
        @DisplayName("빈 문자열이면 null을 반환한다")
        void blankStringReturnsNull() {
            // when
            SalesChannelCategorySearchField field =
                    SalesChannelCategorySearchField.fromString("   ");

            // then
            assertThat(field).isNull();
        }

        @Test
        @DisplayName("유효하지 않은 값이면 null을 반환한다")
        void invalidValueReturnsNull() {
            // when
            SalesChannelCategorySearchField field =
                    SalesChannelCategorySearchField.fromString("INVALID_FIELD");

            // then
            assertThat(field).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 검색 필드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(SalesChannelCategorySearchField.values())
                    .containsExactly(
                            SalesChannelCategorySearchField.EXTERNAL_CODE,
                            SalesChannelCategorySearchField.EXTERNAL_NAME);
        }
    }
}
