package com.ryuqq.marketplace.domain.seller.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.SearchField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("unit")
@DisplayName("SellerSearchField 테스트")
class SellerSearchFieldTest {

    @Nested
    @DisplayName("SearchField 인터페이스 구현 테스트")
    class SearchFieldInterfaceTest {

        @Test
        @DisplayName("SearchField 인터페이스를 구현한다")
        void implementsSearchField() {
            // then
            assertThat(SellerSearchField.SELLER_NAME).isInstanceOf(SearchField.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("SELLER_NAME의 필드명은 sellerName이다")
        void sellerNameFieldName() {
            // then
            assertThat(SellerSearchField.SELLER_NAME.fieldName()).isEqualTo("sellerName");
        }

        @Test
        @DisplayName("REGISTRATION_NUMBER의 필드명은 registrationNumber이다")
        void registrationNumberFieldName() {
            // then
            assertThat(SellerSearchField.REGISTRATION_NUMBER.fieldName())
                    .isEqualTo("registrationNumber");
        }

        @Test
        @DisplayName("COMPANY_NAME의 필드명은 companyName이다")
        void companyNameFieldName() {
            // then
            assertThat(SellerSearchField.COMPANY_NAME.fieldName()).isEqualTo("companyName");
        }

        @Test
        @DisplayName("REPRESENTATIVE_NAME의 필드명은 representativeName이다")
        void representativeNameFieldName() {
            // then
            assertThat(SellerSearchField.REPRESENTATIVE_NAME.fieldName())
                    .isEqualTo("representativeName");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("필드명으로 SellerSearchField를 찾는다")
        void findByFieldName() {
            // then
            assertThat(SellerSearchField.fromString("sellerName"))
                    .isEqualTo(SellerSearchField.SELLER_NAME);
            assertThat(SellerSearchField.fromString("registrationNumber"))
                    .isEqualTo(SellerSearchField.REGISTRATION_NUMBER);
        }

        @Test
        @DisplayName("enum 이름으로 SellerSearchField를 찾는다")
        void findByEnumName() {
            // then
            assertThat(SellerSearchField.fromString("SELLER_NAME"))
                    .isEqualTo(SellerSearchField.SELLER_NAME);
            assertThat(SellerSearchField.fromString("COMPANY_NAME"))
                    .isEqualTo(SellerSearchField.COMPANY_NAME);
        }

        @Test
        @DisplayName("대소문자를 무시하고 SellerSearchField를 찾는다")
        void findCaseInsensitive() {
            // then
            assertThat(SellerSearchField.fromString("SELLERNAME"))
                    .isEqualTo(SellerSearchField.SELLER_NAME);
            assertThat(SellerSearchField.fromString("seller_name"))
                    .isEqualTo(SellerSearchField.SELLER_NAME);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("null이나 빈 문자열이면 null을 반환한다")
        void returnNullForNullOrEmpty(String value) {
            // then
            assertThat(SellerSearchField.fromString(value)).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 필드명이면 null을 반환한다")
        void returnNullForUnknownField() {
            // then
            assertThat(SellerSearchField.fromString("unknownField")).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 검색 필드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(SellerSearchField.values())
                    .containsExactly(
                            SellerSearchField.SELLER_NAME,
                            SellerSearchField.REGISTRATION_NUMBER,
                            SellerSearchField.COMPANY_NAME,
                            SellerSearchField.REPRESENTATIVE_NAME);
        }
    }
}
