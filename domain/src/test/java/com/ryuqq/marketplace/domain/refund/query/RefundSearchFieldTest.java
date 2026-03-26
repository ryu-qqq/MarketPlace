package com.ryuqq.marketplace.domain.refund.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SearchField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundSearchField 단위 테스트")
class RefundSearchFieldTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CLAIM_NUMBER의 필드명은 claimNumber이다")
        void claimNumberHasCorrectFieldName() {
            assertThat(RefundSearchField.CLAIM_NUMBER.fieldName()).isEqualTo("claimNumber");
        }

        @Test
        @DisplayName("ORDER_NUMBER의 필드명은 orderNumber이다")
        void orderNumberHasCorrectFieldName() {
            assertThat(RefundSearchField.ORDER_NUMBER.fieldName()).isEqualTo("orderNumber");
        }

        @Test
        @DisplayName("CUSTOMER_NAME의 필드명은 customerName이다")
        void customerNameHasCorrectFieldName() {
            assertThat(RefundSearchField.CUSTOMER_NAME.fieldName()).isEqualTo("customerName");
        }

        @Test
        @DisplayName("CUSTOMER_PHONE의 필드명은 customerPhone이다")
        void customerPhoneHasCorrectFieldName() {
            assertThat(RefundSearchField.CUSTOMER_PHONE.fieldName()).isEqualTo("customerPhone");
        }

        @Test
        @DisplayName("PRODUCT_NAME의 필드명은 productName이다")
        void productNameHasCorrectFieldName() {
            assertThat(RefundSearchField.PRODUCT_NAME.fieldName()).isEqualTo("productName");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("fieldName으로 검색 필드를 찾는다")
        void findByFieldName() {
            assertThat(RefundSearchField.fromString("claimNumber"))
                    .isEqualTo(RefundSearchField.CLAIM_NUMBER);
        }

        @Test
        @DisplayName("name으로 검색 필드를 찾는다")
        void findByName() {
            assertThat(RefundSearchField.fromString("CLAIM_NUMBER"))
                    .isEqualTo(RefundSearchField.CLAIM_NUMBER);
        }

        @Test
        @DisplayName("대소문자 구분 없이 검색 필드를 찾는다")
        void findCaseInsensitive() {
            assertThat(RefundSearchField.fromString("CUSTOMERNAME"))
                    .isEqualTo(RefundSearchField.CUSTOMER_NAME);
            assertThat(RefundSearchField.fromString("customername"))
                    .isEqualTo(RefundSearchField.CUSTOMER_NAME);
        }

        @Test
        @DisplayName("null 값이면 null을 반환한다")
        void returnNullForNullValue() {
            assertThat(RefundSearchField.fromString(null)).isNull();
        }

        @Test
        @DisplayName("빈 문자열이면 null을 반환한다")
        void returnNullForBlankValue() {
            assertThat(RefundSearchField.fromString("")).isNull();
            assertThat(RefundSearchField.fromString("   ")).isNull();
        }

        @Test
        @DisplayName("알 수 없는 값이면 null을 반환한다")
        void returnNullForUnknownValue() {
            assertThat(RefundSearchField.fromString("UNKNOWN_FIELD")).isNull();
        }
    }

    @Nested
    @DisplayName("SearchField 인터페이스 구현 테스트")
    class InterfaceTest {

        @Test
        @DisplayName("SearchField 인터페이스를 구현한다")
        void implementsSearchField() {
            assertThat(RefundSearchField.CLAIM_NUMBER).isInstanceOf(SearchField.class);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("5개의 검색 필드가 정의되어 있다")
        void hasFiveSearchFields() {
            assertThat(RefundSearchField.values()).hasSize(5);
        }

        @Test
        @DisplayName("모든 검색 필드가 존재한다")
        void allSearchFieldsExist() {
            assertThat(RefundSearchField.values())
                    .containsExactly(
                            RefundSearchField.CLAIM_NUMBER,
                            RefundSearchField.ORDER_NUMBER,
                            RefundSearchField.CUSTOMER_NAME,
                            RefundSearchField.CUSTOMER_PHONE,
                            RefundSearchField.PRODUCT_NAME);
        }
    }
}
