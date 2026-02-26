package com.ryuqq.marketplace.domain.claim.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimShipmentId Value Object 단위 테스트")
class ClaimShipmentIdTest {

    @Nested
    @DisplayName("of() - 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 ClaimShipmentId를 생성한다")
        void createWithValidValue() {
            // given & when
            ClaimShipmentId id = ClaimShipmentId.of("CLAIM-SHIP-0001");

            // then
            assertThat(id.value()).isEqualTo("CLAIM-SHIP-0001");
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            assertThatThrownBy(() -> ClaimShipmentId.of(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void createWithEmptyValue_ThrowsException() {
            assertThatThrownBy(() -> ClaimShipmentId.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열로 생성하면 예외가 발생한다")
        void createWithBlankValue_ThrowsException() {
            assertThatThrownBy(() -> ClaimShipmentId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 생성 테스트")
    class ForNewTest {

        @Test
        @DisplayName("forNew로 유효한 값을 전달하면 ClaimShipmentId를 생성한다")
        void createWithForNew() {
            // given & when
            ClaimShipmentId id = ClaimShipmentId.forNew("CLAIM-SHIP-0002");

            // then
            assertThat(id.value()).isEqualTo("CLAIM-SHIP-0002");
        }

        @Test
        @DisplayName("forNew에 null을 전달하면 예외가 발생한다")
        void forNewWithNullValue_ThrowsException() {
            assertThatThrownBy(() -> ClaimShipmentId.forNew(null))
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
            ClaimShipmentId id1 = ClaimShipmentId.of("CLAIM-SHIP-0001");
            ClaimShipmentId id2 = ClaimShipmentId.of("CLAIM-SHIP-0001");

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValueAreNotEqual() {
            // given
            ClaimShipmentId id1 = ClaimShipmentId.of("CLAIM-SHIP-0001");
            ClaimShipmentId id2 = ClaimShipmentId.of("CLAIM-SHIP-0002");

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
