package com.ryuqq.marketplace.domain.shipment.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentId 테스트")
class ShipmentIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 UUIDv7 문자열로 ID를 생성한다")
        void createWithValidUuid() {
            // given
            String validId = "01944b2a-1234-7fff-8888-abcdef012345";

            // when
            ShipmentId id = ShipmentId.of(validId);

            // then
            assertThat(id.value()).isEqualTo(validId);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> ShipmentId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithEmpty_ThrowsException() {
            assertThatThrownBy(() -> ShipmentId.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> ShipmentId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew()는 of()와 동일하게 동작한다")
        void forNewBehavesLikeOf() {
            // given
            String validId = "01944b2a-1234-7fff-8888-abcdef012345";

            // when
            ShipmentId id = ShipmentId.forNew(validId);

            // then
            assertThat(id.value()).isEqualTo(validId);
        }

        @Test
        @DisplayName("forNew()에 null을 전달하면 예외가 발생한다")
        void forNewWithNull_ThrowsException() {
            assertThatThrownBy(() -> ShipmentId.forNew(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            String id = "01944b2a-1234-7fff-8888-abcdef012345";
            ShipmentId id1 = ShipmentId.of(id);
            ShipmentId id2 = ShipmentId.of(id);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            ShipmentId id1 = ShipmentId.of("01944b2a-1234-7fff-8888-abcdef012345");
            ShipmentId id2 = ShipmentId.of("01944b2a-5678-7fff-9999-abcdef012345");

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ShipmentId는 record이므로 불변이다")
        void shipmentIdIsImmutable() {
            // given
            String idValue = "01944b2a-1234-7fff-8888-abcdef012345";
            ShipmentId id = ShipmentId.of(idValue);

            // then
            assertThat(id.value()).isEqualTo(idValue);
        }
    }
}
