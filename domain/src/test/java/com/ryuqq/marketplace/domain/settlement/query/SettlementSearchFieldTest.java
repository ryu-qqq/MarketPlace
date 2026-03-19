package com.ryuqq.marketplace.domain.settlement.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementSearchField 단위 테스트")
class SettlementSearchFieldTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("ORDER_ID의 fieldName은 orderId이다")
        void orderIdFieldName() {
            assertThat(SettlementSearchField.ORDER_ID.fieldName()).isEqualTo("orderId");
        }

        @Test
        @DisplayName("ORDER_NUMBER의 fieldName은 orderNumber이다")
        void orderNumberFieldName() {
            assertThat(SettlementSearchField.ORDER_NUMBER.fieldName()).isEqualTo("orderNumber");
        }

        @Test
        @DisplayName("PRODUCT_NAME의 fieldName은 productName이다")
        void productNameFieldName() {
            assertThat(SettlementSearchField.PRODUCT_NAME.fieldName()).isEqualTo("productName");
        }

        @Test
        @DisplayName("BUYER_NAME의 fieldName은 buyerName이다")
        void buyerNameFieldName() {
            assertThat(SettlementSearchField.BUYER_NAME.fieldName()).isEqualTo("buyerName");
        }

        @Test
        @DisplayName("PAYMENT_ID의 fieldName은 paymentId이다")
        void paymentIdFieldName() {
            assertThat(SettlementSearchField.PAYMENT_ID.fieldName()).isEqualTo("paymentId");
        }

        @Test
        @DisplayName("PAYMENT_NUMBER의 fieldName은 paymentNumber이다")
        void paymentNumberFieldName() {
            assertThat(SettlementSearchField.PAYMENT_NUMBER.fieldName()).isEqualTo("paymentNumber");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("fieldName으로 검색 필드를 찾는다")
        void findByFieldName() {
            assertThat(SettlementSearchField.fromString("orderId"))
                    .isEqualTo(SettlementSearchField.ORDER_ID);
        }

        @Test
        @DisplayName("enum name으로 검색 필드를 찾는다")
        void findByEnumName() {
            assertThat(SettlementSearchField.fromString("ORDER_ID"))
                    .isEqualTo(SettlementSearchField.ORDER_ID);
        }

        @Test
        @DisplayName("대소문자 구분 없이 검색 필드를 찾는다")
        void findCaseInsensitive() {
            // fieldName "buyerName"과 대소문자 무관 매칭
            assertThat(SettlementSearchField.fromString("buyerName"))
                    .isEqualTo(SettlementSearchField.BUYER_NAME);
            assertThat(SettlementSearchField.fromString("BUYERNAME"))
                    .isEqualTo(SettlementSearchField.BUYER_NAME);
            // enum name "BUYER_NAME"과 대소문자 무관 매칭
            assertThat(SettlementSearchField.fromString("BUYER_NAME"))
                    .isEqualTo(SettlementSearchField.BUYER_NAME);
        }

        @Test
        @DisplayName("null 입력이면 null을 반환한다")
        void returnNullForNullInput() {
            assertThat(SettlementSearchField.fromString(null)).isNull();
        }

        @Test
        @DisplayName("빈 문자열 입력이면 null을 반환한다")
        void returnNullForBlankInput() {
            assertThat(SettlementSearchField.fromString("")).isNull();
            assertThat(SettlementSearchField.fromString("   ")).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 값이면 null을 반환한다")
        void returnNullForUnknownValue() {
            assertThat(SettlementSearchField.fromString("unknownField")).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("SettlementSearchField는 6가지 값이다")
        void searchFieldValues() {
            SettlementSearchField[] values = SettlementSearchField.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            SettlementSearchField.ORDER_ID,
                            SettlementSearchField.ORDER_NUMBER,
                            SettlementSearchField.PRODUCT_NAME,
                            SettlementSearchField.BUYER_NAME,
                            SettlementSearchField.PAYMENT_ID,
                            SettlementSearchField.PAYMENT_NUMBER);
        }
    }
}
