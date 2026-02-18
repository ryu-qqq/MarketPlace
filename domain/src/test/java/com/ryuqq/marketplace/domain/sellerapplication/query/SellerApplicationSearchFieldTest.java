package com.ryuqq.marketplace.domain.sellerapplication.query;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerApplicationSearchField 테스트")
class SellerApplicationSearchFieldTest {

    @Nested
    @DisplayName("fieldName() - 필드명 반환")
    class FieldNameTest {

        @Test
        @DisplayName("COMPANY_NAME의 필드명은 companyName이다")
        void companyNameFieldName() {
            assertThat(SellerApplicationSearchField.COMPANY_NAME.fieldName())
                    .isEqualTo("companyName");
        }

        @Test
        @DisplayName("REPRESENTATIVE_NAME의 필드명은 representativeName이다")
        void representativeNameFieldName() {
            assertThat(SellerApplicationSearchField.REPRESENTATIVE_NAME.fieldName())
                    .isEqualTo("representativeName");
        }
    }

    @Nested
    @DisplayName("fromString() - 문자열 변환")
    class FromStringTest {

        @Test
        @DisplayName("필드명으로 변환한다")
        void fromFieldName() {
            assertThat(SellerApplicationSearchField.fromString("companyName"))
                    .isEqualTo(SellerApplicationSearchField.COMPANY_NAME);
            assertThat(SellerApplicationSearchField.fromString("representativeName"))
                    .isEqualTo(SellerApplicationSearchField.REPRESENTATIVE_NAME);
        }

        @Test
        @DisplayName("enum 이름으로 변환한다")
        void fromEnumName() {
            assertThat(SellerApplicationSearchField.fromString("COMPANY_NAME"))
                    .isEqualTo(SellerApplicationSearchField.COMPANY_NAME);
            assertThat(SellerApplicationSearchField.fromString("REPRESENTATIVE_NAME"))
                    .isEqualTo(SellerApplicationSearchField.REPRESENTATIVE_NAME);
        }

        @Test
        @DisplayName("대소문자 구분 없이 변환한다")
        void caseInsensitive() {
            assertThat(SellerApplicationSearchField.fromString("companyname"))
                    .isEqualTo(SellerApplicationSearchField.COMPANY_NAME);
            assertThat(SellerApplicationSearchField.fromString("COMPANYNAME"))
                    .isEqualTo(SellerApplicationSearchField.COMPANY_NAME);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void nullReturnsNull() {
            assertThat(SellerApplicationSearchField.fromString(null)).isNull();
        }

        @Test
        @DisplayName("빈 문자열이면 null을 반환한다")
        void blankReturnsNull() {
            assertThat(SellerApplicationSearchField.fromString("")).isNull();
            assertThat(SellerApplicationSearchField.fromString("   ")).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 값이면 null을 반환한다")
        void unknownReturnsNull() {
            assertThat(SellerApplicationSearchField.fromString("unknownField")).isNull();
        }
    }
}
