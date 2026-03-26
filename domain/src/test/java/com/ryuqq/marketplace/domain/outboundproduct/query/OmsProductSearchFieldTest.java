package com.ryuqq.marketplace.domain.outboundproduct.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OmsProductSearchField enum 단위 테스트")
class OmsProductSearchFieldTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("PRODUCT_CODE의 fieldName은 productCode이다")
        void productCodeFieldName() {
            assertThat(OmsProductSearchField.PRODUCT_CODE.fieldName()).isEqualTo("productCode");
        }

        @Test
        @DisplayName("PRODUCT_NAME의 fieldName은 productName이다")
        void productNameFieldName() {
            assertThat(OmsProductSearchField.PRODUCT_NAME.fieldName()).isEqualTo("productName");
        }

        @Test
        @DisplayName("PARTNER_NAME의 fieldName은 partnerName이다")
        void partnerNameFieldName() {
            assertThat(OmsProductSearchField.PARTNER_NAME.fieldName()).isEqualTo("partnerName");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("fieldName 문자열로 변환한다")
        void fromFieldName() {
            assertThat(OmsProductSearchField.fromString("productCode"))
                    .isEqualTo(OmsProductSearchField.PRODUCT_CODE);
        }

        @Test
        @DisplayName("enum name 문자열로 변환한다")
        void fromEnumName() {
            assertThat(OmsProductSearchField.fromString("PRODUCT_CODE"))
                    .isEqualTo(OmsProductSearchField.PRODUCT_CODE);
        }

        @Test
        @DisplayName("대소문자 무관하게 변환한다")
        void fromStringCaseInsensitive() {
            assertThat(OmsProductSearchField.fromString("PRODUCTNAME"))
                    .isEqualTo(OmsProductSearchField.PRODUCT_NAME);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void fromNullReturnsNull() {
            assertThat(OmsProductSearchField.fromString(null)).isNull();
        }

        @Test
        @DisplayName("빈 문자열이면 null을 반환한다")
        void fromBlankReturnsNull() {
            assertThat(OmsProductSearchField.fromString("  ")).isNull();
        }

        @Test
        @DisplayName("알 수 없는 값이면 null을 반환한다")
        void fromUnknownValueReturnsNull() {
            assertThat(OmsProductSearchField.fromString("unknownField")).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValuesTest {

        @Test
        @DisplayName("OmsProductSearchField는 3가지 값을 가진다")
        void hasCorrectNumberOfValues() {
            assertThat(OmsProductSearchField.values()).hasSize(3);
        }
    }
}
