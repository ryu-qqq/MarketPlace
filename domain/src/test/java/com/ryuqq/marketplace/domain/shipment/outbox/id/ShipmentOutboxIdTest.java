package com.ryuqq.marketplace.domain.shipment.outbox.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentOutboxId Value Object 단위 테스트")
class ShipmentOutboxIdTest {

    @Nested
    @DisplayName("of() - 값으로 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 Long 값으로 ShipmentOutboxId를 생성한다")
        void createWithValidLongValue() {
            ShipmentOutboxId id = ShipmentOutboxId.of(1L);

            assertThat(id.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            assertThatThrownBy(() -> ShipmentOutboxId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("큰 Long 값으로도 생성할 수 있다")
        void createWithLargeLongValue() {
            ShipmentOutboxId id = ShipmentOutboxId.of(Long.MAX_VALUE);

            assertThat(id.value()).isEqualTo(Long.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew()로 생성한 ID는 null 값을 가진다")
        void forNewHasNullValue() {
            ShipmentOutboxId id = ShipmentOutboxId.forNew();

            assertThat(id.value()).isNull();
        }

        @Test
        @DisplayName("forNew()로 생성한 ID는 isNew()가 true이다")
        void forNewIsNew() {
            ShipmentOutboxId id = ShipmentOutboxId.forNew();

            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("isNew() 테스트")
    class IsNewTest {

        @Test
        @DisplayName("값이 있는 ID는 isNew()가 false이다")
        void withValueIsNotNew() {
            ShipmentOutboxId id = ShipmentOutboxId.of(1L);

            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("forNew()로 생성한 ID는 isNew()가 true이다")
        void forNewIdIsNew() {
            ShipmentOutboxId id = ShipmentOutboxId.forNew();

            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            ShipmentOutboxId id1 = ShipmentOutboxId.of(1L);
            ShipmentOutboxId id2 = ShipmentOutboxId.of(1L);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 다르다")
        void differentValuesAreNotEqual() {
            ShipmentOutboxId id1 = ShipmentOutboxId.of(1L);
            ShipmentOutboxId id2 = ShipmentOutboxId.of(2L);

            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("두 forNew() ID는 둘 다 null 값이므로 동일하다")
        void twoForNewIdsAreEqual() {
            ShipmentOutboxId id1 = ShipmentOutboxId.forNew();
            ShipmentOutboxId id2 = ShipmentOutboxId.forNew();

            assertThat(id1).isEqualTo(id2);
        }
    }
}
