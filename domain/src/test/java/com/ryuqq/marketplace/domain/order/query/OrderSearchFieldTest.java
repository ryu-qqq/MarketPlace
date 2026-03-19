package com.ryuqq.marketplace.domain.order.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.SearchField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderSearchField 테스트")
class OrderSearchFieldTest {

    @Nested
    @DisplayName("SearchField 인터페이스 구현 테스트")
    class SearchFieldInterfaceTest {

        @Test
        @DisplayName("SearchField 인터페이스를 구현한다")
        void implementsSearchField() {
            assertThat(OrderSearchField.ORDER_ID).isInstanceOf(SearchField.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("ORDER_ID의 fieldName은 orderId이다")
        void orderIdFieldName() {
            assertThat(OrderSearchField.ORDER_ID.fieldName()).isEqualTo("orderId");
        }

        @Test
        @DisplayName("ORDER_NUMBER의 fieldName은 orderNumber이다")
        void orderNumberFieldName() {
            assertThat(OrderSearchField.ORDER_NUMBER.fieldName()).isEqualTo("orderNumber");
        }

        @Test
        @DisplayName("CUSTOMER_NAME의 fieldName은 customerName이다")
        void customerNameFieldName() {
            assertThat(OrderSearchField.CUSTOMER_NAME.fieldName()).isEqualTo("customerName");
        }

        @Test
        @DisplayName("PRODUCT_NAME의 fieldName은 productName이다")
        void productNameFieldName() {
            assertThat(OrderSearchField.PRODUCT_NAME.fieldName()).isEqualTo("productName");
        }
    }

    @Nested
    @DisplayName("fromString() - 문자열로 검색 필드 조회")
    class FromStringTest {

        @Test
        @DisplayName("fieldName 문자열로 검색 필드를 조회한다")
        void fromStringByFieldName() {
            assertThat(OrderSearchField.fromString("orderNumber"))
                    .isEqualTo(OrderSearchField.ORDER_NUMBER);
        }

        @Test
        @DisplayName("enum name 문자열(대소문자 무시)로 검색 필드를 조회한다")
        void fromStringByEnumNameCaseInsensitive() {
            assertThat(OrderSearchField.fromString("order_number"))
                    .isEqualTo(OrderSearchField.ORDER_NUMBER);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void fromStringWithNull() {
            assertThat(OrderSearchField.fromString(null)).isNull();
        }

        @Test
        @DisplayName("빈 문자열이면 null을 반환한다")
        void fromStringWithBlank() {
            assertThat(OrderSearchField.fromString("  ")).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 값이면 null을 반환한다")
        void fromStringWithUnknownValue() {
            assertThat(OrderSearchField.fromString("unknownField")).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 OrderSearchField 값이 정의되어 있다")
        void allValuesExist() {
            assertThat(OrderSearchField.values())
                    .containsExactly(
                            OrderSearchField.ORDER_ID,
                            OrderSearchField.ORDER_NUMBER,
                            OrderSearchField.CUSTOMER_NAME,
                            OrderSearchField.PRODUCT_NAME);
        }
    }
}
