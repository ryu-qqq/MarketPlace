package com.ryuqq.marketplace.domain.order.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.DateField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderDateField 테스트")
class OrderDateFieldTest {

    @Nested
    @DisplayName("DateField 인터페이스 구현 테스트")
    class DateFieldInterfaceTest {

        @Test
        @DisplayName("DateField 인터페이스를 구현한다")
        void implementsDateField() {
            assertThat(OrderDateField.ORDERED).isInstanceOf(DateField.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("ORDERED의 fieldName은 orderedAt이다")
        void orderedFieldName() {
            assertThat(OrderDateField.ORDERED.fieldName()).isEqualTo("orderedAt");
        }

        @Test
        @DisplayName("SHIPPED의 fieldName은 shippedAt이다")
        void shippedFieldName() {
            assertThat(OrderDateField.SHIPPED.fieldName()).isEqualTo("shippedAt");
        }

        @Test
        @DisplayName("DELIVERED의 fieldName은 deliveredAt이다")
        void deliveredFieldName() {
            assertThat(OrderDateField.DELIVERED.fieldName()).isEqualTo("deliveredAt");
        }
    }

    @Nested
    @DisplayName("defaultField() 테스트")
    class DefaultFieldTest {

        @Test
        @DisplayName("기본 날짜 필드는 ORDERED이다")
        void defaultFieldIsOrdered() {
            assertThat(OrderDateField.defaultField()).isEqualTo(OrderDateField.ORDERED);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 OrderDateField 값이 정의되어 있다")
        void allValuesExist() {
            assertThat(OrderDateField.values())
                    .containsExactly(
                            OrderDateField.ORDERED,
                            OrderDateField.SHIPPED,
                            OrderDateField.DELIVERED);
        }
    }
}
