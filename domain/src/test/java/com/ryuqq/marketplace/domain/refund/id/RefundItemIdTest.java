package com.ryuqq.marketplace.domain.refund.id;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundItemId Value Object 단위 테스트")
class RefundItemIdTest {

    @Nested
    @DisplayName("of() - 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("유효한 Long 값으로 ID를 생성한다")
        void createWithValidValue() {
            // when
            RefundItemId id = RefundItemId.of(1L);

            // then
            assertThat(id.value()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 생성 테스트")
    class ForNewTest {

        @Test
        @DisplayName("신규 생성 시 ID 값은 null이다")
        void forNewHasNullValue() {
            // when
            RefundItemId id = RefundItemId.forNew();

            // then
            assertThat(id.value()).isNull();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            RefundItemId id1 = RefundItemId.of(10L);
            RefundItemId id2 = RefundItemId.of(10L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            RefundItemId id1 = RefundItemId.of(10L);
            RefundItemId id2 = RefundItemId.of(20L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("forNew()로 생성된 두 ID는 둘 다 null 값으로 동일하다")
        void twoForNewIdsHaveEqualNullValues() {
            // given
            RefundItemId id1 = RefundItemId.forNew();
            RefundItemId id2 = RefundItemId.forNew();

            // then
            assertThat(id1).isEqualTo(id2);
        }
    }
}
