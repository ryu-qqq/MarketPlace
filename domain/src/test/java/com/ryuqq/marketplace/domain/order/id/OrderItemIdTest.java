package com.ryuqq.marketplace.domain.order.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderItemId Value Object 테스트")
class OrderItemIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 Long 값으로 OrderItemId를 생성한다")
        void createWithValidValue() {
            // given
            Long value = 1001L;

            // when
            OrderItemId id = OrderItemId.of(value);

            // then
            assertThat(id.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderItemId.of((Long) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("of(String) - 문자열에서 ID 생성")
    class OfStringTest {

        @Test
        @DisplayName("유효한 숫자 문자열로 OrderItemId를 생성한다")
        void createWithValidStringValue() {
            // when
            OrderItemId id = OrderItemId.of("1001");

            // then
            assertThat(id.value()).isEqualTo(1001L);
        }

        @Test
        @DisplayName("null 문자열이면 예외가 발생한다")
        void createWithNullString_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderItemId.of((String) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderItemId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew()는 전달한 값으로 OrderItemId를 생성한다")
        void forNewReturnsNullId() {
            // when
            OrderItemId id = OrderItemId.forNew();

            // then
            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 OrderItemId는 동일하다")
        void sameValueAreEqual() {
            // given
            Long value = 1001L;

            // when
            OrderItemId id1 = OrderItemId.of(value);
            OrderItemId id2 = OrderItemId.of(value);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 OrderItemId는 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            OrderItemId id1 = OrderItemId.of(1001L);
            OrderItemId id2 = OrderItemId.of(1002L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
