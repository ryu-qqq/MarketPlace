package com.ryuqq.marketplace.domain.canonicaloption.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionValueId Value Object 단위 테스트")
class CanonicalOptionValueIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 CanonicalOptionValueId를 생성한다")
        void createWithOf() {
            // when
            CanonicalOptionValueId id = CanonicalOptionValueId.of(123L);

            // then
            assertThat(id.value()).isEqualTo(123L);
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외를 발생시킨다")
        void ofWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 CanonicalOptionValueId는 동등하다")
        void sameValueEquals() {
            // given
            CanonicalOptionValueId id1 = CanonicalOptionValueId.of(100L);
            CanonicalOptionValueId id2 = CanonicalOptionValueId.of(100L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 CanonicalOptionValueId는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            CanonicalOptionValueId id1 = CanonicalOptionValueId.of(100L);
            CanonicalOptionValueId id2 = CanonicalOptionValueId.of(200L);

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
            CanonicalOptionValueId id = CanonicalOptionValueId.of(1L);

            // then
            assertThat(id.value()).isEqualTo(1L);
            // record는 final 클래스이므로 값 변경 불가능
        }
    }
}
