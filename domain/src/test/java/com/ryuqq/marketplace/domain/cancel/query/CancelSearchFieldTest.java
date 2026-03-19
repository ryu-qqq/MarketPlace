package com.ryuqq.marketplace.domain.cancel.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelSearchField 단위 테스트")
class CancelSearchFieldTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CANCEL_NUMBER의 fieldName은 cancelNumber이다")
        void cancelNumberFieldName() {
            assertThat(CancelSearchField.CANCEL_NUMBER.fieldName()).isEqualTo("cancelNumber");
        }

        @Test
        @DisplayName("ORDER_NUMBER의 fieldName은 orderNumber이다")
        void orderNumberFieldName() {
            assertThat(CancelSearchField.ORDER_NUMBER.fieldName()).isEqualTo("orderNumber");
        }

        @Test
        @DisplayName("CUSTOMER_NAME의 fieldName은 customerName이다")
        void customerNameFieldName() {
            assertThat(CancelSearchField.CUSTOMER_NAME.fieldName()).isEqualTo("customerName");
        }

        @Test
        @DisplayName("CUSTOMER_PHONE의 fieldName은 customerPhone이다")
        void customerPhoneFieldName() {
            assertThat(CancelSearchField.CUSTOMER_PHONE.fieldName()).isEqualTo("customerPhone");
        }

        @Test
        @DisplayName("PRODUCT_NAME의 fieldName은 productName이다")
        void productNameFieldName() {
            assertThat(CancelSearchField.PRODUCT_NAME.fieldName()).isEqualTo("productName");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("fieldName으로 CancelSearchField를 조회한다")
        void findByFieldName() {
            CancelSearchField field = CancelSearchField.fromString("cancelNumber");

            assertThat(field).isEqualTo(CancelSearchField.CANCEL_NUMBER);
        }

        @Test
        @DisplayName("enum name으로 CancelSearchField를 조회한다")
        void findByEnumName() {
            CancelSearchField field = CancelSearchField.fromString("CANCEL_NUMBER");

            assertThat(field).isEqualTo(CancelSearchField.CANCEL_NUMBER);
        }

        @Test
        @DisplayName("fieldName 대소문자 무시로 조회 - CANCELNUMBER는 cancelNumber와 매칭된다")
        void findByFieldNameCaseInsensitiveMatch() {
            CancelSearchField field = CancelSearchField.fromString("CANCELNUMBER");

            assertThat(field).isEqualTo(CancelSearchField.CANCEL_NUMBER);
        }

        @Test
        @DisplayName("완전히 다른 값이면 null을 반환한다")
        void returnNullForCompletelyDifferentValue() {
            CancelSearchField field = CancelSearchField.fromString("COMPLETELY_UNKNOWN_FIELD");

            assertThat(field).isNull();
        }

        @Test
        @DisplayName("enum name 소문자로 조회해도 결과를 반환한다")
        void findByLowerCaseEnumName() {
            CancelSearchField field = CancelSearchField.fromString("cancel_number");

            assertThat(field).isEqualTo(CancelSearchField.CANCEL_NUMBER);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void returnNullForNullInput() {
            CancelSearchField field = CancelSearchField.fromString(null);

            assertThat(field).isNull();
        }

        @Test
        @DisplayName("빈 문자열이면 null을 반환한다")
        void returnNullForBlankInput() {
            CancelSearchField field = CancelSearchField.fromString("");

            assertThat(field).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 값이면 null을 반환한다")
        void returnNullForUnknownValue() {
            CancelSearchField field = CancelSearchField.fromString("UNKNOWN_FIELD");

            assertThat(field).isNull();
        }

        @Test
        @DisplayName("orderNumber로 ORDER_NUMBER를 조회한다")
        void findOrderNumber() {
            CancelSearchField field = CancelSearchField.fromString("orderNumber");

            assertThat(field).isEqualTo(CancelSearchField.ORDER_NUMBER);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("CancelSearchField는 5가지 값이다")
        void searchFieldValues() {
            assertThat(CancelSearchField.values())
                    .containsExactlyInAnyOrder(
                            CancelSearchField.CANCEL_NUMBER,
                            CancelSearchField.ORDER_NUMBER,
                            CancelSearchField.CUSTOMER_NAME,
                            CancelSearchField.CUSTOMER_PHONE,
                            CancelSearchField.PRODUCT_NAME);
        }
    }
}
