package com.ryuqq.marketplace.domain.order.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderSortKey 테스트")
class OrderSortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            assertThat(OrderSortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 fieldName은 createdAt이다")
        void createdAtFieldName() {
            assertThat(OrderSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("ORDERED_AT의 fieldName은 orderedAt이다")
        void orderedAtFieldName() {
            assertThat(OrderSortKey.ORDERED_AT.fieldName()).isEqualTo("orderedAt");
        }

        @Test
        @DisplayName("UPDATED_AT의 fieldName은 updatedAt이다")
        void updatedAtFieldName() {
            assertThat(OrderSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(OrderSortKey.defaultKey()).isEqualTo(OrderSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 OrderSortKey 값이 정의되어 있다")
        void allValuesExist() {
            assertThat(OrderSortKey.values())
                    .containsExactly(
                            OrderSortKey.CREATED_AT,
                            OrderSortKey.ORDERED_AT,
                            OrderSortKey.UPDATED_AT);
        }
    }
}
