package com.ryuqq.marketplace.domain.shipment.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentSearchField 단위 테스트")
class ShipmentSearchFieldTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("ORDER_ITEM_ID의 fieldName은 orderItemId이다")
        void orderItemIdFieldName() {
            assertThat(ShipmentSearchField.ORDER_ITEM_ID.fieldName()).isEqualTo("orderItemId");
        }

        @Test
        @DisplayName("TRACKING_NUMBER의 fieldName은 trackingNumber이다")
        void trackingNumberFieldName() {
            assertThat(ShipmentSearchField.TRACKING_NUMBER.fieldName()).isEqualTo("trackingNumber");
        }

        @Test
        @DisplayName("CUSTOMER_NAME의 fieldName은 customerName이다")
        void customerNameFieldName() {
            assertThat(ShipmentSearchField.CUSTOMER_NAME.fieldName()).isEqualTo("customerName");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("fieldName으로 검색 필드를 찾는다")
        void findByFieldName() {
            assertThat(ShipmentSearchField.fromString("orderItemId"))
                    .isEqualTo(ShipmentSearchField.ORDER_ITEM_ID);
        }

        @Test
        @DisplayName("enum name으로 검색 필드를 찾는다")
        void findByEnumName() {
            assertThat(ShipmentSearchField.fromString("ORDER_ITEM_ID"))
                    .isEqualTo(ShipmentSearchField.ORDER_ITEM_ID);
        }

        @Test
        @DisplayName("trackingNumber fieldName으로 검색 필드를 찾는다")
        void findTrackingNumberByFieldName() {
            assertThat(ShipmentSearchField.fromString("trackingNumber"))
                    .isEqualTo(ShipmentSearchField.TRACKING_NUMBER);
        }

        @Test
        @DisplayName("customerName fieldName으로 검색 필드를 찾는다")
        void findCustomerNameByFieldName() {
            assertThat(ShipmentSearchField.fromString("customerName"))
                    .isEqualTo(ShipmentSearchField.CUSTOMER_NAME);
        }

        @Test
        @DisplayName("대소문자 구분 없이 검색 필드를 찾는다")
        void findCaseInsensitive() {
            assertThat(ShipmentSearchField.fromString("TRACKING_NUMBER"))
                    .isEqualTo(ShipmentSearchField.TRACKING_NUMBER);
            assertThat(ShipmentSearchField.fromString("TRACKINGNO")).isNull();
        }

        @Test
        @DisplayName("null 입력이면 null을 반환한다")
        void returnNullForNullInput() {
            assertThat(ShipmentSearchField.fromString(null)).isNull();
        }

        @Test
        @DisplayName("빈 문자열 입력이면 null을 반환한다")
        void returnNullForBlankInput() {
            assertThat(ShipmentSearchField.fromString("")).isNull();
            assertThat(ShipmentSearchField.fromString("   ")).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 값이면 null을 반환한다")
        void returnNullForUnknownValue() {
            assertThat(ShipmentSearchField.fromString("unknownField")).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("ShipmentSearchField는 3가지 값이다")
        void searchFieldValues() {
            assertThat(ShipmentSearchField.values())
                    .containsExactlyInAnyOrder(
                            ShipmentSearchField.ORDER_ITEM_ID,
                            ShipmentSearchField.TRACKING_NUMBER,
                            ShipmentSearchField.CUSTOMER_NAME);
        }
    }
}
