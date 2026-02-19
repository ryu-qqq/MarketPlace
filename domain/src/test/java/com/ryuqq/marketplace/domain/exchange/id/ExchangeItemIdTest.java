package com.ryuqq.marketplace.domain.exchange.id;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeItemId Value Object 단위 테스트")
class ExchangeItemIdTest {

    @Nested
    @DisplayName("of() - 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("유효한 long 값으로 생성한다")
        void createWithValidLongValue() {
            // when
            ExchangeItemId id = ExchangeItemId.of(1L);

            // then
            assertThat(id).isNotNull();
            assertThat(id.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("큰 long 값으로도 생성할 수 있다")
        void createWithLargeLongValue() {
            // when
            ExchangeItemId id = ExchangeItemId.of(Long.MAX_VALUE);

            // then
            assertThat(id.value()).isEqualTo(Long.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 생성 테스트")
    class ForNewTest {

        @Test
        @DisplayName("영속화 전 신규 ID는 null 값을 가진다")
        void newIdHasNullValue() {
            // when
            ExchangeItemId id = ExchangeItemId.forNew();

            // then
            assertThat(id.value()).isNull();
        }

        @Test
        @DisplayName("forNew 호출 시 ExchangeItemId 인스턴스가 생성된다")
        void forNewCreatesInstance() {
            // when
            ExchangeItemId id = ExchangeItemId.forNew();

            // then
            assertThat(id).isNotNull();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            ExchangeItemId id1 = ExchangeItemId.of(10L);
            ExchangeItemId id2 = ExchangeItemId.of(10L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            ExchangeItemId id1 = ExchangeItemId.of(1L);
            ExchangeItemId id2 = ExchangeItemId.of(2L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("forNew로 생성한 두 ID는 동일하다")
        void twoForNewIdsAreEqual() {
            // given (null 값을 가지는 두 인스턴스)
            ExchangeItemId id1 = ExchangeItemId.forNew();
            ExchangeItemId id2 = ExchangeItemId.forNew();

            // then
            assertThat(id1).isEqualTo(id2);
        }
    }
}
