package com.ryuqq.marketplace.domain.shipment.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentSortKey 단위 테스트")
class ShipmentSortKeyTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 fieldName은 createdAt이다")
        void createdAtFieldName() {
            assertThat(ShipmentSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("SHIPPED_AT의 fieldName은 shippedAt이다")
        void shippedAtFieldName() {
            assertThat(ShipmentSortKey.SHIPPED_AT.fieldName()).isEqualTo("shippedAt");
        }

        @Test
        @DisplayName("DELIVERED_AT의 fieldName은 deliveredAt이다")
        void deliveredAtFieldName() {
            assertThat(ShipmentSortKey.DELIVERED_AT.fieldName()).isEqualTo("deliveredAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(ShipmentSortKey.defaultKey()).isEqualTo(ShipmentSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("ShipmentSortKey는 3가지 값이다")
        void sortKeyValues() {
            assertThat(ShipmentSortKey.values())
                    .containsExactlyInAnyOrder(
                            ShipmentSortKey.CREATED_AT,
                            ShipmentSortKey.SHIPPED_AT,
                            ShipmentSortKey.DELIVERED_AT);
        }
    }
}
