package com.ryuqq.marketplace.domain.exchange.outbox.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeOutboxId Value Object 단위 테스트")
class ExchangeOutboxIdTest {

    @Nested
    @DisplayName("of() - 값으로 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 Long 값으로 ID를 생성한다")
        void createWithValidValue() {
            // when
            ExchangeOutboxId id = ExchangeOutboxId.of(1L);

            // then
            assertThat(id).isNotNull();
            assertThat(id.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            assertThatThrownBy(() -> ExchangeOutboxId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ExchangeOutboxId 값은 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew()로 생성하면 value가 null이다")
        void forNewHasNullValue() {
            // when
            ExchangeOutboxId id = ExchangeOutboxId.forNew();

            // then
            assertThat(id.value()).isNull();
        }

        @Test
        @DisplayName("forNew()로 생성하면 isNew()가 true이다")
        void forNewIsNew() {
            // when
            ExchangeOutboxId id = ExchangeOutboxId.forNew();

            // then
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("isNew() - 신규 여부 확인")
    class IsNewTest {

        @Test
        @DisplayName("value가 null이면 isNew()는 true이다")
        void isNewWhenValueIsNull() {
            ExchangeOutboxId id = ExchangeOutboxId.forNew();
            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("value가 존재하면 isNew()는 false이다")
        void isNotNewWhenValueExists() {
            ExchangeOutboxId id = ExchangeOutboxId.of(100L);
            assertThat(id.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 value를 가진 ID는 동일하다")
        void sameValueAreEqual() {
            ExchangeOutboxId id1 = ExchangeOutboxId.of(1L);
            ExchangeOutboxId id2 = ExchangeOutboxId.of(1L);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 value를 가진 ID는 동일하지 않다")
        void differentValuesAreNotEqual() {
            ExchangeOutboxId id1 = ExchangeOutboxId.of(1L);
            ExchangeOutboxId id2 = ExchangeOutboxId.of(2L);

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
