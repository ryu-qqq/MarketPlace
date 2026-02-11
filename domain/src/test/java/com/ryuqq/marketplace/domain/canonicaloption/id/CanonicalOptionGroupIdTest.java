package com.ryuqq.marketplace.domain.canonicaloption.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionGroupId Value Object 단위 테스트")
class CanonicalOptionGroupIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 CanonicalOptionGroupId를 생성한다")
        void createWithOf() {
            // when
            CanonicalOptionGroupId id = CanonicalOptionGroupId.of(123L);

            // then
            assertThat(id.value()).isEqualTo(123L);
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외를 발생시킨다")
        void ofWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 CanonicalOptionGroupId는 동등하다")
        void sameValueEquals() {
            // given
            CanonicalOptionGroupId id1 = CanonicalOptionGroupId.of(100L);
            CanonicalOptionGroupId id2 = CanonicalOptionGroupId.of(100L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 CanonicalOptionGroupId는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            CanonicalOptionGroupId id1 = CanonicalOptionGroupId.of(100L);
            CanonicalOptionGroupId id2 = CanonicalOptionGroupId.of(200L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("record 타입이므로 불변이다")
        void isImmutable() {
            // given
            CanonicalOptionGroupId id = CanonicalOptionGroupId.of(1L);

            // then
            assertThat(id.value()).isEqualTo(1L);
            // record는 final 클래스이므로 값 변경 불가능
        }
    }
}
