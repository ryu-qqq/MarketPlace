package com.ryuqq.marketplace.domain.exchange.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeSearchField 단위 테스트")
class ExchangeSearchFieldTest {

    @Nested
    @DisplayName("fieldName() - 필드명 반환")
    class FieldNameTest {

        @Test
        @DisplayName("CLAIM_NUMBER의 fieldName은 claimNumber이다")
        void claimNumberFieldName() {
            assertThat(ExchangeSearchField.CLAIM_NUMBER.fieldName()).isEqualTo("claimNumber");
        }

        @Test
        @DisplayName("ORDER_NUMBER의 fieldName은 orderNumber이다")
        void orderNumberFieldName() {
            assertThat(ExchangeSearchField.ORDER_NUMBER.fieldName()).isEqualTo("orderNumber");
        }

        @Test
        @DisplayName("CUSTOMER_NAME의 fieldName은 customerName이다")
        void customerNameFieldName() {
            assertThat(ExchangeSearchField.CUSTOMER_NAME.fieldName()).isEqualTo("customerName");
        }

        @Test
        @DisplayName("CUSTOMER_PHONE의 fieldName은 customerPhone이다")
        void customerPhoneFieldName() {
            assertThat(ExchangeSearchField.CUSTOMER_PHONE.fieldName()).isEqualTo("customerPhone");
        }

        @Test
        @DisplayName("PRODUCT_NAME의 fieldName은 productName이다")
        void productNameFieldName() {
            assertThat(ExchangeSearchField.PRODUCT_NAME.fieldName()).isEqualTo("productName");
        }
    }

    @Nested
    @DisplayName("fromString() - 문자열로 변환")
    class FromStringTest {

        @Test
        @DisplayName("fieldName으로 찾을 수 있다")
        void findByFieldName() {
            assertThat(ExchangeSearchField.fromString("claimNumber"))
                    .isEqualTo(ExchangeSearchField.CLAIM_NUMBER);
            assertThat(ExchangeSearchField.fromString("orderNumber"))
                    .isEqualTo(ExchangeSearchField.ORDER_NUMBER);
        }

        @Test
        @DisplayName("enum 이름으로 찾을 수 있다")
        void findByEnumName() {
            assertThat(ExchangeSearchField.fromString("CLAIM_NUMBER"))
                    .isEqualTo(ExchangeSearchField.CLAIM_NUMBER);
        }

        @Test
        @DisplayName("fieldName과 enum 이름 모두 대소문자 무시 비교로 찾을 수 있다")
        void findCaseInsensitive() {
            // fieldName "claimNumber"를 대소문자 무시 비교 → "CLAIMNUMBER"도 일치
            assertThat(ExchangeSearchField.fromString("CLAIMNUMBER"))
                    .isEqualTo(ExchangeSearchField.CLAIM_NUMBER);
            // enum name "CLAIM_NUMBER" 대소문자 무시 → "claim_number"도 일치
            assertThat(ExchangeSearchField.fromString("claim_number"))
                    .isEqualTo(ExchangeSearchField.CLAIM_NUMBER);
            assertThat(ExchangeSearchField.fromString("claimNumber"))
                    .isEqualTo(ExchangeSearchField.CLAIM_NUMBER);
        }

        @Test
        @DisplayName("null을 전달하면 null을 반환한다")
        void returnNullForNull() {
            assertThat(ExchangeSearchField.fromString(null)).isNull();
        }

        @Test
        @DisplayName("빈 문자열을 전달하면 null을 반환한다")
        void returnNullForBlank() {
            assertThat(ExchangeSearchField.fromString("")).isNull();
            assertThat(ExchangeSearchField.fromString("   ")).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 값이면 null을 반환한다")
        void returnNullForUnknown() {
            assertThat(ExchangeSearchField.fromString("unknown")).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("5개의 검색 필드가 존재한다")
        void fiveSearchFieldsExist() {
            assertThat(ExchangeSearchField.values()).hasSize(5);
        }

        @Test
        @DisplayName("모든 검색 필드 값이 존재한다")
        void allValuesExist() {
            assertThat(ExchangeSearchField.values())
                    .containsExactly(
                            ExchangeSearchField.CLAIM_NUMBER,
                            ExchangeSearchField.ORDER_NUMBER,
                            ExchangeSearchField.CUSTOMER_NAME,
                            ExchangeSearchField.CUSTOMER_PHONE,
                            ExchangeSearchField.PRODUCT_NAME);
        }
    }
}
