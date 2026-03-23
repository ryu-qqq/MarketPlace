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
        @DisplayName("ORDER_ID의 fieldName은 orderItemNumber이다")
        void orderIdFieldName() {
            assertThat(OrderSearchField.ORDER_ID.fieldName()).isEqualTo("orderItemNumber");
        }

        @Test
        @DisplayName("PAYMENT_ID의 fieldName은 paymentNumber이다")
        void paymentIdFieldName() {
            assertThat(OrderSearchField.PAYMENT_ID.fieldName()).isEqualTo("paymentNumber");
        }

        @Test
        @DisplayName("PRODUCT_GROUP_ID의 fieldName은 productGroupId이다")
        void productGroupIdFieldName() {
            assertThat(OrderSearchField.PRODUCT_GROUP_ID.fieldName()).isEqualTo("productGroupId");
        }

        @Test
        @DisplayName("BUYER_NAME의 fieldName은 buyerName이다")
        void buyerNameFieldName() {
            assertThat(OrderSearchField.BUYER_NAME.fieldName()).isEqualTo("buyerName");
        }
    }

    @Nested
    @DisplayName("fromString() - 문자열로 검색 필드 조회")
    class FromStringTest {

        @Test
        @DisplayName("enum name 문자열로 검색 필드를 조회한다")
        void fromStringByEnumName() {
            assertThat(OrderSearchField.fromString("ORDER_ID"))
                    .isEqualTo(OrderSearchField.ORDER_ID);
        }

        @Test
        @DisplayName("enum name 문자열(대소문자 무시)로 검색 필드를 조회한다")
        void fromStringByEnumNameCaseInsensitive() {
            assertThat(OrderSearchField.fromString("buyer_name"))
                    .isEqualTo(OrderSearchField.BUYER_NAME);
        }

        @Test
        @DisplayName("fieldName 문자열로 검색 필드를 조회한다")
        void fromStringByFieldName() {
            assertThat(OrderSearchField.fromString("buyerName"))
                    .isEqualTo(OrderSearchField.BUYER_NAME);
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
                            OrderSearchField.PAYMENT_ID,
                            OrderSearchField.PRODUCT_GROUP_ID,
                            OrderSearchField.BUYER_NAME);
        }
    }
}
