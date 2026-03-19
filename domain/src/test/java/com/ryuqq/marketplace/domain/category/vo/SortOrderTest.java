package com.ryuqq.marketplace.domain.category.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("unit")
@DisplayName("SortOrder Value Object 단위 테스트")
class SortOrderTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("0으로 생성한다")
        void createWithZero() {
            SortOrder order = SortOrder.of(0);

            assertThat(order.value()).isZero();
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 100, 999})
        @DisplayName("0 이상의 값으로 생성한다")
        void createWithNonNegativeValue(int value) {
            SortOrder order = SortOrder.of(value);

            assertThat(order.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("음수이면 예외가 발생한다")
        void createWithNegative_ThrowsException() {
            assertThatThrownBy(() -> SortOrder.of(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0 이상");
        }
    }

    @Nested
    @DisplayName("defaultOrder() 팩토리 메서드")
    class DefaultOrderTest {

        @Test
        @DisplayName("defaultOrder()는 0을 반환한다")
        void defaultOrderReturnsZero() {
            SortOrder order = SortOrder.defaultOrder();

            assertThat(order.value()).isZero();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 순서는 동일하다")
        void sameOrderAreEqual() {
            SortOrder order1 = SortOrder.of(5);
            SortOrder order2 = SortOrder.of(5);

            assertThat(order1).isEqualTo(order2);
            assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
        }

        @Test
        @DisplayName("다른 순서는 동일하지 않다")
        void differentOrderAreNotEqual() {
            SortOrder order1 = SortOrder.of(1);
            SortOrder order2 = SortOrder.of(2);

            assertThat(order1).isNotEqualTo(order2);
        }
    }
}
